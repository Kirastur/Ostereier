package de.quadrathelden.ostereier.config.subsystems;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigBunny {

	public static final String MIN_CONCURRENT_EGGS_NAME = "minConurrentEggs";
	public static final String ADDITIONAL_EGGS_PER_PLAYER_NAME = "additionalEggsPerPlayer";
	public static final String MAX_CONCURRENT_EGGS_NAME = "maxConcurrentEggs";
	public static final String MIN_RESPAWN_INTERVAL_NAME = "minRespawnInterval";
	public static final String MAX_RESPAWN_INTERVAL_NAME = "maxRespawnInterval";
	public static final String REDUCE_RESPAWN_INTERVAL_PER_PLAYER_NAME = "reduceRespawnIntervalPerPlayer";
	public static final String MAX_EGG_LIFETIME_NAME = "maxEggLifetime";

	public static final int MIN_CONCURRENT_EGGS_DEFAULT = 5;
	public static final double ADDITIONAL_EGGS_PER_PLAYER_DEFAULT = 0.5;
	public static final int MAX_CONCURRENT_EGGS_DEFAULT = 20;
	public static final int MIN_RESPAWN_INTERVAL_DEFAULT = 20;
	public static final int MAX_RESPAWN_INTERVAL_DEFAULT = 40;
	public static final double REDUCE_RESPAWN_INTERVAL_PER_PLAYER_DEFAULT = 0.1;
	public static final int MAX_EGG_LIFETIME_DEFAULT = 300;

	protected int minConcurrentEggs = MIN_CONCURRENT_EGGS_DEFAULT;
	protected double additionalEggsPerPlayer = ADDITIONAL_EGGS_PER_PLAYER_DEFAULT;
	protected int maxConcurrentEggs = MAX_CONCURRENT_EGGS_DEFAULT;
	protected int minRespawnInterval = MIN_RESPAWN_INTERVAL_DEFAULT;
	protected int maxRespawnInterval = MAX_RESPAWN_INTERVAL_DEFAULT;
	protected double reduceRespawnIntervalPerPlayer = REDUCE_RESPAWN_INTERVAL_PER_PLAYER_DEFAULT;
	protected int maxEggLifetime = MAX_EGG_LIFETIME_DEFAULT;

	public ConfigBunny() {
	}

	public ConfigBunny(int minConcurrentEggs, double additionalEggsPerPlayer, int maxConcurrentEggs,
			int minRespawnInterval, int maxRespawnInterval, double reduceRespawnIntervalPerPlayer,
			int maxEggLifetimeDefault) {
		this.minConcurrentEggs = minConcurrentEggs;
		this.additionalEggsPerPlayer = additionalEggsPerPlayer;
		this.maxConcurrentEggs = maxConcurrentEggs;
		this.minRespawnInterval = minRespawnInterval;
		this.maxRespawnInterval = maxRespawnInterval;
		this.reduceRespawnIntervalPerPlayer = reduceRespawnIntervalPerPlayer;
		this.maxEggLifetime = maxEggLifetimeDefault;
	}

	public ConfigBunny(ConfigurationSection configurationSection) {
		this.minConcurrentEggs = configurationSection.getInt(MIN_CONCURRENT_EGGS_NAME, MIN_CONCURRENT_EGGS_DEFAULT);
		this.additionalEggsPerPlayer = configurationSection.getDouble(ADDITIONAL_EGGS_PER_PLAYER_NAME,
				ADDITIONAL_EGGS_PER_PLAYER_DEFAULT);
		this.maxConcurrentEggs = configurationSection.getInt(MAX_CONCURRENT_EGGS_NAME, MAX_CONCURRENT_EGGS_DEFAULT);
		this.minRespawnInterval = configurationSection.getInt(MIN_RESPAWN_INTERVAL_NAME, MIN_RESPAWN_INTERVAL_DEFAULT);
		this.maxRespawnInterval = configurationSection.getInt(MAX_RESPAWN_INTERVAL_NAME, MAX_RESPAWN_INTERVAL_DEFAULT);
		this.reduceRespawnIntervalPerPlayer = configurationSection.getDouble(REDUCE_RESPAWN_INTERVAL_PER_PLAYER_NAME,
				REDUCE_RESPAWN_INTERVAL_PER_PLAYER_DEFAULT);
		this.maxEggLifetime = configurationSection.getInt(MAX_EGG_LIFETIME_NAME, MAX_EGG_LIFETIME_DEFAULT);
	}

	public int getMinConcurrentEggs() {
		return minConcurrentEggs;
	}

	public double getAdditionalEggsPerPlayer() {
		return additionalEggsPerPlayer;
	}

	public int getMaxConcurrentEggs() {
		return maxConcurrentEggs;
	}

	public int getMinRespawnInterval() {
		return minRespawnInterval;
	}

	public int getMaxRespawnInterval() {
		return maxRespawnInterval;
	}

	public double getReduceRespawnIntervalPerPlayer() {
		return reduceRespawnIntervalPerPlayer;
	}

	public int getMaxEggLifetime() {
		return maxEggLifetime;
	}

}
