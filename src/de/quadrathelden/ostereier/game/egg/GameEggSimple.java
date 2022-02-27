package de.quadrathelden.ostereier.game.egg;

import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.quadrathelden.ostereier.bunny.Bunny;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.subsystem.ConfigGame;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Coordinate;

public abstract class GameEggSimple implements GameEgg {

	protected final ConfigEgg configEgg;
	protected GameHelper gameHelper = null;

	protected GameEggSimple(ConfigEgg configEgg) {
		this.configEgg = configEgg;
	}

	@Override
	public World getWorld() {
		return gameHelper.getWorld();
	}

	@Override
	public Coordinate getCoordinate() {
		return gameHelper.getCoordinate();
	}

	@Override
	public ConfigEgg getConfigEgg() {
		return configEgg;
	}

	@Override
	public void setGameHelper(GameHelper newGameHelper) {
		if (gameHelper == null) {
			gameHelper = newGameHelper;
		}
	}

	@Override
	public boolean isPlaced() {
		return gameHelper.hasDrawnEgg();
	}

	@Override
	public UUID getUUID() {
		return gameHelper.getUUID();
	}

	protected ConfigGame getConfigGame() {
		return gameHelper.getConfigGame();
	}

	protected Bunny getBunny() {
		return gameHelper.getBunny();
	}

	protected void drawEgg() {
		gameHelper.drawEgg(configEgg);
	}

	protected void undrawEgg() {
		gameHelper.undrawEgg();
	}

	protected void playSound(Sound sound) {
		if (sound != null) {
			gameHelper.playSound(sound);
		}
	}

	protected void playPickupSound() {
		playSound(configEgg.getPickupSound());
	}

	protected int calculateEggLifetime() {
		int activePlayer = gameHelper.getActivePlayer();
		return getBunny().calculateEggLifetime(getWorld(), getCoordinate(), activePlayer, configEgg);
	}

	protected int calculateRespawnDelay() {
		int activePlayer = gameHelper.getActivePlayer();
		return getBunny().calculateRespawnDelay(getWorld(), getCoordinate(), activePlayer, configEgg);
	}

	protected boolean sendEggPickupEvent(Player player) {
		return gameHelper.sendEggPickupEvent(configEgg, player);
	}

	protected void sendEggCancelEvent() {
		gameHelper.sendEggCancelEvent(configEgg);
	}

	protected void rewardPlayer(Player player) throws OstereierException {
		gameHelper.rewardPlayer(this, player);
	}

}
