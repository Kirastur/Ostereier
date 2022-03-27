package de.quadrathelden.ostereier.economy;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.integrations.IntegrationManager;
import de.quadrathelden.ostereier.integrations.vault.VaultIntegration;
import de.quadrathelden.ostereier.tools.Message;

public class VaultEconomyProvider implements EconomyProvider {

	protected final ConfigManager configManager;
	protected final VaultIntegration vault;

	public VaultEconomyProvider(ConfigManager configManager, IntegrationManager integrationManager) throws OstereierException {
		this.configManager = configManager;
		this.vault = integrationManager.getVaultIntegrationHook();
		if (vault == null) {
			throw new OstereierException(Message.ECONOMY_VAULT_NOT_AVAIL);
		}
	}

	@Override
	public boolean isReady() {
		return vault.isEnabled();
	}

	@Override
	public int getEggs(OfflinePlayer player) throws OstereierException {
		// Vault doesn't support multiple currencies
		return 0;
	}

	@Override
	public void incrementEggs(OfflinePlayer player) throws OstereierException {
		// Nothing to do. Vault doesn't support multiple currencies
	}

	@Override
	public void setEggs(OfflinePlayer player, int newEggAmount) throws OstereierException {
		// Nothing to do. Vault doesn't support multiple currencies
	}

	@Override
	public int getPoints(OfflinePlayer player, String currency) throws OstereierException {
		if (configManager.getConfigEconomy().getDefaultRewardCurrencyName().equals(currency)) {
			return (int) vault.getBalance(player);
		} else {
			return 0;
		}
	}

	@Override
	public void addPoints(OfflinePlayer player, int pointsToAdd, String currency) throws OstereierException {
		if (configManager.getConfigEconomy().getDefaultRewardCurrencyName().equals(currency)) {
			vault.depositPlayer(player, pointsToAdd);
		}
	}

	@Override
	public void removePoints(OfflinePlayer player, int pointsToRemove, String currency) throws OstereierException {
		if (configManager.getConfigEconomy().getDefaultRewardCurrencyName().equals(currency)) {
			vault.withdrawPlayer(player, pointsToRemove);
		}
	}

	@Override
	public void commit() throws OstereierException {
		// Nothing to do
	}

	@Override
	public void close() {
		// Nothing to do
	}

}
