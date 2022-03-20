package de.quadrathelden.ostereier.events;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.statistics.AggregatedEntry;
import de.quadrathelden.ostereier.statistics.CollectDetailEntry;

public class OstereierStatisticEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final LocalDateTime intervalStart;
	private final Collection<CollectDetailEntry> collectDetails;
	private final Map<World, Integer> worldSegmentSizes;
	private final Collection<AggregatedEntry> aggregateds;

	OstereierStatisticEvent(boolean isAsync, LocalDateTime intervalStart, Collection<CollectDetailEntry> collectDetails,
			Map<World, Integer> worldSegmentSizes, Collection<AggregatedEntry> aggregateds) {
		super(isAsync);
		this.intervalStart = intervalStart;
		this.collectDetails = collectDetails;
		this.worldSegmentSizes = worldSegmentSizes;
		this.aggregateds = aggregateds;
	}

	public LocalDateTime getIntervalStart() {
		return intervalStart;
	}

	public Collection<CollectDetailEntry> getCollectDetails() {
		return collectDetails;
	}

	public Set<World> getWorlds() {
		return worldSegmentSizes.keySet();
	}

	public int getSegmentSize(World world) {
		Integer segmentSize = worldSegmentSizes.get(world);
		if (segmentSize == null) {
			return 0;
		} else {
			return segmentSize;
		}
	}

	public Collection<AggregatedEntry> getAggregateds() {
		return aggregateds;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
