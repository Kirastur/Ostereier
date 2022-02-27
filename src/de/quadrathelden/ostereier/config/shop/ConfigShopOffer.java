package de.quadrathelden.ostereier.config.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.text.OsterText;
import de.quadrathelden.ostereier.text.TextKey;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigShopOffer {

	public static final String NAME_NAME = "name";
	public static final String DESCRIPTION_NAME = "description";
	public static final String MATERIAL_NAME = "material";
	public static final String AMOUNT_NAME = "amount";
	public static final String ENCHANTMENT_TYPE_NAME = "enchantmentType";
	public static final String ENCHANTMENT_LEVEL_NAME = "enchantmentLevel";
	public static final String PRICE_NAME = "price";
	public static final String CURRENCY_NAME = "currency";

	protected final String id;
	protected OsterText name;
	protected OsterText description;
	protected Material material;
	protected int amount;
	protected Enchantment enchantmentType;
	protected int enchantmentLevel;
	protected int price;
	protected String currency;

	protected ConfigShopOffer(String id) {
		this.id = id;
	}

	public ConfigShopOffer(String id, OsterText name, OsterText description, Material material, int amount, // NOSONAR
			Enchantment enchantmentType, int enchantmentLevel, int price, String currency) throws OstereierException { // NOSONAR
		this.id = id;
		this.name = name;
		this.description = description;
		this.material = material;
		this.amount = amount;
		this.enchantmentType = enchantmentType;
		this.enchantmentLevel = enchantmentLevel;
		this.price = price;
		this.currency = currency;
		validate();
	}

	protected Map<String, String> parseTextString(ConfigurationSection configurationSection, String keyName) {
		Map<String, String> values = new HashMap<>();
		for (String myKey : configurationSection.getKeys(false)) {
			if (configurationSection.isString(myKey)) {
				TextKey textKey = TextKey.from(myKey);
				if (textKey.name().equals(keyName)) {
					values.put(textKey.locale(), configurationSection.getString(myKey));
				}
			}
		}
		return values;
	}

	public ConfigShopOffer(ConfigurationSection configurationSection, String defaultCurrency)
			throws OstereierException {
		this.id = configurationSection.getName();
		amount = configurationSection.getInt(AMOUNT_NAME, 1);
		price = configurationSection.getInt(PRICE_NAME);
		currency = configurationSection.getString(CURRENCY_NAME, defaultCurrency);
		enchantmentLevel = configurationSection.getInt(ENCHANTMENT_LEVEL_NAME);

		String materialName = configurationSection.getString(MATERIAL_NAME);
		if ((materialName == null) || materialName.isEmpty()) {
			throw new OstereierException(id, Message.CONFIG_SHOP_MATERIAL_MISSING, null);
		}
		material = Material.getMaterial(materialName);
		if (material == null) {
			throw new OstereierException(id, Message.CONFIG_SHOP_MATERIAL_UNKNOWN, materialName);
		}

		String enchantmentName = configurationSection.getString(ENCHANTMENT_TYPE_NAME);
		if ((enchantmentName == null) || enchantmentName.isEmpty()) {
			enchantmentType = null;
		} else {
			enchantmentType = findEnchantment(enchantmentName);
		}

		name = new OsterText(id, parseTextString(configurationSection, NAME_NAME));
		description = new OsterText(id, parseTextString(configurationSection, DESCRIPTION_NAME));

		validate();
	}

	protected Enchantment findEnchantment(String enchantmentName) {
		for (Enchantment myEnchantment : Enchantment.values()) {
			if (myEnchantment.getKey().getKey().equalsIgnoreCase(enchantmentName)) {
				return myEnchantment;
			}
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public String getName(CommandSender sender) {
		return name.getText(sender);
	}

	public List<String> getDescription(CommandSender sender) {
		String s = description.getText(sender);
		if (s.isEmpty()) {
			return new ArrayList<>();
		}
		s = s.replace("\r", "\n");
		s = s.replace("\n\n", "\n");
		return Arrays.asList(s.split("\\n"));
	}

	public Material getMaterial() {
		return material;
	}

	public int getAmount() {
		return amount;
	}

	public Enchantment getEnchantmentType() {
		return enchantmentType;
	}

	public int getEnchantmentLevel() {
		return enchantmentLevel;
	}

	public int getPrice() {
		return price;
	}

	public String getCurrency() {
		return currency;
	}

	protected void validate() throws OstereierException {
		if (material == null) {
			throw new OstereierException(id, Message.CONFIG_SHOP_MATERIAL_MISSING, null);
		}
		if (price == 0) {
			throw new OstereierException(id, Message.CONFIG_SHOP_PRICE_MISSING, null);
		}
		if ((currency == null) || currency.isEmpty()) {
			throw new OstereierException(id, Message.CONFIG_SHOP_CURRENCY_MISSING, null);
		}
		if (enchantmentType != null) {
			if (enchantmentLevel > enchantmentType.getMaxLevel()) {
				throw new OstereierException(id, Message.CONFIG_SHOP_ENCHANTMENT_LEVEL_TOO_HIGH,
						Integer.toString(enchantmentLevel));
			}
			if (enchantmentLevel == 0) {
				enchantmentLevel = enchantmentType.getStartLevel();
			}
			if (!enchantmentType.canEnchantItem(new ItemStack(material, 1))) {
				throw new OstereierException(id, Message.CONFIG_SHOP_ENCHANTMENT_NOT_ALLOWED,
						enchantmentType.toString());
			}
		}
		ItemStack testItemStack = new ItemStack(material, 1);
		if (testItemStack.getMaxStackSize() < amount) {
			throw new OstereierException(id, Message.CONFIG_SHOP_AMOUNT_TOO_BIG, Integer.toString(amount));
		}
	}

}
