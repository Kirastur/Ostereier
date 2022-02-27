package de.quadrathelden.ostereier.bunny;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.World;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.subsystem.ConfigBunny;
import de.quadrathelden.ostereier.tools.Coordinate;

public class DefaultBunny implements Bunny {

	protected final ConfigBunny configBunny;

	public DefaultBunny(ConfigBunny configBunny) {
		this.configBunny = configBunny;
	}

	@Override
	public int calculateEggsNeeded(World world, int activePlayer) {
		int eggsNeeded = (int) (activePlayer * configBunny.getAdditionalEggsPerPlayer());
		eggsNeeded = eggsNeeded + configBunny.getMinConcurrentEggs();
		if (eggsNeeded > configBunny.getMaxConcurrentEggs()) {
			eggsNeeded = configBunny.getMaxConcurrentEggs();
		}
		return eggsNeeded;
	}

	@Override
	public int calculateEggLifetime(World world, Coordinate coordinate, int activePlayer, ConfigEgg configEgg) {
		return configBunny.getMaxEggLifetime();
	}

	@Override
	public int calculateRespawnDelay(World world, Coordinate coordinate, int activePlayer, ConfigEgg configEgg) {
		int range = configBunny.getMaxRespawnInterval() - configBunny.getMinRespawnInterval();
		int respawnDelay = ThreadLocalRandom.current().nextInt(range) + configBunny.getMinRespawnInterval();
		respawnDelay = respawnDelay - (int) (activePlayer * configBunny.getReduceRespawnIntervalPerPlayer());
		if (respawnDelay < 0) {
			respawnDelay = 0;
		}
		return respawnDelay;
	}

}
