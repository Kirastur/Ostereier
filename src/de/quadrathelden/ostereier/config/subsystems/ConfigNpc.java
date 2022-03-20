package de.quadrathelden.ostereier.config.subsystems;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigNpc {

	public static final String PARAM_ENABLE_CITIZENS = "enableCitizensIntegration";
	public static final String PARAM_NPC_NAME = "npcName";
	public static final String PARAM_USE_LEFT_BUTTON = "openShopUsingLeftMouseclick";
	public static final String PARAM_USE_RIGHT_BUTTON = "openShopUsingRightMouseclick";

	public static final boolean DEFAULT_ENABLE_CITIZENS = false;
	public static final String DEFAULT_NPC_NAME = "Osterhase";
	public static final boolean DEFAULT_USE_LEFT_BUTTON = true;
	public static final boolean DEFAULT_USE_RIGHT_BUTTON = true;

	protected boolean enableCitizensIntegration = DEFAULT_ENABLE_CITIZENS;
	protected String npcName = DEFAULT_NPC_NAME;
	protected boolean openShopUsingLeftMouseclick = DEFAULT_USE_LEFT_BUTTON;
	protected boolean openShopUsingRightMouseclick = DEFAULT_USE_RIGHT_BUTTON;

	public ConfigNpc() {
	}

	public ConfigNpc(boolean enableCitizensIntegration, String npcName, boolean openShopUsingLeftMouseclick,
			boolean openShopUsingRightMouseclick) {
		this.enableCitizensIntegration = enableCitizensIntegration;
		this.npcName = npcName;
		this.openShopUsingLeftMouseclick = openShopUsingLeftMouseclick;
		this.openShopUsingRightMouseclick = openShopUsingRightMouseclick;
	}

	public ConfigNpc(ConfigurationSection configurationSection) {
		this.enableCitizensIntegration = configurationSection.getBoolean(PARAM_ENABLE_CITIZENS,
				DEFAULT_ENABLE_CITIZENS);
		this.npcName = configurationSection.getString(PARAM_NPC_NAME, DEFAULT_NPC_NAME);
		this.openShopUsingLeftMouseclick = configurationSection.getBoolean(PARAM_USE_LEFT_BUTTON,
				DEFAULT_USE_LEFT_BUTTON);
		this.openShopUsingRightMouseclick = configurationSection.getBoolean(PARAM_USE_RIGHT_BUTTON,
				DEFAULT_USE_RIGHT_BUTTON);
	}

	public boolean isEnableCitizensIntegration() {
		return enableCitizensIntegration;
	}

	public String getNpcName() {
		return npcName;
	}

	public boolean isOpenShopUsingLeftMouseclick() {
		return openShopUsingLeftMouseclick;
	}

	public boolean isOpenShopUsingRightMouseclick() {
		return openShopUsingRightMouseclick;
	}

}
