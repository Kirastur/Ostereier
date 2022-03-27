package de.quadrathelden.ostereier.integrations.theneweconomy;

import java.math.BigDecimal;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;
import net.tnemc.core.TNE;
import net.tnemc.core.common.api.TNEAPI;
import net.tnemc.core.common.currency.TNECurrency;

public class TNEHook implements TNEIntegration {

	protected final TNEAPI tneAPI;

	public TNEHook() throws OstereierException {
		tneAPI = TNE.instance().api();
		if (tneAPI == null) {
			throw new OstereierException(Message.ECONOMY_TNE_NOT_AVAIL);
		}
	}

	protected TNECurrency findCurrency(String currencyName) throws OstereierException {
		for (TNECurrency myCurrency : tneAPI.getCurrencies()) {
			if (myCurrency.getIdentifier().equals(currencyName)) {
				return myCurrency;
			}
		}
		throw new OstereierException(null, Message.ECONOMY_TNE_CURRENCY_MISSING, currencyName);
	}

	@Override
	public void validateCurrency(String currency) throws OstereierException {
		findCurrency(currency);
	}

	@Override
	public BigDecimal getHoldings(String identifier, String currency) throws OstereierException {
		TNECurrency tneCurrency = findCurrency(currency);
		return tneAPI.getHoldings(identifier, tneCurrency);
	}

	@Override
	public boolean addHoldings(String identifier, BigDecimal amount, String currency) throws OstereierException {
		TNECurrency tneCurrency = findCurrency(currency);
		return tneAPI.addHoldings(identifier, amount, tneCurrency);
	}

	@Override
	public boolean removeHoldings(String identifier, BigDecimal amount, String currency) throws OstereierException {
		TNECurrency tneCurrency = findCurrency(currency);
		return tneAPI.removeHoldings(identifier, amount, tneCurrency);
	}

}
