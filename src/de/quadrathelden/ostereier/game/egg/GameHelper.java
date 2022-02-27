package de.quadrathelden.ostereier.game.egg;

import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.quadrathelden.ostereier.bunny.Bunny;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.subsystem.ConfigGame;
import de.quadrathelden.ostereier.displays.DisplayEgg;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.world.GameWorld;
import de.quadrathelden.ostereier.tools.Coordinate;

public class GameHelper {

	protected final GameWorld gameWorld;
	protected final ConfigGame configGame;
	protected final EventManager eventManager;
	protected final DisplayManager displayManager;
	protected final Coordinate coordinate;
	protected DisplayEgg displayEgg = null;

	public GameHelper(GameWorld gameWorld, ConfigGame configGame, EventManager eventManager,
			DisplayManager displayManager, Coordinate coordinate) {
		this.gameWorld = gameWorld;
		this.configGame = configGame;
		this.eventManager = eventManager;
		this.displayManager = displayManager;
		this.coordinate = coordinate;
	}

	public ConfigGame getConfigGame() {
		return configGame;
	}

	public Bunny getBunny() {
		return gameWorld.getBunny();
	}

	public World getWorld() {
		return gameWorld.getWorld();
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public boolean hasDrawnEgg() {
		return (displayEgg != null);
	}

	public void drawEgg(ConfigEgg configEgg) {
		if (displayEgg != null) {
			return;
		}
		boolean collectable = configGame.isCollectUsingPlayerPickupItem();
		displayEgg = displayManager.drawEgg(getWorld(), coordinate, configEgg, false, collectable);
	}

	public void undrawEgg() {
		if (displayEgg != null) {
			displayManager.undrawEgg(displayEgg);
			displayEgg = null;
		}
	}

	public UUID getUUID() {
		if (displayEgg != null) {
			return displayEgg.getUUID();
		} else {
			return null;
		}
	}

	public int getActivePlayer() {
		return gameWorld.getActivePlayer();
	}

	public void playSound(Sound sound) {
		World world = getWorld();
		float volume = (float) configGame.getSoundVolume();
		float pitch = (float) configGame.getSoundPitch();
		world.playSound(coordinate.toLocation(world), sound, volume, pitch);
	}

	public boolean sendEggPickupEvent(ConfigEgg configEgg, Player player) {
		return eventManager.sendEggPickupEvent(getWorld(), coordinate, configEgg, player);
	}

	public void sendEggCancelEvent(ConfigEgg configEgg) {
		eventManager.sendEggCancelEvent(getWorld(), coordinate, configEgg);
	}

	public void rewardPlayer(GameEgg gameEgg, Player player) throws OstereierException {
		gameWorld.addPlayerScore(player, gameEgg);
	}

}
