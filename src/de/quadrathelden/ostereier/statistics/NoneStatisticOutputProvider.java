package de.quadrathelden.ostereier.statistics;

import java.time.LocalDateTime;
import java.util.Collection;

import de.quadrathelden.ostereier.exception.OstereierException;

public class NoneStatisticOutputProvider implements StatisticOutputProvider {

	@Override
	public void writeAggregatedData(LocalDateTime intervalStart, Collection<AggregatedEntry> aggregateds)
			throws OstereierException {
		// Nothing to do
	}

	@Override
	public void close() {
		// Nothing to do
	}

}
