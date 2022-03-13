package de.quadrathelden.ostereier.config.subsystem;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigSanity {

	public static final String ENABLE_NAME = "enablePeriodicSanityCheck";
	public static final String CHECK_INTERVAL_NAME = "checkInterval";

	public static final boolean ENABLE_DEFAULT = true;
	public static final int CHECK_INTERVAL_DEFAULT = 3600;

	protected boolean enablePeriodicSanityCheck = ENABLE_DEFAULT;
	protected int checkInterval = CHECK_INTERVAL_DEFAULT;

	public ConfigSanity() {
	}

	public ConfigSanity(boolean enablePeriodicSanityCheck, int checkInterval) {
		this.enablePeriodicSanityCheck = enablePeriodicSanityCheck;
		this.checkInterval = checkInterval;
	}

	public ConfigSanity(ConfigurationSection configurationSection) {
		this.enablePeriodicSanityCheck = configurationSection.getBoolean(ENABLE_NAME, ENABLE_DEFAULT);
		this.checkInterval = configurationSection.getInt(CHECK_INTERVAL_NAME, CHECK_INTERVAL_DEFAULT);
	}

	public boolean isEnablePeriodicSanityCheck() {
		return enablePeriodicSanityCheck;
	}

	public int getCheckInterval() {
		return checkInterval;
	}

}
