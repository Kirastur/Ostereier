package de.quadrathelden.ostereier.economy;

import org.bukkit.OfflinePlayer;

public class NoneEconomyProvider implements EconomyProvider {

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public int getEggs(OfflinePlayer player) {
		return 0;
	}

	@Override
	public void incrementEggs(OfflinePlayer player) {
		// Do nothing
	}

	@Override
	public void setEggs(OfflinePlayer player, int newEggAmount) {
		// Do nothing
	}

	@Override
	public int getPoints(OfflinePlayer player, String currency) {
		return 0;
	}

	@Override
	public void addPoints(OfflinePlayer player, int pointsToAdd, String currency) {
		// Do nothing
	}

	@Override
	public void removePoints(OfflinePlayer player, int pointsToRemove, String currency) {
		// Do nothing
	}

	@Override
	public void commit() {
		// Do nothing
	}

	@Override
	public void close() {
		// Do nothing
	}

}
