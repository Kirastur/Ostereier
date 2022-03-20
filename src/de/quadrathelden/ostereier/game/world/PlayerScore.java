package de.quadrathelden.ostereier.game.world;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.game.egg.GameEgg;

public class PlayerScore {

	protected final OfflinePlayer offlinePlayer;
	protected final EconomyManager economyManager;
	protected int eggsCollected = 0;
	protected Map<String, Integer> pointsCollected = new HashMap<>();
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

	public Map<String, Integer> getPointsCollected() {
		return pointsCollected;
	}

	public Instant getLastChange() {
		return lastChange;
	}

	void rewardPlayer(GameEgg gameEgg) {
		lastChange = Instant.now();
		eggsCollected = eggsCollected + 1;
		ConfigEgg configEgg = gameEgg.getConfigEgg();
		if (configEgg.getRewardAmount() > 0) {
			String currency = economyManager.refineRewardCurrencyName(configEgg.getRewardCurrency());
			Integer myPoints = pointsCollected.get(currency);
			if (myPoints == null) {
				myPoints = 0;
			}
			myPoints = myPoints + configEgg.getRewardAmount();
			pointsCollected.put(currency, myPoints);
		}
	}

}
