package de.quadrathelden.ostereier.integrations;

import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.shop.ShopManager;
import de.quadrathelden.ostereier.text.TextManager;

public class IntegrationManager {

	public static final String TNE_NAME = "TheNewEconomy";
	public static final String VAULT_NAME = "Vault";
	public static final String CITIZENS_NAME = "Citizens";

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;

	public IntegrationManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
	}

	public boolean hasVault() {
		return (plugin.getServer().getPluginManager().getPlugin(VAULT_NAME) != null);
	}

	public VaultIntegration getVaultImplementation() throws OstereierException {
		if (hasVault()) {
			return new VaultImplementation(plugin);
		} else {
			return null;
		}
	}

	public boolean hasTNE() {
		return (plugin.getServer().getPluginManager().getPlugin(TNE_NAME) != null);
	}

	public TNEIntegration getTNEImplementation() throws OstereierException {
		if (hasTNE()) {
			return new TNEImplementation();
		} else {
			return null;
		}
	}

	public boolean hasCitizens() {
		return (plugin.getServer().getPluginManager().getPlugin(CITIZENS_NAME) != null);
	}

	public CitizensIntegration getCitizensImplementation(ShopManager shopManager) {
		if (hasCitizens()) {
			return new CitizensImplementation(plugin, textManager, configManager.getConfigNpc(), shopManager);
		} else {
			return null;
		}
	}

}
