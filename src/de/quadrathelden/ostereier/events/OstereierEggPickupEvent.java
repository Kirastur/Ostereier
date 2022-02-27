package de.quadrathelden.ostereier.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.tools.Coordinate;

public class OstereierEggPickupEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final World world;
	private final Coordinate coordinate;
	private final ConfigEgg configEgg;
	private final Player player;
	private boolean cancelled = false;

	OstereierEggPickupEvent(World world, Coordinate coordinate, ConfigEgg configEgg, Player player) {
		this.world = world;
		this.coordinate = coordinate;
		this.configEgg = configEgg;
		this.player = player;
	}

	public World getWorld() {
		return world;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public ConfigEgg getConfigEgg() {
		return configEgg;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		if (cancelled) {
			this.cancelled = cancelled;
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
