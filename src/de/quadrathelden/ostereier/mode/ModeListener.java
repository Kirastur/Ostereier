package de.quadrathelden.ostereier.mode;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.displays.DisplayManager;

public class ModeListener implements Listener {

	protected final Plugin plugin;
	protected final DisplayManager displayManager;
	protected final ModeManager modeManager;

	public ModeListener(Plugin plugin, DisplayManager displayManager, ModeManager modeManager) {
		this.plugin = plugin;
		this.displayManager = displayManager;
		this.modeManager = modeManager;
	}

	public void enableListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void disableListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		Item item = event.getItem();
		UUID itemUUID = DisplayManager.readDisplaySeal(item.getItemStack());
		if (itemUUID != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryPickupItemEventEvent(InventoryPickupItemEvent event) {
		Item item = event.getItem();
		UUID itemUUID = DisplayManager.readDisplaySeal(item.getItemStack());
		if (itemUUID != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnloadEvent(WorldUnloadEvent event) {
		World world = event.getWorld();
		try {
			modeManager.stopGame(null, world);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	protected void handleEntityEnterLoveModeEvent(EntityEnterLoveModeEvent event) {
		Entity entity = event.getEntity();
		if (displayManager.findSpawnpointCoordinate(entity) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	protected void handleEntityBreedEvent(EntityBreedEvent event) {
		Entity mother = event.getMother();
		Entity father = event.getFather();
		if ((displayManager.findSpawnpointCoordinate(mother) != null)
				|| (displayManager.findSpawnpointCoordinate(father) != null)) {
			event.setCancelled(true);
		}
	}

}
