package de.quadrathelden.ostereier.events;

import java.util.List;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.config.subsystems.ConfigGame;
import de.quadrathelden.ostereier.game.world.PlayerScore;

public class OstereierGameStopEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final ConfigGame configGame;
	private final World world;
	private final List<PlayerScore> playerScores;

	OstereierGameStopEvent(World world, ConfigGame configGame, List<PlayerScore> playerScores) {
		this.configGame = configGame;
		this.world = world;
		this.playerScores = playerScores;
	}

	public ConfigGame getConfigGame() {
		return configGame;
	}

	public World getWorld() {
		return world;
	}

	public List<PlayerScore> getPlayerScores() {
		return playerScores;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
