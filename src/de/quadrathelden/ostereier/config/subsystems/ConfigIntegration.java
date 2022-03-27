package de.quadrathelden.ostereier.config.subsystems;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigIntegration {

	public static final String PARAM_VAULT = "vault";
	public static final String PARAM_TNE = "theneweconomy";
	public static final String PARAM_CITIZENS = "citizens";
	public static final String PARAM_PAPI = "placeholderapi";
	public static final String PARAM_BETONQUEST = "betonquest";
	public static final String PARAM_HELIUMBALLOON = "heliumballoon";

	protected boolean bVault = true;
	protected boolean bTNE = true;
	protected boolean bCitizens = true;
	protected boolean bPAPI = true;
	protected boolean bBetonQuest = true;
	protected boolean bHeliumBalloon = true;

	public ConfigIntegration() {
	}

	public ConfigIntegration(boolean bVault, boolean bTNE, boolean bCitizens, boolean bPAPI, boolean bBetonQuest,
			boolean bHeliumBalloon) {
		this.bVault = bVault;
		this.bTNE = bTNE;
		this.bCitizens = bCitizens;
		this.bPAPI = bPAPI;
		this.bBetonQuest = bBetonQuest;
		this.bHeliumBalloon = bHeliumBalloon;
	}

	public ConfigIntegration(ConfigurationSection configurationSection) {
		this.bVault = configurationSection.getBoolean(PARAM_VAULT, true);
		this.bTNE = configurationSection.getBoolean(PARAM_TNE, true);
		this.bCitizens = configurationSection.getBoolean(PARAM_CITIZENS, true);
		this.bPAPI = configurationSection.getBoolean(PARAM_PAPI, true);
		this.bBetonQuest = configurationSection.getBoolean(PARAM_BETONQUEST, true);
		this.bHeliumBalloon = configurationSection.getBoolean(PARAM_HELIUMBALLOON, true);
	}

	public boolean hasVault() {
		return bVault;
	}

	public boolean hasTNE() {
		return bTNE;
	}

	public boolean hasCitizens() {
		return bCitizens;
	}

	public boolean hasPAPI() {
		return bPAPI;
	}

	public boolean hasBetonQuest() {
		return bBetonQuest;
	}

	public boolean hasHeliumBalloon() {
		return bHeliumBalloon;
	}

}
