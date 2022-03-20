package de.quadrathelden.ostereier.statistics;

import java.time.LocalDateTime;
import java.util.Collection;

import de.quadrathelden.ostereier.exception.OstereierException;

public interface StatisticOutputProvider {

	public void writeAggregatedData(LocalDateTime intervalStart, Collection<AggregatedEntry> aggregateds) throws OstereierException;

	public void close();

}
