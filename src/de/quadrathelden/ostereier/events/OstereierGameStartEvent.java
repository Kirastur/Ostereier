package de.quadrathelden.ostereier.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.bunny.Bunny;
import de.quadrathelden.ostereier.config.subsystem.ConfigBunny;
import de.quadrathelden.ostereier.config.subsystem.ConfigGame;

public class OstereierGameStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final ConfigBunny configBunny;
	private final ConfigGame configGame;
	private final World world;
	private Bunny customBunny = null;

	OstereierGameStartEvent(World world, ConfigBunny configBunny, ConfigGame configGame) {
		this.configBunny = configBunny;
		this.world = world;
		this.configGame = configGame;
	}

	public ConfigBunny getConfigBunny() {
		return configBunny;
	}

	public ConfigGame getConfigGame() {
		return configGame;
	}

	public World getWorld() {
		return world;
	}

	public boolean hasCustomBunny() {
		return (customBunny != null);
	}

	Bunny getCustomBunny() {
		return customBunny;
	}

	public void setCustomBunny(Bunny customBunny) {
		this.customBunny = customBunny;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
