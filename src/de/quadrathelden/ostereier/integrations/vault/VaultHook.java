package de.quadrathelden.ostereier.integrations.vault;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultHook implements VaultIntegration {

	// We use the demo-code from Vault to perform the integration
	// see https://github.com/MilkBowl/VaultAPI

	protected final Economy vaultEconomy;

	public VaultHook(Plugin plugin) throws OstereierException {
		RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			throw new OstereierException(Message.ECONOMY_VAULT_NOT_AVAIL);
		}
		vaultEconomy = rsp.getProvider(); // NOSONAR
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
	public boolean isEnabled() {
		return vaultEconomy.isEnabled();
	}

	@Override
	public double getBalance(OfflinePlayer player) throws OstereierException {
		return vaultEconomy.getBalance(player);
	}

	@Override
	public void withdrawPlayer(OfflinePlayer player, double amount) throws OstereierException {
		EconomyResponse economyResponse = vaultEconomy.withdrawPlayer(player, amount);
		checkEconomyResponse(economyResponse);
	}

	@Override
	public void depositPlayer(OfflinePlayer player, double amount) throws OstereierException {
		EconomyResponse economyResponse = vaultEconomy.depositPlayer(player, amount);
		checkEconomyResponse(economyResponse);
	}

}
