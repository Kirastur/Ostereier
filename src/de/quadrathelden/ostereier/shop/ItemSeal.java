package de.quadrathelden.ostereier.shop;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.config.shop.ConfigShopOffer;

public record ItemSeal(String owner, String offer, int amount, int price, String currency) {

	public static ItemSeal fromOffer(OfflinePlayer offlinePlayer, ConfigShopOffer offer) {
		String signOwner = "";
		if (offlinePlayer != null) {
			signOwner = offlinePlayer.getUniqueId().toString();
		}
		String signOffer = offer.getId();
		int signAmount = offer.getAmount();
		int signPrice = offer.getPrice();
		String signCurrency = offer.getCurrency();
		return new ItemSeal(signOwner, signOffer, signAmount, signPrice, signCurrency);
	}

	public ItemSeal changeOwner(OfflinePlayer offlinePlayer) {
		String signOwner = "";
		if (offlinePlayer != null) {
			signOwner = offlinePlayer.getUniqueId().toString();
		}
		return new ItemSeal(signOwner, offer, amount, price, currency);
	}

}
