package de.quadrathelden.ostereier.integrations;

import java.math.BigDecimal;

import de.quadrathelden.ostereier.exception.OstereierException;

public interface TNEIntegration {

	public void validateCurrency(String currency) throws OstereierException;

	public BigDecimal getHoldings(String identifier, String currency) throws OstereierException;

	public boolean addHoldings(String identifier, BigDecimal amount, String currency) throws OstereierException;

	public boolean removeHoldings(String identifier, BigDecimal amount, String currency) throws OstereierException;

}
