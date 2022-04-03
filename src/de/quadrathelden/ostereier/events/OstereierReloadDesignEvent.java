package de.quadrathelden.ostereier.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.design.ConfigDesign;
import de.quadrathelden.ostereier.config.spawnpoints.ConfigSpawnpointCollection;
import de.quadrathelden.ostereier.config.subsystems.ConfigEconomy;
import de.quadrathelden.ostereier.exception.OstereierException;

public class OstereierReloadDesignEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	
	protected final Plugin plugin;
	protected final ConfigEconomy configEconomy;

	private ConfigDesign configDesign = null;
	private ConfigSpawnpointCollection configSpawnpointCollection = null;
	private boolean cancelled = false;
	private OstereierException cause = null;

	OstereierReloadDesignEvent(Plugin plugin, ConfigEconomy configEconomy) {
		this.plugin = plugin;
		this.configEconomy = configEconomy;
	}

	public Plugin getPlugin() {
		return plugin;
	}
	
	public String getDefaultRewardCurrencyName() {
		return configEconomy.getDefaultRewardCurrencyName();
	}

	public ConfigDesign getConfigDesign() {
		return configDesign;
	}

	public void setConfigDesign(ConfigDesign configDesign) {
		this.configDesign = configDesign;
	}

	public ConfigSpawnpointCollection getConfigSpawnpointCollection() {
		return configSpawnpointCollection;
	}

	public void setConfigSpawnpointCollection(ConfigSpawnpointCollection configSpawnpointCollection) {
		this.configSpawnpointCollection = configSpawnpointCollection;
	}

	OstereierException getCause() {
		return cause;
	}

	ReloadDesignResult getResult() {
		if (!isCancelled()) {
			return new ReloadDesignResult(configDesign, configSpawnpointCollection);
		} else {
			return null;
		}
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

	public void cancelWithReason(OstereierException cause) {
		this.cause = cause;
		setCancelled(true);
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
