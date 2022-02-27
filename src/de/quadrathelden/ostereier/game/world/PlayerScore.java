package de.quadrathelden.ostereier.game.world;

import java.time.Instant;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.economy.EconomyProvider;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.egg.GameEgg;

public class PlayerScore {

	protected final OfflinePlayer offlinePlayer;
	protected final EconomyManager economyManager;
	protected int eggsCollected = 0;
	protected int pointsCollected = 0;
	protected Instant lastChange = Instant.now();

	public PlayerScore(OfflinePlayer offlinePlayer, EconomyManager economyManager) {
		this.offlinePlayer = offlinePlayer;
		this.economyManager = economyManager;
	}

	public OfflinePlayer getOfflinePlayer() {
		return offlinePlayer;
	}

	public int getEggsCollected() {
		return eggsCollected;
	}

	public int getPointsCollected() {
		return pointsCollected;
	}

	public Instant getLastChange() {
		return lastChange;
	}

	void rewardPlayer(GameEgg gameEgg) throws OstereierException {
		lastChange = Instant.now();
		ConfigEgg configEgg = gameEgg.getConfigEgg();
		pointsCollected = pointsCollected + configEgg.getRewardAmount();
		eggsCollected = eggsCollected + 1;
		EconomyProvider economyProvider = economyManager.getEconomyProvider();
		economyProvider.incrementEggs(offlinePlayer);
		String rewardCurrency = configEgg.getRewardCurrency();
		rewardCurrency = economyManager.refineRewardCurrencyName(rewardCurrency);
		economyProvider.addPoints(offlinePlayer, configEgg.getRewardAmount(), rewardCurrency);
		economyProvider.commit();
	}

}
