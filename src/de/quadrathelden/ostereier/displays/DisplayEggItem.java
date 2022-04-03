package de.quadrathelden.ostereier.displays;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

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

		// Now the Custom Head Stuff //
		if (material == Material.PLAYER_HEAD) {
			ItemMeta skullMeta = itemStack.getItemMeta();
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			String headData = getConfigEgg().getHead().getData();
			profile.getProperties().put("textures", new Property("textures", headData));
			Field profileField = null;
			try {
				profileField = skullMeta.getClass().getDeclaredField("profile");
			} catch (Exception e) {
				e.printStackTrace();
			}
			profileField.setAccessible(true); // NOSONAR
			try {
				profileField.set(skullMeta, profile); // NOSONAR
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			itemStack.setItemMeta(skullMeta);
		}
		// Custom HEAD Ends here

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
	public boolean hasEntity(Entity entity) {
		return ((item != null) && item.equals(entity));
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
