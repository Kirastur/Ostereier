package de.quadrathelden.ostereier.displays;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigSpawnpoint;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.tools.Coordinate;

public class DisplayManager {

	public static final String NAMESPACE_UUID = "ostereiUUID";
	protected static NamespacedKey uuidNamespacedKey = null;

	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final EventManager eventManager;

	protected List<DisplayEgg> displayEggs = new ArrayList<>();

	public DisplayManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.configManager = orchestrator.getConfigManager();
		this.eventManager = orchestrator.getEventManager();
		if (getUuidNamespacedKey() == null) {
			setUuidNamespacedKey(new NamespacedKey(plugin, NAMESPACE_UUID));
		}
	}

	public static NamespacedKey getUuidNamespacedKey() {
		return uuidNamespacedKey;
	}

	protected static void setUuidNamespacedKey(NamespacedKey uuidNamespacedKey) {
		DisplayManager.uuidNamespacedKey = uuidNamespacedKey;
	}

	public static UUID readDisplaySeal(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return null;
		}
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		if (!container.has(uuidNamespacedKey, PersistentDataType.STRING)) {
			return null;
		}
		String uuidName = container.get(uuidNamespacedKey, PersistentDataType.STRING);
		try {
			return UUID.fromString(uuidName);
		} catch (Exception e) {
			return null;
		}
	}

	public static void writeDisplaySeal(ItemStack itemStack, UUID uuid) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		PersistentDataContainer container = itemMeta.getPersistentDataContainer();
		container.set(uuidNamespacedKey, PersistentDataType.STRING, uuid.toString());
		itemStack.setItemMeta(itemMeta);
	}

	protected DisplayEgg buildDisplayEgg(World world, Coordinate coordinate, ConfigEgg configEgg) {
		switch (configEgg.getMode()) {
		case BLOCK:
			return new DisplayEggBlock(world, coordinate, configEgg);
		case ITEM:
			return new DisplayEggItem(world, coordinate, configEgg);
		case CUSTOM:
			return eventManager.sendCustomDrawEggEvent(world, coordinate, configEgg);
		default:
			return null;
		}
	}

	protected DisplayEgg findDisplayEgg(World world, Coordinate coordinate) {
		for (DisplayEgg myDisplayEgg : displayEggs) {
			if (myDisplayEgg.getWorld().equals(world) && myDisplayEgg.getCoordinate().equals(coordinate)) {
				return myDisplayEgg;
			}
		}
		return null;
	}

	public DisplayEgg findDisplayEgg(UUID uuid) {
		for (DisplayEgg myDisplayEgg : displayEggs) {
			UUID myUuid = myDisplayEgg.getUUID();
			if ((myUuid != null) && myUuid.equals(uuid)) {
				return myDisplayEgg;
			}
		}
		return null;
	}

	public DisplayEgg drawEgg(World world, Coordinate coordinate, ConfigEgg configEgg, boolean isEditor,
			boolean collectable) {
		DisplayEgg newDisplayEgg = buildDisplayEgg(world, coordinate, configEgg);
		if (newDisplayEgg == null) {
			return null;
		}
		newDisplayEgg.draw(isEditor, collectable);
		displayEggs.add(newDisplayEgg);
		return newDisplayEgg;
	}

	public void undrawEgg(DisplayEgg oldDisplayEgg) {
		displayEggs.remove(oldDisplayEgg);
		oldDisplayEgg.undraw();
	}

	public void undrawEgg(World world, Coordinate coordinate) {
		DisplayEgg oldDisplayEgg = findDisplayEgg(world, coordinate);
		if (oldDisplayEgg == null) {
			return;
		}
		undrawEgg(oldDisplayEgg);
	}

	public void drawAllEditorEggs(World world) {
		for (ConfigSpawnpoint mySpawnpoint : configManager.getSpawnpoints()) {
			if (mySpawnpoint.getWorld().equals(world)) {
				Coordinate myCoordinate = mySpawnpoint.getCoordinate();
				ConfigEgg myConfigEgg = mySpawnpoint.getEditorEgg();
				drawEgg(world, myCoordinate, myConfigEgg, true, false);
			}
		}
	}

	public void undrawAllEggs(World world) {
		for (DisplayEgg myDisplayEgg : new ArrayList<>(displayEggs)) {
			if ((world == null) || (myDisplayEgg.getWorld().equals(world))) {
				undrawEgg(myDisplayEgg);
			}
		}
	}

	public void handleScheduler() {
		for (DisplayEgg myDisplayEgg : displayEggs) {
			myDisplayEgg.keepAlive();
		}
	}

	public void disable() {
		undrawAllEggs(null);
	}
}
