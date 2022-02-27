package de.quadrathelden.ostereier.displays;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.tools.Coordinate;

public class DisplayEggItem extends DisplayEggSimple {

	protected Item item = null;
	protected boolean collectable = false;

	public DisplayEggItem(World world, Coordinate coordinate, ConfigEgg configEgg) {
		super(world, coordinate, configEgg, UUID.randomUUID());
	}

	public Item getItem() {
		return item;
	}

	@Override
	public void draw(boolean isEditor, boolean collectable) {
		this.collectable = collectable;
		backupTarget();
		drawBaseplate();
		Location thisLocation = getCoordinate().toLocation(getWorld());
		Material material = getConfigEgg().getMaterial();
		ItemStack itemStack = new ItemStack(material, 1);
		DisplayManager.writeDisplaySeal(itemStack, getUUID());
		item = getWorld().dropItem(thisLocation.clone().add(0.5, 0, 0.5), itemStack);
		item.setVelocity(new Vector());
		item.setThrower(UUID.randomUUID());
		item.setPickupDelay(100);
		if (isEditor) {
			item.setGlowing(true);
		}
	}

	@Override
	public void undraw() {
		if (item == null) {
			return;
		}
		item.remove();
		item = null;
		undrawBaseplate();
		restoreTarget();
	}

	@Override
	public void keepAlive() {
		if (item != null) {
			item.setTicksLived(1);
			if (collectable) {
				item.setPickupDelay(0);
			} else {
				item.setPickupDelay(100);
			}
		}
	}

}
