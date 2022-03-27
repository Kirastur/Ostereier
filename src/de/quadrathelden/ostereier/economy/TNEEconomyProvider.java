package de.quadrathelden.ostereier.economy;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.subsystems.ConfigEconomy;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.integrations.IntegrationManager;
import de.quadrathelden.ostereier.integrations.theneweconomy.TNEIntegration;
import de.quadrathelden.ostereier.tools.Message;

public class TNEEconomyProvider implements EconomyProvider {

	protected final ConfigManager configManager;
	protected final TNEIntegration tne;

	public TNEEconomyProvider(ConfigManager configManager, IntegrationManager integrationManager) throws OstereierException {
		this.configManager = configManager;
		tne = integrationManager.getTNEIntegrationHook();
		if (tne == null) {
			throw new OstereierException(Message.ECONOMY_TNE_NOT_AVAIL);
		}
		checkCurrencies();
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
			tne.validateCurrency(myCurrencyName);
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public int getEggs(OfflinePlayer player) throws OstereierException {
		String currency = configManager.getConfigEconomy().getEggCounterCurrencyName();
		return tne.getHoldings(player.getUniqueId().toString(), currency).intValue();
	}

	@Override
	public void incrementEggs(OfflinePlayer player) throws OstereierException {
		String currency = configManager.getConfigEconomy().getEggCounterCurrencyName();
		if (!tne.addHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(1), currency)) {
			throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currency);
		}
	}

	@Override
	public void setEggs(OfflinePlayer player, int newEggAmount) throws OstereierException {
		String currency = configManager.getConfigEconomy().getEggCounterCurrencyName();
		int oldEggAmount = tne.getHoldings(player.getUniqueId().toString(), currency).intValue();
		if (oldEggAmount < newEggAmount) {
			int i = newEggAmount - oldEggAmount;
			if (!tne.addHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(i), currency)) {
				throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currency);
			}
		}
		if (oldEggAmount > newEggAmount) {
			int i = oldEggAmount - newEggAmount;
			if (!tne.removeHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(i), currency)) {
				throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currency);
			}
		}
	}

	@Override
	public int getPoints(OfflinePlayer player, String currency) throws OstereierException {
		if ((currency == null) || currency.isEmpty()) {
			currency = configManager.getConfigEconomy().getDefaultRewardCurrencyName();
		}
		return tne.getHoldings(player.getUniqueId().toString(), currency).intValue();
	}

	@Override
	public void addPoints(OfflinePlayer player, int pointsToAdd, String currency) throws OstereierException {
		if ((currency == null) || currency.isEmpty()) {
			currency = configManager.getConfigEconomy().getDefaultRewardCurrencyName();
		}
		if (!tne.addHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(pointsToAdd), currency)) {
			throw new OstereierException(null, Message.ECONOMY_TNE_ERROR, currency);
		}
	}

	@Override
	public void removePoints(OfflinePlayer player, int pointsToRemove, String currency) throws OstereierException {
		if ((currency == null) || currency.isEmpty()) {
			currency = configManager.getConfigEconomy().getDefaultRewardCurrencyName();
		}
		if (!tne.removeHoldings(player.getUniqueId().toString(), BigDecimal.valueOf(pointsToRemove), currency)) {
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
