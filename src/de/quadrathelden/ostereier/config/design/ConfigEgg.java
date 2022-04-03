package de.quadrathelden.ostereier.config.design;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigEgg {

	public static final String MODE_NAME = "mode";
	public static final String MATERIAL_NAME = "material";
	public static final String ANIMAL_NAME = "animal";
	public static final String HEAD_NAME = "head";
	public static final String BALLOON_NAME = "balloon";
	public static final String REWARD_AMOUNT_NAME = "rewardAmount";
	public static final String REWARD_CURRENCY_NAME = "rewardCurrency";
	public static final String PICKUP_SOUND_NAME = "pickupSound";
	public static final String CUSTOM_NAME = "custom";

	public static final String DEFAULT_MODE = "ITEM";

	protected final String name;
	protected ConfigEggMode mode;
	protected Material material;
	protected EntityType animal;
	protected ConfigHead head;
	protected String balloon;
	protected int rewardAmount;
	protected String rewardCurrency;
	protected Sound pickupSound;
	protected String custom;

	protected ConfigEgg(String name) {
		this.name = name;
	}

	public ConfigEgg(String name, ConfigEggMode mode, Material material, EntityType animal, ConfigHead head, // NOSONAR
			String balloon, int rewardAmount, String rewardCurrency, Sound pickupSound, String custom)
			throws OstereierException {
		this.name = name;
		this.mode = mode;
		this.material = material;
		this.animal = animal;
		this.head = head;
		this.balloon = balloon;
		this.rewardAmount = rewardAmount;
		this.rewardCurrency = rewardCurrency;
		this.pickupSound = pickupSound;
		this.custom = custom;
		if (mode == null) {
			throw new OstereierException(name, Message.CONFIG_EGG_MODE_MISSING, null);
		}
		validate();
	}

	protected void readEggMode(ConfigurationSection configurationSection) {
		String modeName = configurationSection.getString(MODE_NAME, DEFAULT_MODE);
		for (ConfigEggMode myEggMode : ConfigEggMode.values()) {
			if (myEggMode.toString().equalsIgnoreCase(modeName)) {
				mode = myEggMode;
				return;
			}
		}
		mode = null;
	}

	protected void readMaterial(ConfigurationSection configurationSection) throws OstereierException {
		String materialName = configurationSection.getString(MATERIAL_NAME);
		if ((materialName == null) || materialName.isEmpty()) {
			material = null;
		} else {
			material = Material.getMaterial(materialName);
			if (material == null) {
				throw new OstereierException(name, Message.CONFIG_EGG_MATERIAL_UNKNOWN, materialName);
			}
		}
	}

	protected void readAnimal(ConfigurationSection configurationSection) throws OstereierException {
		String animalName = configurationSection.getString(ANIMAL_NAME);
		if ((animalName == null) || animalName.isEmpty()) {
			animal = null;
		} else
			try {
				animal = EntityType.valueOf(animalName);
			} catch (Exception e) {
				throw new OstereierException(name, Message.CONFIG_EGG_ANIMAL_UNKNOWN, animalName);
			}
	}

	protected void readHead(ConfigurationSection configurationSection, ConfigHeadCollection headCollection)
			throws OstereierException {
		String headName = configurationSection.getString(HEAD_NAME, "");
		if (headName.isEmpty()) {
			head = null;
		} else {
			head = headCollection.findHead(headName);
			if (head == null) {
				throw new OstereierException(name, Message.CONFIG_EGG_HEAD_UNKNOWN, headName);
			}
		}
	}

	protected void readBalloon(ConfigurationSection configurationSection) {
		balloon = configurationSection.getString(BALLOON_NAME, "");
	}

	protected void readPickupSound(ConfigurationSection configurationSection) throws OstereierException {
		String pickupSoundName = configurationSection.getString(PICKUP_SOUND_NAME);
		if ((pickupSoundName == null) || pickupSoundName.isEmpty()) {
			pickupSound = null;
		} else {
			try {
				pickupSound = Sound.valueOf(pickupSoundName);
			} catch (Exception e) {
				throw new OstereierException(name, Message.CONFIG_EGG_SOUND_UNKNOWN, pickupSoundName);
			}
		}
	}

	public ConfigEgg(ConfigurationSection configurationSection, ConfigHeadCollection headCollection,
			String defaultCurrency) throws OstereierException {
		this.name = configurationSection.getName();
		readEggMode(configurationSection);
		readMaterial(configurationSection);
		readAnimal(configurationSection);
		readHead(configurationSection, headCollection);
		readBalloon(configurationSection);
		readPickupSound(configurationSection);
		rewardAmount = configurationSection.getInt(REWARD_AMOUNT_NAME);
		rewardCurrency = configurationSection.getString(REWARD_CURRENCY_NAME, defaultCurrency);
		custom = configurationSection.getString(CUSTOM_NAME, "");
		validate();
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

	public EntityType getAnimal() {
		return animal;
	}

	public ConfigHead getHead() {
		return head;
	}

	public String getBalloon() {
		return balloon;
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

	public String getCustom() {
		return custom;
	}

	protected void validateMaterial() throws OstereierException {
		if (material == null) {
			throw new OstereierException(name, Message.CONFIG_EGG_MATERIAL_MISSING, null);
		}
		if ((material == Material.AIR) || (material == Material.WATER) || (material == Material.LAVA)) {
			throw new OstereierException(name, Message.CONFIG_EGG_INAPPROPRIATE_MATERIAL, null);
		}
	}

	protected void validateHead() throws OstereierException {
		if ((material == Material.PLAYER_HEAD) && (head == null)) {
			throw new OstereierException(name, Message.CONFIG_EGG_HEAD_MISSING, null);
		}
	}

	protected void validateItem() throws OstereierException {
		validateMaterial();
		if (!material.isItem()) {
			throw new OstereierException(name, Message.CONFIG_EGG_INAPPROPRIATE_MATERIAL, null);
		}
		validateHead();
	}

	protected void validateBlock() throws OstereierException {
		validateMaterial();
		if (!material.isBlock()) {
			throw new OstereierException(name, Message.CONFIG_EGG_INAPPROPRIATE_MATERIAL, null);
		}
		validateHead();
	}

	protected void validateAnimal() throws OstereierException {
		if (animal == null) {
			throw new OstereierException(name, Message.CONFIG_EGG_ANIMAL_MISSING, null);
		}
		if (!animal.isAlive() || !animal.isSpawnable()) {
			throw new OstereierException(name, Message.CONFIG_EGG_INAPPROPRIATE_ENTITY, animal.name());
		}
	}

	// We can't check for a valid balloon name,
	// because the HeliumBalloon reload is retarded.
	protected void validateBalloon() throws OstereierException {
		if ((balloon == null) || (balloon.isEmpty())) {
			throw new OstereierException(name, Message.CONFIG_EGG_BALLOON_MISSING, null);
		}
	}

	protected void validate() throws OstereierException {
		if (mode == null) {
			throw new OstereierException(name, Message.CONFIG_EGG_MODE_UNKNOWN, null);
		}
		switch (mode) {
		case ITEM:
			validateItem();
			break;
		case BLOCK:
			validateBlock();
			break;
		case ANIMAL:
			validateAnimal();
			break;
		case BALLOON:
			validateBalloon();
			break;
		default:
			throw new OstereierException(name, Message.CONFIG_EGG_MODE_UNKNOWN, null);
		}

		// If config is read from file, the currency is always set.
		// So this check is only needed for custom designs.
		if ((rewardCurrency == null) || rewardCurrency.isEmpty()) {
			throw new OstereierException(name, Message.CONFIG_EGG_CURRENCY_MISSING, null);
		}
	}

}
