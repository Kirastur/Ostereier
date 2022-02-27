package de.quadrathelden.ostereier.economy;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.score.InternalPlayerPersistentScore;
import de.quadrathelden.ostereier.exception.OstereierException;

public class InternalEconomyProvider implements EconomyProvider {

	protected final ConfigManager configManager;

	public InternalEconomyProvider(ConfigManager configManager) throws OstereierException {
		this.configManager = configManager;
		configManager.openInternalPlayerPersistentScore();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public int getEggs(OfflinePlayer player) throws OstereierException {
		InternalPlayerPersistentScore ipps = configManager.findIPPScore(player);
		if (ipps != null) {
			return ipps.getCurrencyAmount(configManager.getConfigEconomy().getEggCounterCurrencyName());
		} else {
			return 0;
		}
	}

	@Override
	public void incrementEggs(OfflinePlayer player) throws OstereierException {
		InternalPlayerPersistentScore ipps = configManager.findOrCreateIPPScore(player);
		ipps.incrementCurrencyAmount(configManager.getConfigEconomy().getEggCounterCurrencyName());
	}

	@Override
	public void setEggs(OfflinePlayer player, int newEggAmount) throws OstereierException {
		InternalPlayerPersistentScore ipps = configManager.findOrCreateIPPScore(player);
		ipps.setCurrencyAmount(configManager.getConfigEconomy().getEggCounterCurrencyName(), newEggAmount);
	}

	@Override
	public int getPoints(OfflinePlayer player, String currency) throws OstereierException {
		InternalPlayerPersistentScore ipps = configManager.findIPPScore(player);
		if (ipps != null) {
			return ipps.getCurrencyAmount(currency);
		} else {
			return 0;
		}
	}

	@Override
	public void addPoints(OfflinePlayer player, int pointsToAdd, String currency) throws OstereierException {
		InternalPlayerPersistentScore ipps = configManager.findOrCreateIPPScore(player);
		ipps.addCurrencyAmount(currency, pointsToAdd);
	}

	@Override
	public void removePoints(OfflinePlayer player, int pointsToRemove, String currency) throws OstereierException {
		InternalPlayerPersistentScore ipps = configManager.findOrCreateIPPScore(player);
		ipps.removeCurrencyAmount(currency, pointsToRemove);
	}

	@Override
	public void commit() throws OstereierException {
		configManager.saveIPPScores();
	}

	@Override
	public void close() {
		try {
			configManager.closeInternalPlayerPersistentScore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
