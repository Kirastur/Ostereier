package de.quadrathelden.ostereier.game.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;

public class GamePlayerInteractListener implements Listener {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final DisplayManager displayManager;
	protected final GameManager gameManager;

	public GamePlayerInteractListener(Plugin plugin, TextManager textManager, ConfigManager configManager,
			PermissionManager permissionManager, DisplayManager displayManager, GameManager gameManager) {
		this.plugin = plugin;
		this.textManager = textManager;
		this.configManager = configManager;
		this.permissionManager = permissionManager;
		this.displayManager = displayManager;
		this.gameManager = gameManager;
	}

	public void registerListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((player.getGameMode() == GameMode.SPECTATOR) || !permissionManager.hasGamePermission(player)) {
			return;
		}

		Block block = event.getClickedBlock();
		EquipmentSlot slot = event.getHand();
		Action action = event.getAction();
		if ((block == null) || (block.getType() == Material.AIR) || (slot != EquipmentSlot.HAND)
				|| ((action != Action.LEFT_CLICK_BLOCK) && (action != Action.RIGHT_CLICK_BLOCK))) {
			return;
		}

		try {
			boolean wasEgg = false;
			if ((action == Action.LEFT_CLICK_BLOCK) && configManager.getConfigGame().isCollectUsingPlayerLeftClick()) {
				wasEgg = gameManager.playerClickToCollect(player, block.getLocation(), event.getBlockFace());
			}
			if ((action == Action.RIGHT_CLICK_BLOCK)
					&& configManager.getConfigGame().isCollectUsingPlayerRightClick()) {
				wasEgg = gameManager.playerClickToCollect(player, block.getLocation(), event.getBlockFace());
			}
			if (wasEgg) {
				event.setCancelled(true);
			}
		} catch (OstereierException oe) {
			player.sendMessage(oe.getLocalizedFullErrorMessage(textManager, player));
			if (oe.getCause() != null) {
				oe.printStackTrace();
			}
		} catch (Exception e) {
			player.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if ((player.getGameMode() == GameMode.SPECTATOR) || !permissionManager.hasGamePermission(player)
				|| !configManager.getConfigGame().isCollectUsingPlayerRightClick()
				|| (event.getHand() != EquipmentSlot.HAND)) {
			return;
		}

		Entity entity = event.getRightClicked();
		Coordinate coordinate = displayManager.findSpawnpointCoordinate(entity);
		if (coordinate == null) {
			return;
		}

		Location location = coordinate.toLocation(entity.getWorld());
		try {
			boolean wasEgg = gameManager.playerClickToCollect(player, location, null);
			if (wasEgg) {
				event.setCancelled(true);
			}
		} catch (OstereierException oe) {
			player.sendMessage(oe.getLocalizedFullErrorMessage(textManager, player));
			if (oe.getCause() != null) {
				oe.printStackTrace();
			}
		} catch (Exception e) {
			player.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (!(damager instanceof Player)) {
			return;
		}
		Player player = (Player) damager;
		if ((player.getGameMode() == GameMode.SPECTATOR) || !permissionManager.hasGamePermission(player)
				|| !configManager.getConfigGame().isCollectUsingPlayerLeftClick()
				|| (event.getCause() != DamageCause.ENTITY_ATTACK)) {
			return;
		}

		Entity entity = event.getEntity();
		Coordinate coordinate = displayManager.findSpawnpointCoordinate(entity);
		if (coordinate == null) {
			return;
		}

		Location location = coordinate.toLocation(entity.getWorld());
		try {
			boolean wasEgg = gameManager.playerClickToCollect(player, location, null);
			if (wasEgg) {
				event.setCancelled(true);
			}
		} catch (OstereierException oe) {
			player.sendMessage(oe.getLocalizedFullErrorMessage(textManager, player));
			if (oe.getCause() != null) {
				oe.printStackTrace();
			}
		} catch (Exception e) {
			player.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}

}
