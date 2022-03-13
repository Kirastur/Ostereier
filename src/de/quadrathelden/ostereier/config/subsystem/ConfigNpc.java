package de.quadrathelden.ostereier.config.subsystem;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigNpc {

	public static final String ENABLE_CITIZENS_NAME = "enableCitizensIntegration";
	public static final String NPC_NAME = "npcName";
	public static final String USE_LEFT_BUTTON_NAME = "openShopUsingLeftMouseclick";
	public static final String USE_RIGHT_BUTTON_NAME = "openShopUsingRightMouseclick";

	public static final boolean ENABLE_CITIZENS_DEFAULT = false;
	public static final String NPC_DEFAULT = "Osterhase";
	public static final boolean USE_LEFT_BUTTON_DEFAULT = true;
	public static final boolean USE_RIGHT_BUTTON_DEFAULT = true;

	protected boolean enableCitizensIntegration = ENABLE_CITIZENS_DEFAULT;
	protected String npcName = NPC_DEFAULT;
	protected boolean openShopUsingLeftMouseclick = USE_LEFT_BUTTON_DEFAULT;
	protected boolean openShopUsingRightMouseclick = USE_RIGHT_BUTTON_DEFAULT;

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
		this.enableCitizensIntegration = configurationSection.getBoolean(ENABLE_CITIZENS_NAME, ENABLE_CITIZENS_DEFAULT);
		this.npcName = configurationSection.getString(NPC_NAME, NPC_DEFAULT);
		this.openShopUsingLeftMouseclick = configurationSection.getBoolean(USE_LEFT_BUTTON_NAME,
				USE_LEFT_BUTTON_DEFAULT);
		this.openShopUsingRightMouseclick = configurationSection.getBoolean(USE_RIGHT_BUTTON_NAME,
				USE_RIGHT_BUTTON_DEFAULT);
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
