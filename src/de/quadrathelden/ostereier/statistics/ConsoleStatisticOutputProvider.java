package de.quadrathelden.ostereier.statistics;

import java.time.LocalDateTime;
import java.util.Collection;

import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;

public class ConsoleStatisticOutputProvider implements StatisticOutputProvider {

	protected final Plugin plugin;

	public ConsoleStatisticOutputProvider(Plugin plugin) {
		this.plugin = plugin;
	}

	protected String buildLine(AggregatedEntry entry) {
		String worldName = entry.getDimensions().world().getName();
		String eggName = entry.getDimensions().egg().getName();
		String currencyName = entry.getDimensions().currency();
		int segmentSize = entry.getSegmentSize();
		int eggs = entry.getEggsCollected();
		int players = entry.getActivePlayers();
		int rewards = entry.getRewardsGiven();
		return String.format("%s.%s.%s(%d): eggs=%d, players=%d, rewards=%d", worldName, eggName, currencyName,
				segmentSize, eggs, players, rewards);
	}

	@Override
	public void writeAggregatedData(LocalDateTime intervalStart, Collection<AggregatedEntry> aggregateds)
			throws OstereierException {
		for (AggregatedEntry myEntry : aggregateds) {
			String s = buildLine(myEntry);
			plugin.getLogger().info(s);
		}
	}

	@Override
	public void close() {
		// Nothing to do
	}

}
