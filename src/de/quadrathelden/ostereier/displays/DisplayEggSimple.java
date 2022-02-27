package de.quadrathelden.ostereier.displays;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.tools.Coordinate;

public abstract class DisplayEggSimple implements DisplayEgg {

	private final World world;
	private final Coordinate coordinate;
	private final ConfigEgg configEgg;
	private final UUID uuid;
	protected BlockData backupBlockData = null;
	protected BlockData backupBaseplate = null;

	protected DisplayEggSimple(World world, Coordinate coordinate, ConfigEgg configEgg, UUID uuid) {
		this.world = world;
		this.coordinate = coordinate;
		this.configEgg = configEgg;
		this.uuid = uuid;
	}

	protected void backupTarget() {
		Location location = coordinate.toLocation(world);
		Block block = location.getBlock();
		if (block.getType().isSolid()) {
			backupBlockData = block.getBlockData();
			block.setBlockData(Material.AIR.createBlockData());
		}
	}

	protected void restoreTarget() {
		if (backupBlockData != null) {
			Location location = coordinate.toLocation(world);
			Block block = location.getBlock();
			block.setBlockData(backupBlockData);
		}
	}

	protected void drawBaseplate() {
		Location belowLocation = coordinate.down().toLocation(world);
		Block belowBlock = belowLocation.getBlock();
		if (!belowBlock.getType().isSolid()) {
			backupBaseplate = belowBlock.getBlockData();
			belowBlock.setBlockData(Material.BARRIER.createBlockData());
		}
	}

	protected void undrawBaseplate() {
		if (backupBaseplate != null) {
			Location belowLocation = coordinate.down().toLocation(world);
			Block belowBlock = belowLocation.getBlock();
			belowBlock.setBlockData(backupBaseplate);
		}
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Coordinate getCoordinate() {
		return coordinate;
	}

	@Override
	public ConfigEgg getConfigEgg() {
		return configEgg;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

}
