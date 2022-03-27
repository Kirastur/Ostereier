package de.quadrathelden.ostereier.displays;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import de.quadrathelden.ostereier.chunktickets.ChunkTicketOwner;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.tools.Coordinate;

public interface DisplayEgg extends ChunkTicketOwner {

	public World getWorld();

	public Coordinate getCoordinate();

	public ConfigEgg getConfigEgg();

	public UUID getUUID();

	public void draw(boolean isEditor, boolean collectable);

	public void undraw();

	public boolean hasEntity(Entity entity);

	public void keepAlive();

}
