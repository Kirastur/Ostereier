package de.quadrathelden.ostereier.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.config.shop.ConfigShopOffer;

public class OstereierShopFindOfferEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final String offerName;
	private ConfigShopOffer offer;

	OstereierShopFindOfferEvent(String offerName) {
		this.offerName = offerName;
	}

	public String getOfferName() {
		return offerName;
	}

	public boolean hasOffer() {
		return (offer != null);
	}

	ConfigShopOffer getOffer() {
		return offer;
	}

	public void setOffer(ConfigShopOffer offer) {
		this.offer = offer;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
