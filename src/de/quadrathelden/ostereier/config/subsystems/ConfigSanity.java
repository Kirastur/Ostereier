package de.quadrathelden.ostereier.config.subsystems;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigSanity {

	public static final String PARAM_ENABLE = "enablePeriodicSanityCheck";
	public static final String PARAM_CHECK_INTERVAL = "checkInterval";

	public static final boolean DEFAULT_ENABLE = true;
	public static final int DEFAULT_CHECK_INTERVAL = 3600;

	protected boolean enablePeriodicSanityCheck = DEFAULT_ENABLE;
	protected int checkInterval = DEFAULT_CHECK_INTERVAL;

	public ConfigSanity() {
	}

	public ConfigSanity(boolean enablePeriodicSanityCheck, int checkInterval) {
		this.enablePeriodicSanityCheck = enablePeriodicSanityCheck;
		this.checkInterval = checkInterval;
	}

	public ConfigSanity(ConfigurationSection configurationSection) {
		this.enablePeriodicSanityCheck = configurationSection.getBoolean(PARAM_ENABLE, DEFAULT_ENABLE);
		this.checkInterval = configurationSection.getInt(PARAM_CHECK_INTERVAL, DEFAULT_CHECK_INTERVAL);
	}

	public boolean isEnablePeriodicSanityCheck() {
		return enablePeriodicSanityCheck;
	}

	public int getCheckInterval() {
		return checkInterval;
	}

}
