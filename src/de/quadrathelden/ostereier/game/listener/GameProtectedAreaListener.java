package de.quadrathelden.ostereier.game.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.permissions.PermissionManager;

public class GameProtectedAreaListener implements Listener {

	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final GameManager gameManager;

	public GameProtectedAreaListener(Plugin plugin, ConfigManager configManager, PermissionManager permissionManager,
			GameManager gameManager) {
		this.plugin = plugin;
		this.configManager = configManager;
		this.permissionManager = permissionManager;
		this.gameManager = gameManager;
	}

	public void registerListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) {
			return;
		}
		if (gameManager.isProtectedArea(block.getLocation())) {
			event.setCancelled(true);
		}
	}

}
