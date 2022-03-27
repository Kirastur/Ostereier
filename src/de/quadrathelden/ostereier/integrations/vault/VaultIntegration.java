package de.quadrathelden.ostereier.integrations.vault;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.exception.OstereierException;

public interface VaultIntegration {

	public boolean isEnabled();

	public double getBalance(OfflinePlayer player) throws OstereierException;

	public void withdrawPlayer(OfflinePlayer player, double amount) throws OstereierException;

	public void depositPlayer(OfflinePlayer player, double amount) throws OstereierException;

}
