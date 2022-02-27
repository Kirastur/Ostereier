package de.quadrathelden.ostereier.config.shop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigShopOfferCollection {

	public static final String OFFER_FILENAME = "offers.yml";

	protected List<ConfigShopOffer> offers = new ArrayList<>();

	public ConfigShopOfferCollection() {
	}

	public ConfigShopOfferCollection(List<ConfigShopOffer> offers) {
		this.offers.addAll(offers);
	}

	public ConfigShopOfferCollection(Plugin plugin, String defaultCurrency) throws OstereierException {
		saveDefaultOfferConfig(plugin);
		loadAllOffers(plugin, defaultCurrency);
	}

	protected void saveDefaultOfferConfig(Plugin plugin) {
		if (!new File(plugin.getDataFolder(), OFFER_FILENAME).exists()) {
			plugin.saveResource(OFFER_FILENAME, false);
		}
	}

	protected void loadAllOffers(Plugin plugin, String defaultCurrency) throws OstereierException {
		File offerFile = new File(plugin.getDataFolder(), OFFER_FILENAME);
		if (!offerFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_SHOP_FILE_MISSING, OFFER_FILENAME);
		}
		FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(offerFile);

		for (String myKey : fileConfiguration.getKeys(false)) {
			if (fileConfiguration.isConfigurationSection(myKey)) {
				ConfigShopOffer myOffer = new ConfigShopOffer(fileConfiguration.getConfigurationSection(myKey),
						defaultCurrency);
				offers.add(myOffer);
			}
		}
	}

	public ConfigShopOffer findShopOffer(String offerId) {
		for (ConfigShopOffer myOffer : offers) {
			if (myOffer.getId().equals(offerId)) {
				return myOffer;
			}
		}
		return null;
	}

	public List<ConfigShopOffer> getShopOffers() {
		return new ArrayList<>(offers);
	}
}
