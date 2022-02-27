package de.quadrathelden.ostereier.economy;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.exception.OstereierException;

public interface EconomyProvider {

	public boolean isReady();

	public int getEggs(OfflinePlayer player) throws OstereierException;

	public void incrementEggs(OfflinePlayer player) throws OstereierException;

	public void setEggs(OfflinePlayer player, int newEggAmount) throws OstereierException;

	public int getPoints(OfflinePlayer player, String currency) throws OstereierException;

	public void addPoints(OfflinePlayer player, int pointsToAdd, String currency) throws OstereierException;

	public void removePoints(OfflinePlayer player, int pointsToRemove, String currency) throws OstereierException;

	public void commit() throws OstereierException;

	public void close();
}
