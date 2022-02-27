package de.quadrathelden.ostereier.economy;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.subsystem.ConfigEconomy;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;
import net.tnemc.core.TNE;
import net.tnemc.core.common.api.TNEAPI;
import net.tnemc.core.common.currency.TNECurrency;

public class TNEEconomyProvider implements EconomyProvider {

	protected final ConfigManager configManager;
	protected final TNEAPI tneAPI;

	public TNEEconomyProvider(ConfigManager configManager) throws OstereierException {
		this.configManager = configManager;
		tneAPI = TNE.instance().api();
		if (tneAPI == null) {
			throw new OstereierException(Message.ECONOMY_TNE_NOT_AVAIL);
		}
		checkCurrencies();
	}

	protected TNECurrency findCurrency(String currencyName) throws OstereierException {
		for (TNECurrency myCurrency : tneAPI.getCurrencies()) {
			if (myCurrency.getIdentifier().equals(currencyName)) {
				return myCurrency;
			}
		}
		throw new OstereierException(null, Message.ECONOMY_TNE_CURRENCY_MISSING, currencyName);
	}

	protected void checkCurrencies() throws OstereierException {
		Set<String> currencyNames = new HashSet<>();
		for (ConfigEgg myEgg : configManager.getEggs()) {
			String myCurrency = myEgg.getRewardCurrency();
			if ((myCurrency != null) && !myCurrency.isEmpty()) {
				currencyNames.add(myCurrency);
			}
		}
		ConfigEconomy configEconomy = configManager.getConfigEconomy();
		currencyNames.add(configEconomy.getEggCounterCurrencyName());
		currencyNames.add(configEconomy.getDefaultRewardCurrencyName());
		for (String myCurrencyName : currencyNames) {
			findCurrency(myCurrencyName);
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public int getEggs(OfflinePlayer player) throws OstereierException {
		TNECurrency tneCurrency = findCurrency(configManager.getConfigEconomy().getEggCounterCurrencyName());
		return tneAPI.getHoldings(player.getUniqueId().toString(), tneCurrency).intValue();
	}

	@Override
	public void incrementEggs(OfflinePlayer player) throws OstereierException {
		String currencyName = configManager.getConfigEconomy().getEggCounterCurrencyName();
		TNECurrency tneCurrency = findCurrency(currencyName);
		if (!tneAPI.addHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(1), tneCurrency)) {
			throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currencyName);
		}
	}

	@Override
	public void setEggs(OfflinePlayer player, int newEggAmount) throws OstereierException {
		String currencyName = configManager.getConfigEconomy().getEggCounterCurrencyName();
		TNECurrency tneCurrency = findCurrency(currencyName);
		int oldEggAmount = tneAPI.getHoldings(player.getUniqueId().toString(), tneCurrency).intValue();
		if (oldEggAmount < newEggAmount) {
			int i = newEggAmount - oldEggAmount;
			if (!tneAPI.addHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(i), tneCurrency)) {
				throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currencyName);
			}
		}
		if (oldEggAmount > newEggAmount) {
			int i = oldEggAmount - newEggAmount;
			if (!tneAPI.removeHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(i), tneCurrency)) {
				throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currencyName);
			}
		}
	}

	@Override
	public int getPoints(OfflinePlayer player, String currency) throws OstereierException {
		if ((currency == null) || currency.isEmpty()) {
			currency = configManager.getConfigEconomy().getDefaultRewardCurrencyName();
		}
		TNECurrency tneCurrency = findCurrency(currency);
		return tneAPI.getHoldings(player.getUniqueId().toString(), tneCurrency).intValue();
	}

	@Override
	public void addPoints(OfflinePlayer player, int pointsToAdd, String currency) throws OstereierException {
		if ((currency == null) || currency.isEmpty()) {
			currency = configManager.getConfigEconomy().getDefaultRewardCurrencyName();
		}
		TNECurrency tneCurrency = findCurrency(currency);
		if (!tneAPI.addHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(pointsToAdd), tneCurrency)) {
			throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currency);
		}
	}

	@Override
	public void removePoints(OfflinePlayer player, int pointsToRemove, String currency) throws OstereierException {
		if ((currency == null) || currency.isEmpty()) {
			currency = configManager.getConfigEconomy().getDefaultRewardCurrencyName();
		}
		TNECurrency tneCurrency = findCurrency(currency);
		if (!tneAPI.removeHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(pointsToRemove), tneCurrency)) {
			throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currency);

		}
	}

	@Override
	public void commit() throws OstereierException {
		// Nothing to do
	}

	@Override
	public void close() {
		// Nothing to do
	}

}
