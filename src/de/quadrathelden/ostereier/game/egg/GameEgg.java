package de.quadrathelden.ostereier.game.egg;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Coordinate;

public interface GameEgg {

	public World getWorld();

	public Coordinate getCoordinate();

	public ConfigEgg getConfigEgg();

	public UUID getUUID();

	public void setGameHelper(GameHelper gameHelper);

	public void placeEgg() throws OstereierException;

	public void decrementLifetime();

	public boolean pickupEgg(Player player) throws OstereierException;

	public void cancelEgg();

	public boolean isPlaced();

	public boolean isFinished();

}
