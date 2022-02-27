package de.quadrathelden.ostereier.game.egg;

import org.bukkit.entity.Player;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.exception.OstereierException;

public class GameEggDefault extends GameEggSimple {

	protected int remainingPlacedTime = 1;
	protected int remainingCollectedTime = 1;

	public GameEggDefault(ConfigEgg configEgg) {
		super(configEgg);
	}

	@Override
	public void placeEgg() {
		drawEgg();
		remainingPlacedTime = calculateEggLifetime();
	}

	@Override
	public boolean pickupEgg(Player player) throws OstereierException {
		if (!sendEggPickupEvent(player)) {
			return false;
		}
		undrawEgg();
		remainingCollectedTime = calculateRespawnDelay();
		playPickupSound();
		rewardPlayer(player);
		return true;
	}

	@Override
	public void cancelEgg() {
		sendEggCancelEvent();
		undrawEgg();
		remainingCollectedTime = calculateRespawnDelay();
	}

	@Override
	public void decrementLifetime() {
		if (isPlaced()) {
			remainingPlacedTime = remainingPlacedTime - 1;
			if (remainingPlacedTime <= 0) {
				cancelEgg();
				return;
			}
		}
		if (!isPlaced() && (remainingCollectedTime > 0)) {
			remainingCollectedTime = remainingCollectedTime - 1;
		}
	}

	@Override
	public boolean isFinished() {
		return (!isPlaced() && (remainingCollectedTime <= 0));
	}

}
