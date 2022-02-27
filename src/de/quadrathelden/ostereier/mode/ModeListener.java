package de.quadrathelden.ostereier.mode;

import java.util.UUID;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.displays.DisplayManager;

public class ModeListener implements Listener {

	protected final Plugin plugin;

	public ModeListener(Plugin plugin) {
		this.plugin = plugin;
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
}
