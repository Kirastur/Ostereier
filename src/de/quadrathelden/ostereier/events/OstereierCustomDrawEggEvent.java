package de.quadrathelden.ostereier.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.displays.DisplayEgg;
import de.quadrathelden.ostereier.tools.Coordinate;

public class OstereierCustomDrawEggEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final World world;
	private final Coordinate coordinate;
	private final ConfigEgg configEgg;
	private DisplayEgg displayEgg = null;

	OstereierCustomDrawEggEvent(World world, Coordinate coordinate, ConfigEgg configEgg) {
		this.world = world;
		this.coordinate = coordinate;
		this.configEgg = configEgg;
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

	public boolean hasDisplayEgg() {
		return (displayEgg != null);
	}

	DisplayEgg getDisplayEgg() {
		return displayEgg;
	}

	public void setDisplayEgg(DisplayEgg newDisplayEgg) {
		displayEgg = newDisplayEgg;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
