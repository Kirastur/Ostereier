package de.quadrathelden.ostereier.statistics;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;

public class AggregatedEntry {

	protected final AggregatedDimensions dimensions;
	protected final int segmentSize;
	protected int eggsCollected = 0;
	protected int rewardsGiven = 0;
	protected Set<OfflinePlayer> players = new HashSet<>();

	public AggregatedEntry(AggregatedDimensions dimensions, int segmentSize) {
		this.dimensions = dimensions;
		this.segmentSize = segmentSize;
	}

	public AggregatedDimensions getDimensions() {
		return dimensions;
	}

	public int getSegmentSize() {
		return segmentSize;
	}

	public int getEggsCollected() {
		return eggsCollected;
	}

	public int getRewardsGiven() {
		return rewardsGiven;
	}

	public int getActivePlayers() {
		return players.size();
	}

	protected void add(OfflinePlayer offlinePlayer, int reward) {
		players.add(offlinePlayer);
		eggsCollected = eggsCollected + 1;
		rewardsGiven = rewardsGiven + reward;
	}

}
