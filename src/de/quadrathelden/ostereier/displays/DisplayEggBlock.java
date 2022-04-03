package de.quadrathelden.ostereier.displays;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

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
		Material material = getConfigEgg().getMaterial();
		Block thisBlock = thisLocation.getBlock();
		BlockData thisBlockData = material.createBlockData();
		thisBlock.setBlockData(thisBlockData);

		// Now the Custom Head Stuff //
		if (material == Material.PLAYER_HEAD) {
			Skull state = (Skull) thisBlock.getState();
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			String headData = getConfigEgg().getHead().getData();
			profile.getProperties().put("textures", new Property("textures", headData));
			Field profileField = null;
			try {
				profileField = state.getClass().getDeclaredField("profile");
			} catch (Exception e) {
				e.printStackTrace();
			}
			profileField.setAccessible(true); // NOSONAR
			try {
				profileField.set(state, profile); // NOSONAR
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			state.update(false, false);
		}
		// Custom HEAD Ends here

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
