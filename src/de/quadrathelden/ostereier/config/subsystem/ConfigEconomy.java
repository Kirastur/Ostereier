package de.quadrathelden.ostereier.config.subsystem;

import org.bukkit.configuration.ConfigurationSection;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigEconomy {

	public static final String PARAM_ECONOMY_PROVIDER = "economyProvider";
	public static final String PARAM_DEFAULT_REWARD_CURRENCY_NAME = "defaultRewardCurrencyName";
	public static final String PARAM_EGG_COUNTER_CURRENCY_NAME = "eggCounterCurrencyName";

	public static final ConfigEconomyProvider DEFAULT_ECONOMY_PROVIDER = ConfigEconomyProvider.INTERNAL;
	public static final String DEFAULT_DEFAULT_REWARD_CURRENCY_NAME = "Easterpoints";
	public static final String DEFAULT_EGG_COUNTER_CURRENCY_NAME = "Eastereggs";

	protected ConfigEconomyProvider economyProvider = DEFAULT_ECONOMY_PROVIDER;
	protected String defaultRewardCurrencyName = DEFAULT_DEFAULT_REWARD_CURRENCY_NAME;
	protected String eggCounterCurrencyName = DEFAULT_EGG_COUNTER_CURRENCY_NAME;

	public ConfigEconomy() {
	}

	public ConfigEconomy(ConfigEconomyProvider economyProvider, String defaultRewardCurrencyName,
			String eggCounterCurrencyName) throws OstereierException {
		this.economyProvider = economyProvider;
		this.defaultRewardCurrencyName = defaultRewardCurrencyName;
		this.eggCounterCurrencyName = eggCounterCurrencyName;
		validate();
	}

	public ConfigEconomy(ConfigurationSection configurationSection) throws OstereierException {
		String economyProviderName = configurationSection.getString(PARAM_ECONOMY_PROVIDER);
		if ((economyProviderName == null) || economyProviderName.isEmpty()) {
			throw new OstereierException(configurationSection.getName(), Message.CONFIG_ECONOMY_PROVIDER_MISSING, null);
		}
		economyProviderName = economyProviderName.toUpperCase();
		try {
			economyProvider = ConfigEconomyProvider.valueOf(economyProviderName);
		} catch (Exception e) {
			throw new OstereierException(configurationSection.getName(), Message.CONFIG_ECONOMY_PROVIDER_UNKNOWN,
					economyProviderName);
		}
		defaultRewardCurrencyName = configurationSection.getString(PARAM_DEFAULT_REWARD_CURRENCY_NAME,
				DEFAULT_DEFAULT_REWARD_CURRENCY_NAME);
		eggCounterCurrencyName = configurationSection.getString(PARAM_EGG_COUNTER_CURRENCY_NAME,
				DEFAULT_EGG_COUNTER_CURRENCY_NAME);
		validate();
	}

	protected void validate() throws OstereierException {
		if (economyProvider == null) {
			throw new OstereierException(Message.CONFIG_ECONOMY_PROVIDER_MISSING);
		}
	}

	public ConfigEconomyProvider getEconomyProvider() {
		return economyProvider;
	}

	public String getDefaultRewardCurrencyName() {
		return defaultRewardCurrencyName;
	}

	public String getEggCounterCurrencyName() {
		return eggCounterCurrencyName;
	}

}
