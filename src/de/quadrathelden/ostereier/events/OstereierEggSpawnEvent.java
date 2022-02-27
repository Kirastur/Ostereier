package de.quadrathelden.ostereier.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigSpawnpoint;
import de.quadrathelden.ostereier.game.egg.GameEgg;

public class OstereierEggSpawnEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final ConfigEgg configEgg;
	private final ConfigSpawnpoint configSpawnpoint;
	private final World world;
	private GameEgg customGameEgg = null;

	OstereierEggSpawnEvent(World world, ConfigEgg configEgg, ConfigSpawnpoint configSpawnpoint) {
		this.world = world;
		this.configEgg = configEgg;
		this.configSpawnpoint = configSpawnpoint;
	}

	public World getWorld() {
		return world;
	}

	public ConfigEgg getConfigEgg() {
		return configEgg;
	}

	public ConfigSpawnpoint getConfigSpawnpoint() {
		return configSpawnpoint;
	}

	public boolean hasCustomGameEgg() {
		return (customGameEgg != null);
	}

	GameEgg getCustomGameEgg() {
		return customGameEgg;
	}

	public void setCustomGameEgg(GameEgg customGameEgg) {
		this.customGameEgg = customGameEgg;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
