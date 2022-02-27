package de.quadrathelden.ostereier.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.shop.ConfigShopOffer;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.economy.EconomyProvider;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.scoreboard.ScoreboardManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class ShopManager {

	public static final String NAMESPACE_OWNER = "ostereiOwner";
	public static final String NAMESPACE_OFFER = "ostereiOffer";
	public static final String NAMESPACE_AMOUNT = "ostereiAmount";
	public static final String NAMESPACE_PRICE = "ostereiPrice";
	public static final String NAMESPACE_CURRENCY = "ostereiCurrency";

	public static final String TEXT_TITLE = "shopTitle";
	public static final String TEXT_SELL_AMOUNT = "shopSellAmount";
	public static final String TEXT_FINANCIAL_ASSETS = "shopFinancialAssets";
	public static final String CURRENCY_FORMAT = "%d %s";
	public static final String AMOUNT_FORMAT = "%s: %d";
	public static final String UNIT_FORMAT = "%d/%d";

	protected NamespacedKey ownerNamespacedKey = null;
	protected NamespacedKey offerNamespacedKey = null;
	protected NamespacedKey amountNamespacedKey = null;
	protected NamespacedKey priceNamespacedKey = null;
	protected NamespacedKey currencyNamespacedKey = null;

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final EventManager eventManager;
	protected final EconomyManager economyManager;
	protected final ScoreboardManager scoreboardManager;

	protected ShopListener shopListener;
	protected Map<Inventory, InventoryView> protectedInventories = new HashMap<>();

	public ShopManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
		this.permissionManager = orchestrator.getPermissionManager();
		this.eventManager = orchestrator.getEventManager();
		this.economyManager = orchestrator.getEconomyManager();
		this.scoreboardManager = orchestrator.getScoreboardManager();
		ownerNamespacedKey = new NamespacedKey(plugin, NAMESPACE_OWNER);
		offerNamespacedKey = new NamespacedKey(plugin, NAMESPACE_OFFER);
		amountNamespacedKey = new NamespacedKey(plugin, NAMESPACE_AMOUNT);
		priceNamespacedKey = new NamespacedKey(plugin, NAMESPACE_PRICE);
		currencyNamespacedKey = new NamespacedKey(plugin, NAMESPACE_CURRENCY);
		shopListener = new ShopListener(plugin, textManager, configManager, scoreboardManager, this);
		shopListener.enableListener();
	}

	public ItemSeal readItemSeal(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return null;
		}

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		String owner = "";
		if (container.has(ownerNamespacedKey, PersistentDataType.STRING)) {
			owner = container.get(ownerNamespacedKey, PersistentDataType.STRING);
		}

		String offer = "";
		if (container.has(offerNamespacedKey, PersistentDataType.STRING)) {
			offer = container.get(offerNamespacedKey, PersistentDataType.STRING);
		}

		int amount = 0;
		if (container.has(amountNamespacedKey, PersistentDataType.STRING)) {
			amount = Integer.valueOf(container.get(amountNamespacedKey, PersistentDataType.STRING));
		}

		int price = 0;
		if (container.has(priceNamespacedKey, PersistentDataType.STRING)) {
			price = Integer.valueOf(container.get(priceNamespacedKey, PersistentDataType.STRING));
		}

		String currency = "";
		if (container.has(currencyNamespacedKey, PersistentDataType.STRING)) {
			currency = container.get(currencyNamespacedKey, PersistentDataType.STRING);
		}
		if (currency.isEmpty()) {
			currency = configManager.getConfigEconomy().getDefaultRewardCurrencyName();
		}

		if (amount == 0) {
			return null;
		}

		return new ItemSeal(owner, offer, amount, price, currency);
	}

	public void writeItemSeal(ItemStack itemStack, ItemSeal itemSeal) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		container.set(ownerNamespacedKey, PersistentDataType.STRING, itemSeal.owner());
		container.set(offerNamespacedKey, PersistentDataType.STRING, itemSeal.offer());
		container.set(amountNamespacedKey, PersistentDataType.STRING, Integer.toString(itemSeal.amount()));
		container.set(priceNamespacedKey, PersistentDataType.STRING, Integer.toString(itemSeal.price()));
		container.set(currencyNamespacedKey, PersistentDataType.STRING, itemSeal.currency());
		itemStack.setItemMeta(itemMeta);
	}

	public void localizeSealedItemStack(ItemStack itemStack, CommandSender sender) {
		ItemSeal itemSeal = readItemSeal(itemStack);
		if (itemSeal == null) {
			return;
		}

		String offerId = itemSeal.offer();
		ConfigShopOffer offer = configManager.findShopOffer(offerId);
		if (offer == null) {
			offer = eventManager.sendShopFindOfferEvent(offerId);
			if (offer == null) {
				return;
			}
		}

		ItemMeta itemMeta = itemStack.getItemMeta();
		String offerName = offer.getName(sender);
		if ((offerName != null) && !offerName.isEmpty()) {
			itemMeta.setDisplayName(offerName);
		} else {
			itemMeta.setDisplayName(offerId);
		}

		List<String> lore = new ArrayList<>();
		lore.addAll(offer.getDescription(sender));
		int amount = itemSeal.amount();
		if (amount > 1) {
			String sellAmountText = textManager.findText(TEXT_SELL_AMOUNT, sender);
			lore.add(String.format(AMOUNT_FORMAT, sellAmountText, amount));
		}
		int price = itemSeal.price();
		if (price > 0) {
			String currencyName = itemSeal.currency();
			String currencyDisplay = configManager.findCurrency(price, currencyName, sender);
			lore.add(String.format(CURRENCY_FORMAT, price, currencyDisplay));
		}
		if (!lore.isEmpty()) {
			itemMeta.setLore(lore);
		}

		itemStack.setItemMeta(itemMeta);
	}

	protected void enchantOffer(ItemStack itemStack, ConfigShopOffer offer) {
		if (offer.getEnchantmentType() != null) {
			itemStack.addEnchantment(offer.getEnchantmentType(), offer.getEnchantmentLevel());
		}
	}

	public ItemStack buildItemStack(Player player, ConfigShopOffer offer) {
		ItemStack itemStack = new ItemStack(offer.getMaterial(), offer.getAmount());
		ItemSeal itemSeal = ItemSeal.fromOffer(player, offer);
		enchantOffer(itemStack, offer);
		writeItemSeal(itemStack, itemSeal);
		localizeSealedItemStack(itemStack, player);
		return itemStack;
	}

	public ItemSeal getValidatedItemStackSeal(ItemStack itemStack) throws OstereierException {
		ItemSeal itemSeal = readItemSeal(itemStack);
		if (itemSeal == null) {
			throw new OstereierException(null, Message.SHOP_ITEM_SIGNATURE_MISSING, itemStack.getType().toString());
		}
		return itemSeal;
	}

	public boolean canPlayerAffordItem(Player player, ItemStack itemStack) throws OstereierException {
		ItemSeal itemSeal = getValidatedItemStackSeal(itemStack);
		String currency = itemSeal.currency();
		int price = itemSeal.price();
		EconomyProvider economyProvider = economyManager.getEconomyProvider();
		int playerAmount = economyProvider.getPoints(player, currency);
		return (playerAmount >= price);
	}

	public void chargePlayerForItem(Player player, ItemStack itemStack) throws OstereierException {
		ItemSeal itemSeal = getValidatedItemStackSeal(itemStack);
		String currency = itemSeal.currency();
		int price = itemSeal.price();
		EconomyProvider economyProvider = economyManager.getEconomyProvider();
		int playerAmount = economyProvider.getPoints(player, currency);
		if (playerAmount < price) {
			String s = String.format(CURRENCY_FORMAT, price, configManager.findCurrency(price, currency, player));
			throw new OstereierException(null, Message.SHOP_PLAYER_NOT_ENOUGH_MONEY, s);
		}
		economyProvider.removePoints(player, price, currency);
	}

	public void refundPlayerForItemByOneUnit(Player player, ItemStack itemStack) throws OstereierException {
		ItemSeal itemSeal = getValidatedItemStackSeal(itemStack);
		if (itemStack.getAmount() < itemSeal.amount()) {
			String s = String.format(UNIT_FORMAT, itemStack.getAmount(), itemSeal.amount());
			throw new OstereierException(null, Message.SHOP_PLAYER_NOT_ENOUGH_ITEMS, s);
		}
		String currency = itemSeal.currency();
		int price = itemSeal.price();
		EconomyProvider economyProvider = economyManager.getEconomyProvider();
		economyProvider.addPoints(player, price, currency);
	}

	protected boolean isStackable(ItemStack itemStack1, ItemStack itemStack2) {
		if ((itemStack1 == null) || (itemStack2 == null)) {
			return false;
		}
		if (!itemStack1.getType().equals(itemStack2.getType())) {
			return false;
		}
		if ((itemStack1.getMaxStackSize() == 1) || (itemStack2.getMaxStackSize() == 1)) {
			return false;
		}
		ItemSeal itemSeal1 = readItemSeal(itemStack1);
		ItemSeal itemSeal2 = readItemSeal(itemStack2);
		return ((itemSeal1 != null) && (itemSeal2 != null) && itemSeal1.equals(itemSeal2));
	}

	public void putSealedItemStackIntoPlayerInventory(Player player, ItemStack itemStack) throws OstereierException {
		int addAmount = itemStack.getAmount();
		Inventory playerInventory = player.getInventory();
		for (ItemStack myItemStack : playerInventory.getStorageContents()) {
			if (isStackable(itemStack, myItemStack)) {
				int newAmount = myItemStack.getAmount() + addAmount;
				if ((newAmount <= myItemStack.getMaxStackSize()) && (newAmount <= playerInventory.getMaxStackSize())) {
					myItemStack.setAmount(newAmount);
					return;
				}
			}
		}
		int freeItemPosition = playerInventory.firstEmpty();
		if (freeItemPosition < 0) {
			throw new OstereierException(null, Message.SHOP_PLAYER_INVENTORY_FULL, player.getName());
		}
		playerInventory.setItem(freeItemPosition, itemStack);
	}

	public void removeOneUnitOfSealedItemStackFromPlayerInventory(Player player, int itemPosition)
			throws OstereierException {
		Inventory playerInventory = player.getInventory();
		ItemStack itemStack = playerInventory.getItem(itemPosition);
		ItemSeal itemSeal = getValidatedItemStackSeal(itemStack);
		int newAmount = itemStack.getAmount() - itemSeal.amount();
		if (newAmount < 0) {
			String s = String.format(UNIT_FORMAT, itemStack.getAmount(), itemSeal.amount());
			throw new OstereierException(null, Message.SHOP_PLAYER_NOT_ENOUGH_ITEMS, s);
		}
		if (newAmount == 0) {
			playerInventory.clear(itemPosition);
		} else {
			itemStack.setAmount(newAmount);
		}
	}

	public void buyItem(Player player, ItemStack templateItemStack) throws OstereierException {
		if (!canPlayerAffordItem(player, templateItemStack)) {
			ItemSeal itemSeal = readItemSeal(templateItemStack);
			String currency = configManager.findCurrency(itemSeal.price(), itemSeal.currency(), player);
			String s = String.format(CURRENCY_FORMAT, itemSeal.price(), currency);
			throw new OstereierException(null, Message.SHOP_PLAYER_NOT_ENOUGH_MONEY, s);
		}
		ItemSeal templateItemSeal = getValidatedItemStackSeal(templateItemStack);
		ItemStack newItemStack = templateItemStack.clone();
		ItemSeal newItemSeal = templateItemSeal.changeOwner(player);
		writeItemSeal(newItemStack, newItemSeal);
		localizeSealedItemStack(newItemStack, player);
		newItemStack.setAmount(newItemSeal.amount());
		if (eventManager.sendPlayerBuyItemEvent(player, newItemStack)) {
			putSealedItemStackIntoPlayerInventory(player, newItemStack);
			chargePlayerForItem(player, newItemStack);
		}
		player.updateInventory();
	}

	public void sellItem(Player player, int itemPosition) throws OstereierException {
		ItemStack oldItemStack = player.getInventory().getItem(itemPosition);
		if (eventManager.sendPlayerSellItemEvent(player, oldItemStack)) {
			refundPlayerForItemByOneUnit(player, oldItemStack);
			removeOneUnitOfSealedItemStackFromPlayerInventory(player, itemPosition);
		}
		player.updateInventory();
	}

	public Set<String> getUsedCurrencies(Inventory inventory) {
		Set<String> currencies = new TreeSet<>();
		for (ItemStack myItemStack : inventory.getStorageContents()) {
			ItemSeal myItemSeal = readItemSeal(myItemStack);
			if (myItemSeal != null) {
				currencies.add(myItemSeal.currency());
			}
		}
		return currencies;
	}

	public List<String> buildPlayerCurrencySheet(Player player, Inventory inventory) throws OstereierException {
		List<String> currencySheet = new ArrayList<>();
		Set<String> currencyNames = new TreeSet<>();
		currencyNames.addAll(getUsedCurrencies(player.getInventory()));
		if (inventory != null) {
			currencyNames.addAll(getUsedCurrencies(inventory));
		}
		EconomyProvider economyProvider = economyManager.getEconomyProvider();
		for (String myCurrencyName : currencyNames) {
			int amount = economyProvider.getPoints(player, myCurrencyName);
			if (amount > 0) {
				String myCurrencyDisplay = configManager.findCurrency(amount, myCurrencyName, player);
				currencySheet.add(String.format(CURRENCY_FORMAT, amount, myCurrencyDisplay));
			}
		}
		return currencySheet;
	}

	public void updatePlayerCurrencySheet(Player player, Inventory inventory) throws OstereierException {
		ItemStack itemStack = inventory.getItem(inventory.getSize() - 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(textManager.findText(TEXT_FINANCIAL_ASSETS, player));
		itemMeta.setLore(buildPlayerCurrencySheet(player, inventory));
		itemStack.setItemMeta(itemMeta);
	}

	public Inventory openShopGui(Player player) throws OstereierException {
		if (!permissionManager.hasShopPermission(player)) {
			throw new OstereierException(Message.SHOP_PLAYER_FORBIDDEN);
		}
		int guiSize = configManager.getShopOffers().size() + 1;
		while (guiSize % 9 > 0) {
			guiSize = guiSize + 1;
		}
		String title = textManager.findText(TEXT_TITLE, player);
		Inventory newInventory = Bukkit.createInventory(null, guiSize, title);
		for (ConfigShopOffer myOffer : configManager.getShopOffers()) {
			ItemStack myItemStack = buildItemStack(player, myOffer);
			newInventory.addItem(myItemStack);
		}

		ItemStack playerCurrencySheet = new ItemStack(Material.EGG, 1);
		newInventory.setItem(newInventory.getSize() - 1, playerCurrencySheet);
		updatePlayerCurrencySheet(player, newInventory);

		newInventory = eventManager.sendPlayerOpenShopEvent(player, newInventory);
		if (newInventory == null) {
			return null;
		}
		protectedInventories.put(newInventory, null);
		player.openInventory(newInventory);
		return newInventory;
	}

	public void disable() {
		shopListener.disableListener();
		shopListener = null;
	}

}
