package de.quadrathelden.ostereier.config.design;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigEgg {

	public static final String MATERIAL_NAME = "material";
	public static final String MODE_NAME = "mode";
	public static final String REWARD_AMOUNT_NAME = "rewardAmount";
	public static final String REWARD_CURRENCY_NAME = "rewardCurrency";
	public static final String PICKUP_SOUND_NAME = "pickupSound";
	public static final String BALLOON_NAME = "balloon";
	public static final String CUSTOM_NAME = "custom";

	public static final String DEFAULT_MODE = "ITEM";

	protected final String name;
	protected ConfigEggMode mode;
	protected Material material;
	protected int rewardAmount;
	protected String rewardCurrency;
	protected Sound pickupSound;
	protected String balloon;
	protected String custom;

	protected ConfigEgg(String name) {
		this.name = name;
	}

	public ConfigEgg(String name, ConfigEggMode mode, Material material, String balloon, int rewardAmount,
			String rewardCurrency, Sound pickupSound) throws OstereierException {
		this.name = name;
		this.mode = mode;
		this.material = material;
		this.balloon = balloon;
		this.rewardAmount = rewardAmount;
		this.rewardCurrency = rewardCurrency;
		this.pickupSound = pickupSound;
		validate();
	}

	public ConfigEgg(ConfigurationSection configurationSection, String defaultCurrency) throws OstereierException {
		this.name = configurationSection.getName();
		mode = findEggMode(configurationSection.getString(MODE_NAME, DEFAULT_MODE));

		String materialName = configurationSection.getString(MATERIAL_NAME);
		if ((materialName == null) || materialName.isEmpty()) {
			material = null;
		} else {
			material = Material.getMaterial(materialName);
			if (material == null) {
				throw new OstereierException(name, Message.CONFIG_EGG_MATERIAL_UNKNOWN, materialName);

			}
		}
		rewardAmount = configurationSection.getInt(REWARD_AMOUNT_NAME);
		rewardCurrency = configurationSection.getString(REWARD_CURRENCY_NAME, defaultCurrency);
		String pickupSoundName = configurationSection.getString(PICKUP_SOUND_NAME);
		if ((pickupSoundName == null) || pickupSoundName.isEmpty()) {
			pickupSound = null;
		} else
			try {
				pickupSound = Sound.valueOf(pickupSoundName);
			} catch (Exception e) {
				throw new OstereierException(name, Message.CONFIG_EGG_SOUND_UNKNOWN, pickupSoundName);
			}
		balloon = configurationSection.getString(BALLOON_NAME, "");
		custom = configurationSection.getString(CUSTOM_NAME, "");
		validate();
	}

	public static ConfigEggMode findEggMode(String modeName) {
		for (ConfigEggMode myEggMode : ConfigEggMode.values()) {
			if (myEggMode.toString().equalsIgnoreCase(modeName)) {
				return myEggMode;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public ConfigEggMode getMode() {
		return mode;
	}

	public Material getMaterial() {
		return material;
	}

	public int getRewardAmount() {
		return rewardAmount;
	}

	public String getRewardCurrency() {
		return rewardCurrency;
	}

	public Sound getPickupSound() {
		return pickupSound;
	}

	public String getBalloon() {
		return balloon;
	}

	public String getCustom() {
		return custom;
	}

	protected boolean isSafeMaterial() {
		return (material.toString().contains("_EGG") || (material == Material.EGG) || material == Material.PUMPKIN);
	}

	protected void validate() throws OstereierException {
		if (mode == null) {
			throw new OstereierException(name, Message.CONFIG_EGG_MODE_WRONG, null);
		}
		if ((mode == ConfigEggMode.BLOCK) || (mode == ConfigEggMode.ITEM)) {
			if (material == null) {
				throw new OstereierException(name, Message.CONFIG_EGG_MATERIAL_MISSING, null);
			}
			if (ConfigManager.isSafemode() && !isSafeMaterial()) {
				throw new OstereierException(name, Message.CONFIG_EGG_ONLY_EGGS_ALLOWED, material.toString());
			}
		}

		if ((mode == ConfigEggMode.BLOCK) && !material.isSolid()) {
			throw new OstereierException(name, Message.CONFIG_EGG_NOT_SOLID, material.toString());
		}

		// If config is read from file, the currency is always set.
		// So this check is only needed for custom designs.
		if ((rewardCurrency == null) || rewardCurrency.isEmpty()) {
			throw new OstereierException(name, Message.CONFIG_EGG_CURRENCY_MISSING, null);
		}

		if ((mode == ConfigEggMode.BALLOON) && ((balloon == null) || (balloon.isEmpty()))) {
			throw new OstereierException(name, Message.CONFIG_EGG_BALLOON_MISSING, null);
		}
	}

}
