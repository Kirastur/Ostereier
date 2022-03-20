package de.quadrathelden.ostereier.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Objective;

import de.quadrathelden.ostereier.config.subsystems.ConfigScoreboard;

public class OstereierScoreboardUpdateEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final ConfigScoreboard configScoreboard;
	private final Objective objective;
	private boolean cancelled = false;

	OstereierScoreboardUpdateEvent(Player player, ConfigScoreboard configScoreboard, Objective objective) {
		this.player = player;
		this.configScoreboard = configScoreboard;
		this.objective = objective;
	}

	public Player getPlayer() {
		return player;
	}

	public ConfigScoreboard getConfigScoreboard() {
		return configScoreboard;
	}

	public Objective getObjective() {
		return objective;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean newCancelled) {
		if (newCancelled) {
			cancelled = true;
		}
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}