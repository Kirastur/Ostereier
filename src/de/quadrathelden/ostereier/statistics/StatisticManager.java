package de.quadrathelden.ostereier.statistics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.subsystems.ConfigStatistic;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;

public class StatisticManager {

	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final EventManager eventManager;
	protected final EconomyManager economyManager;

	protected Collection<CollectDetailEntry> collectDetailsRealtime = Collections
			.synchronizedCollection(new ArrayList<>());
	protected Collection<GameDetailEntry> gameDetailsRealtime = Collections.synchronizedCollection(new ArrayList<>());
	protected LocalDateTime currentIntervalStart;
	protected LocalDateTime currentIntervalEnd;
	protected Exception lastAsyncException = null;

	protected StatisticOutputProvider statisticOutputProvider = new NoneStatisticOutputProvider();

	// The config is loaded after all Managers are enabled.
	// In the default config statistic is diabled,
	// so we don't need to care about the cuurentIntervalStart here.
	public StatisticManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.configManager = orchestrator.getConfigManager();
		this.eventManager = orchestrator.getEventManager();
		this.economyManager = orchestrator.getEconomyManager();
		setCurrentInterval();
	}

	public static LocalDateTime calculateIntervalStart(LocalDateTime timestamp, int intervalSize) {
		int year = timestamp.getYear();
		int month = timestamp.getMonthValue();
		int day = timestamp.getDayOfMonth();
		int hour = timestamp.getHour();
		int minute = timestamp.getMinute();
		minute = minute - (minute % intervalSize);
		return LocalDateTime.of(year, month, day, hour, minute);
	}

	public synchronized void addCollectDetailEntry(CollectDetailEntry newCollectDetailEntry) {
		if (configManager.getConfigStatistic().isEnableStatistic()) {
			collectDetailsRealtime.add(newCollectDetailEntry);
		}
	}

	public synchronized void addGameDetailEntry(GameDetailEntry newGameDetailEntry) {
		if (configManager.getConfigStatistic().isEnableStatistic()) {
			gameDetailsRealtime.add(newGameDetailEntry);
		}
	}

	protected void setCurrentInterval() {
		ConfigStatistic configStatistic = configManager.getConfigStatistic();
		currentIntervalStart = calculateIntervalStart(LocalDateTime.now(), configStatistic.getIntervalSize());
		currentIntervalEnd = currentIntervalStart.plusMinutes(configManager.getConfigStatistic().getIntervalSize());
	}

	//
	// StatisticOutputProvider Subsystem
	//

	public void updateProvider() {
		ConfigStatistic configStatistic = configManager.getConfigStatistic();
		statisticOutputProvider.close();
		switch (configStatistic.getOutputProvider()) {
		case NONE:
			statisticOutputProvider = new NoneStatisticOutputProvider();
			break;
		case CONSOLE:
			statisticOutputProvider = new ConsoleStatisticOutputProvider(plugin);
			break;
		case FILE:
			statisticOutputProvider = new FileStatisticOutputProvider(plugin, configStatistic);
			break;
		default:
			statisticOutputProvider = new NoneStatisticOutputProvider();
		}
		setCurrentInterval();
	}

	public StatisticOutputProvider getStatisticOutputProvider() {
		return statisticOutputProvider;
	}

	//
	// Aggregation Subsystem
	//

	protected List<CollectDetailEntry> selectCollectDetailsForProcessing(World world) {
		List<CollectDetailEntry> collectDetailsForProcessing = new ArrayList<>();
		Collection<CollectDetailEntry> collectDetailsOld = collectDetailsRealtime;
		collectDetailsRealtime = Collections.synchronizedCollection(new ArrayList<>());
		for (CollectDetailEntry myCDE : collectDetailsOld) {
			if (myCDE.timestamp().isBefore(currentIntervalEnd) && ((world == null) || (world.equals(myCDE.world())))) {
				collectDetailsForProcessing.add(myCDE);
			} else {
				addCollectDetailEntry(myCDE);
			}
		}
		return collectDetailsForProcessing;
	}

	protected int calculateSegmentSize(World world, List<GameDetailEntry> gameDetailsSnapshot) {
		LocalDateTime lastGameStart = null;
		LocalDateTime lastGameEnd = null;
		for (GameDetailEntry myGDE : gameDetailsSnapshot) {
			if (myGDE.world().equals(world) && myGDE.isStarted()
					&& ((lastGameStart == null) || lastGameStart.isBefore(myGDE.timestamp()))) {
				lastGameStart = myGDE.timestamp();
			}
			if (myGDE.world().equals(world) && !myGDE.isStarted()
					&& ((lastGameEnd == null) || lastGameEnd.isBefore(myGDE.timestamp()))) {
				lastGameEnd = myGDE.timestamp();
			}
		}
		if ((lastGameStart == null) || lastGameStart.isBefore(currentIntervalStart)) {
			lastGameStart = currentIntervalStart;
		}
		if ((lastGameEnd == null) || lastGameEnd.isBefore(lastGameStart)) {
			lastGameEnd = currentIntervalEnd;
		}
		return (int) Duration.between(lastGameStart, lastGameEnd).toSeconds();
	}

	protected Map<World, Integer> calculateWorldSegmentSizes(List<CollectDetailEntry> collectDetailsForProcessing) {

		Map<World, Integer> worldSegmentSizes = new HashMap<>();
		for (CollectDetailEntry myCDE : collectDetailsForProcessing) {
			worldSegmentSizes.put(myCDE.world(), null);
		}
		List<GameDetailEntry> gameDetailsSnapshot = new ArrayList<>(gameDetailsRealtime);
		for (Entry<World, Integer> myEntry : worldSegmentSizes.entrySet()) {
			myEntry.setValue(calculateSegmentSize(myEntry.getKey(), gameDetailsSnapshot));
		}
		return worldSegmentSizes;
	}

	// Important: We assume that the aggregate is called at least one for each
	// interval, so we do not need to care about past intervals.
	protected Collection<AggregatedEntry> aggregateCollectDetails(List<CollectDetailEntry> collectDetailsForProcessing,
			Map<World, Integer> worldSegmentSizes) {

		Map<AggregatedDimensions, AggregatedEntry> aggregateds = new HashMap<>();
		for (Entry<World, Integer> myEntry : worldSegmentSizes.entrySet()) {
			World myWorld = myEntry.getKey();
			int segmentSize = myEntry.getValue();
			for (CollectDetailEntry myCDE : collectDetailsForProcessing) {
				if (myCDE.world().equals(myWorld)) {

					// Build Dimension Set
					AggregatedDimensions myDimensions = new AggregatedDimensions(currentIntervalStart, myWorld,
							myCDE.egg(), economyManager.refineRewardCurrencyName(myCDE.egg().getRewardCurrency()));

					// Find or Create aggregated Entry
					AggregatedEntry myAggregate = aggregateds.get(myDimensions); // NOSONAR
					if (myAggregate == null) {
						myAggregate = new AggregatedEntry(myDimensions, segmentSize);
						aggregateds.put(myDimensions, myAggregate);
					}

					// Summarize Entry
					myAggregate.add(myCDE.player(), myCDE.rewardAmount());
				}
			}
		}
		return aggregateds.values();
	}

	// Only one aggregation is allowed at the same time.
	// So we need to synchronize it here.
	// The "isAsync" here is needed to set the right flag in the
	// OstereierStatisticEvent.
	// If world == null then this is the aggregation at IntervalEnd.
	public synchronized void doAggregation(World world, boolean isAsync) throws OstereierException {
		List<CollectDetailEntry> collectDetailsForProcessing = selectCollectDetailsForProcessing(world);
		if (!collectDetailsForProcessing.isEmpty()) {
			Map<World, Integer> worldSegmentSizes = calculateWorldSegmentSizes(collectDetailsForProcessing);
			Collection<AggregatedEntry> aggregates = aggregateCollectDetails(collectDetailsForProcessing,
					worldSegmentSizes);
			statisticOutputProvider.writeAggregatedData(currentIntervalStart, aggregates);
			eventManager.sendStatisticEvent(isAsync, currentIntervalStart, collectDetailsForProcessing,
					worldSegmentSizes, aggregates);
		}
		if (world != null) {
			return;
		}

		setCurrentInterval();
		for (GameDetailEntry myGDE : new ArrayList<>(gameDetailsRealtime)) {
			if (myGDE.timestamp().isBefore(currentIntervalStart)) {
				gameDetailsRealtime.remove(myGDE);
			}
		}
	}

	public void summarizeWorld(World world, boolean isShutdown) throws OstereierException {
		if (!configManager.getConfigStatistic().isEnableStatistic()) {
			return;
		}
		if (isShutdown) {
			doAggregation(world, false);
		} else {
			new AsyncStatistics(plugin, this, world);
		}
	}

	public void handleScheduler() throws Exception { // NOSONAR
		if (lastAsyncException != null) {
			Exception myException = lastAsyncException;
			lastAsyncException = null;
			throw myException;
		}
		if (!configManager.getConfigStatistic().isEnableStatistic()
				|| currentIntervalEnd.isAfter(LocalDateTime.now())) {
			return;
		}
		new AsyncStatistics(plugin, this, null);
	}

	public void disable() {
		statisticOutputProvider.close();
		statisticOutputProvider = new NoneStatisticOutputProvider();
		if (lastAsyncException != null) {
			lastAsyncException.printStackTrace();
			lastAsyncException = null;
		}
	}
}
