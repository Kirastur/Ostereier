package de.quadrathelden.ostereier.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.config.subsystems.ConfigEconomy;
import de.quadrathelden.ostereier.economy.EconomyProvider;
import de.quadrathelden.ostereier.exception.OstereierException;

public class OstereierGetCustomEconomyProviderEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final ConfigEconomy configEconomy;
	private EconomyProvider economyProvider = null;
	private boolean cancelled = false;
	private OstereierException cancelReason = null;

	OstereierGetCustomEconomyProviderEvent(ConfigEconomy configEconomy) {
		this.configEconomy = configEconomy;
	}

	public ConfigEconomy getConfigEconomy() {
		return configEconomy;
	}

	public boolean hasEconomyProvider() {
		return (economyProvider != null);
	}

	EconomyProvider getEconomyProvider() {
		return economyProvider;
	}

	public void setEconomyProvider(EconomyProvider economyProvider) {
		this.economyProvider = economyProvider;
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

	public void cancelWithReason(OstereierException cancelReason) {
		setCancelled(true);
		this.cancelReason = cancelReason;
	}

	OstereierException getCancelReason() {
		return cancelReason;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}