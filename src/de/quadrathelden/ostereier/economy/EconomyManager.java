package de.quadrathelden.ostereier.economy;

import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.integrations.IntegrationManager;
import de.quadrathelden.ostereier.tools.Message;

public class EconomyManager {

	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final EventManager eventManager;
	protected final IntegrationManager integrationManager;
	protected EconomyProvider economyProvider = new NoneEconomyProvider();

	public EconomyManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.configManager = orchestrator.getConfigManager();
		this.eventManager = orchestrator.getEventManager();
		this.integrationManager = orchestrator.getIntegrationManager();
	}

	protected EconomyProvider getCustomEconomyProvider() throws OstereierException {
		EconomyProvider newEconomyProvider = eventManager.sendGetCustomEconomyProvider();
		if (newEconomyProvider == null) {
			throw new OstereierException(Message.ECONOMY_CUSTOM_NOT_AVAIL);
		}
		return newEconomyProvider;
	}

	public void updateProvider() throws OstereierException {
		economyProvider.close();
		switch (configManager.getConfigEconomy().getEconomyProvider()) {
		case INTERNAL:
			economyProvider = new InternalEconomyProvider(configManager);
			break;
		case CUSTOM:
			economyProvider = getCustomEconomyProvider();
			break;
		case VAULT:
			economyProvider = new VaultEconomyProvider(configManager, integrationManager);
			break;
		case TNE:
			economyProvider = new TNEEconomyProvider(configManager, integrationManager);
			break;
		default:
			economyProvider = new NoneEconomyProvider();
		}
	}

	public EconomyProvider getEconomyProvider() {
		return economyProvider;
	}

	public String refineRewardCurrencyName(String currencyName) {
		if ((currencyName != null) && !currencyName.isEmpty()) {
			return currencyName;
		} else {
			return configManager.getConfigEconomy().getDefaultRewardCurrencyName();
		}
	}

	public void disable() {
		economyProvider.close();
		economyProvider = new NoneEconomyProvider();
	}

}
