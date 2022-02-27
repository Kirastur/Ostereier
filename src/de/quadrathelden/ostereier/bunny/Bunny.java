package de.quadrathelden.ostereier.bunny;

import org.bukkit.World;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.tools.Coordinate;

public interface Bunny {

	public int calculateEggsNeeded(World world, int activePlayer);

	public int calculateEggLifetime(World world, Coordinate coordinate, int activePlayer, ConfigEgg configEgg);

	public int calculateRespawnDelay(World world, Coordinate coordinate, int activePlayer, ConfigEgg configEgg);

}
