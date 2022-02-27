package de.quadrathelden.ostereier.shop;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.scoreboard.ScoreboardManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class ShopListener implements Listener {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final ScoreboardManager scoreboardManager;
	protected final ShopManager shopManager;

	public ShopListener(Plugin plugin, TextManager textManager, ConfigManager configManager,
			ScoreboardManager scoreboardManager, ShopManager shopManager) {
		this.plugin = plugin;
		this.textManager = textManager;
		this.configManager = configManager;
		this.scoreboardManager = scoreboardManager;
		this.shopManager = shopManager;
	}

	public void enableListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void disableListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryOpenEvent(InventoryOpenEvent event) {
		Inventory inventory = event.getInventory();
		if (shopManager.protectedInventories.containsKey(inventory)
				&& (shopManager.protectedInventories.get(inventory) == null)) {
			shopManager.protectedInventories.put(event.getInventory(), event.getView());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		shopManager.protectedInventories.remove(event.getInventory(), event.getView());
		if (event.getPlayer() instanceof Player player)
			try {
				scoreboardManager.updateScoreboard(player);
			} catch (OstereierException oe) {
				player.sendMessage(oe.getLocalizedFullErrorMessage(textManager, player));
				if (oe.getCause() != null) {
					oe.printStackTrace();
				}
			} catch (Exception e) {
				player.sendMessage(Message.JAVA_EXCEPTION.toString());
				e.printStackTrace();
			}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		InventoryView inventoryView = event.getView();
		if (!inventoryView.equals(shopManager.protectedInventories.get(inventory))) {
			return;
		}
		event.setCancelled(true);

		ItemStack itemStack = event.getCurrentItem();
		ItemSeal itemSeal = shopManager.readItemSeal(itemStack);
		if (itemSeal == null) {
			return;
		}

		HumanEntity humanEntity = event.getWhoClicked();
		if (!(humanEntity instanceof Player)) {
			return;
		}
		Player player = (Player) humanEntity;

		try {
			if (event.getClickedInventory().equals(inventory)) {
				if (event.getSlot() == inventory.getSize() - 1) {
					return;
				}
				shopManager.buyItem(player, itemStack);
			}
			if (event.getClickedInventory().equals(player.getInventory())) {
				shopManager.sellItem(player, event.getSlot());
			}
			shopManager.updatePlayerCurrencySheet(player, inventory);
			player.updateInventory();
		} catch (OstereierException oe) {
			player.sendMessage(oe.getLocalizedFullErrorMessage(textManager, player));
			if (oe.getCause() != null) {
				oe.printStackTrace();
			}
		} catch (Exception e) {
			player.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}

}
