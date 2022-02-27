package de.quadrathelden.ostereier.config.score;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class InternalPlayerPersistentScore {

	public static final String PLAYER = "player";
	public static final String CURRENCY = "currency";
	public static final String LAST_CHANGE = "lastChange";

	protected final OfflinePlayer offlinePlayer;
	protected Instant lastChange = Instant.now();
	Map<String, Integer> currencies = new HashMap<>();

	public InternalPlayerPersistentScore(OfflinePlayer offlinePlayer) {
		this.offlinePlayer = offlinePlayer;
	}

	public InternalPlayerPersistentScore(ConfigurationSection configurationSection) throws OstereierException {
		try {
			String uuidName = configurationSection.getName();
			UUID playerUUID = UUID.fromString(uuidName);
			offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
			String lastChangeString = configurationSection.getString(LAST_CHANGE);
			lastChange = Instant.parse(lastChangeString);
			ConfigurationSection currencyConfigurationSection = configurationSection.getConfigurationSection(CURRENCY);
			for (String myCurrencyName : currencyConfigurationSection.getKeys(false)) {
				int myCurrencyAmount = currencyConfigurationSection.getInt(myCurrencyName);
				currencies.put(myCurrencyName, Integer.valueOf(myCurrencyAmount));
			}
		} catch (Exception e) {
			throw new OstereierException(null, Message.JAVA_EXCEPTION, e.getMessage(), e);
		}
	}

	public OfflinePlayer getOfflinePlayer() {
		return offlinePlayer;
	}

	public Instant getLastChange() {
		return lastChange;
	}

	public int getCurrencyAmount(String currencyName) {
		for (Entry<String, Integer> myEntry : currencies.entrySet()) {
			if (myEntry.getKey().equals(currencyName)) {
				return myEntry.getValue();
			}
		}
		return 0;
	}

	public List<String> getCurrencyNames() {
		return new ArrayList<>(currencies.keySet());
	}

	public void setCurrencyAmount(String currencyName, int newAmount) {
		currencies.put(currencyName, Integer.valueOf(newAmount));
		lastChange = Instant.now();
	}

	public void addCurrencyAmount(String currencyName, int amountToAdd) {
		int oldAmount = getCurrencyAmount(currencyName);
		int newAmount = oldAmount + amountToAdd;
		setCurrencyAmount(currencyName, newAmount);
	}

	public void removeCurrencyAmount(String currencyName, int amountToRemove) throws OstereierException {
		int oldAmount = getCurrencyAmount(currencyName);
		int newAmount = oldAmount - amountToRemove;
		if (newAmount < 0) {
			throw new OstereierException(offlinePlayer.getName(), Message.CONFIG_IPPS_NEGATIVE, currencyName);
		}
		setCurrencyAmount(currencyName, newAmount);
	}

	public void incrementCurrencyAmount(String currencyName) {
		addCurrencyAmount(currencyName, 1);
	}

	protected void writeToFile(ConfigurationSection parentConfigurationSection) {
		String uuidName = offlinePlayer.getUniqueId().toString();
		ConfigurationSection thisConfigurationSection;
		if (parentConfigurationSection.isConfigurationSection(uuidName)) {
			thisConfigurationSection = parentConfigurationSection.getConfigurationSection(uuidName);
		} else {
			thisConfigurationSection = parentConfigurationSection.createSection(uuidName);
		}
		thisConfigurationSection.set(PLAYER, offlinePlayer.getName());
		thisConfigurationSection.set(LAST_CHANGE, lastChange.toString());

		thisConfigurationSection.set(CURRENCY, null);
		if (!currencies.isEmpty()) {
			ConfigurationSection currencyConfigurationSection = thisConfigurationSection.createSection(CURRENCY);
			for (Entry<String, Integer> myEntry : currencies.entrySet()) {
				currencyConfigurationSection.set(myEntry.getKey(), myEntry.getValue());
			}
		}
	}

	protected void removeFromFile(ConfigurationSection parentConfigurationSection) {
		String uuidName = offlinePlayer.getUniqueId().toString();
		if (parentConfigurationSection.isConfigurationSection(uuidName)) {
			parentConfigurationSection.set(uuidName, null);
		}
	}
}
