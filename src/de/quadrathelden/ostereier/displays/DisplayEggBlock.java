package de.quadrathelden.ostereier.displays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.tools.Coordinate;

public class DisplayEggBlock extends DisplayEggSimple {

	public DisplayEggBlock(World world, Coordinate coordinate, ConfigEgg configEgg) {
		super(world, coordinate, configEgg, null);
	}

	@Override
	public void draw(boolean isEditor, boolean collectable) {
		backupTarget();
		drawBaseplate();
		Location thisLocation = getCoordinate().toLocation(getWorld());
		Block thisBlock = thisLocation.getBlock();
		BlockData thisBlockData = getConfigEgg().getMaterial().createBlockData();
		thisBlock.setBlockData(thisBlockData);
	}

	@Override
	public void undraw() {
		Location thisLocation = getCoordinate().toLocation(getWorld());
		Block thisBlock = thisLocation.getBlock();
		BlockData thisBlockData = Material.AIR.createBlockData();
		thisBlock.setBlockData(thisBlockData);
		undrawBaseplate();
		restoreTarget();
	}

	@Override
	public boolean hasEntity(Entity entity) {
		return false;
	}

	@Override
	public void keepAlive() {
		// Do nothing
	}

}
