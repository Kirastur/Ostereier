package de.quadrathelden.ostereier.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultEconomyProvider implements EconomyProvider {

	// Setup Vault
	// We use the demo-code from Vault to perform the integration
	// see https://github.com/MilkBowl/VaultAPI

	protected final Plugin plugin;
	protected final ConfigManager configManager;

	protected Economy vaultEconomy = null;

	public VaultEconomyProvider(Plugin plugin, ConfigManager configManager) throws OstereierException {
		this.plugin = plugin;
		this.configManager = configManager;
		if (!setupEconomy()) {
			throw new OstereierException(Message.ECONOMY_VAULT_NOT_AVAIL);
		}
	}

	protected boolean setupEconomy() {
		if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		vaultEconomy = rsp.getProvider(); // NOSONAR
		return vaultEconomy != null;
	}

	protected void checkEconomyResponse(EconomyResponse economyResponse) throws OstereierException {
		switch (economyResponse.type) {
		case SUCCESS:
			return;
		case FAILURE:
			throw new OstereierException(null, Message.ECONOMY_VAULT_FAILURE, economyResponse.errorMessage);
		case NOT_IMPLEMENTED:
			throw new OstereierException(Message.ECONOMY_VAULT_NOT_IMPLEMENTED);
		}
	}

	@Override
	public boolean isReady() {
		return vaultEconomy.isEnabled();
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
			return (int) vaultEconomy.getBalance(player);
		} else {
			return 0;
		}
	}

	@Override
	public void addPoints(OfflinePlayer player, int pointsToAdd, String currency) throws OstereierException {
		if (configManager.getConfigEconomy().getDefaultRewardCurrencyName().equals(currency)) {
			EconomyResponse economyResponse = vaultEconomy.depositPlayer(player, pointsToAdd);
			checkEconomyResponse(economyResponse);
		}
	}

	@Override
	public void removePoints(OfflinePlayer player, int pointsToRemove, String currency) throws OstereierException {
		if (configManager.getConfigEconomy().getDefaultRewardCurrencyName().equals(currency)) {
			EconomyResponse economyResponse = vaultEconomy.withdrawPlayer(player, pointsToRemove);
			checkEconomyResponse(economyResponse);
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
