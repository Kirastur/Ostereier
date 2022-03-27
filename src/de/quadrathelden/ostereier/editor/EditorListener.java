package de.quadrathelden.ostereier.editor;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;

public class EditorListener implements Listener {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final DisplayManager displayManager;
	protected final EditorManager editorManager;

	public EditorListener(Plugin plugin, TextManager textManager, ConfigManager configManager,
			PermissionManager permissionManager, DisplayManager displayManager, EditorManager editorManager) {
		this.plugin = plugin;
		this.textManager = textManager;
		this.configManager = configManager;
		this.permissionManager = permissionManager;
		this.displayManager = displayManager;
		this.editorManager = editorManager;
	}

	public void enableListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void disableListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!editorManager.hasListener() || !player.getWorld().equals(editorManager.getEditorWorld())) {
			return;
		}
		event.setCancelled(true);

		if ((player.getGameMode() == GameMode.SPECTATOR) || (player.getGameMode() == GameMode.ADVENTURE)
				|| !permissionManager.hasEditorPermission(player)) {
			return;
		}

		EquipmentSlot slot = event.getHand();
		Block block = event.getClickedBlock();
		Action action = event.getAction();
		if ((slot != EquipmentSlot.HAND) || (block == null)
				|| ((action != Action.LEFT_CLICK_BLOCK) && (action != Action.RIGHT_CLICK_BLOCK))) {
			return;
		}

		BlockData blockData = block.getBlockData();
		if (blockData.getMaterial() == Material.AIR) {
			return;
		}

		try {
			if ((action == Action.RIGHT_CLICK_BLOCK) ^ configManager.getConfigEditor().isSwapMouseButtonsForEggs()) {
				editorManager.playerPlaceClick(block.getLocation(), event.getBlockFace());
			} else {
				editorManager.playerRemoveClick(block.getLocation(), event.getBlockFace());
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
		if (!editorManager.hasListener() || !player.getWorld().equals(editorManager.getEditorWorld())) {
			return;
		}
		event.setCancelled(true);

		if ((player.getGameMode() == GameMode.SPECTATOR) || (player.getGameMode() == GameMode.ADVENTURE)
				|| !permissionManager.hasEditorPermission(player) || (event.getHand() != EquipmentSlot.HAND)
				|| !configManager.getConfigEditor().isSwapMouseButtonsForEggs()) {
			return;
		}

		Entity entity = event.getRightClicked();
		Coordinate coordinate = displayManager.findSpawnpointCoordinate(entity);
		if (coordinate == null) {
			return;
		}

		Location location = coordinate.toLocation(entity.getWorld());
		try {
			editorManager.playerRemoveClick(location, null);
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
		Entity entity = event.getEntity();
		if (!editorManager.hasListener() || !entity.getWorld().equals(editorManager.getEditorWorld())) {
			return;
		}
		event.setCancelled(true);

		Entity damager = event.getDamager();
		if (!(damager instanceof Player)) {
			return;
		}
		Player player = (Player) damager;
		if ((player.getGameMode() == GameMode.SPECTATOR) || (player.getGameMode() == GameMode.ADVENTURE)
				|| !permissionManager.hasEditorPermission(player) || (event.getCause() != DamageCause.ENTITY_ATTACK)
				|| configManager.getConfigEditor().isSwapMouseButtonsForEggs()) {
			return;
		}

		Coordinate coordinate = displayManager.findSpawnpointCoordinate(entity);
		if (coordinate == null) {
			return;
		}

		Location location = coordinate.toLocation(entity.getWorld());
		try {
			editorManager.playerRemoveClick(location, null);
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

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent event) {
		Entity entity = event.getEntity();
		if (!editorManager.hasListener() || !entity.getWorld().equals(editorManager.getEditorWorld())) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		LivingEntity entity = event.getEntity();
		if (!editorManager.hasListener() || !entity.getWorld().equals(editorManager.getEditorWorld())) {
			return;
		}
		if (event.getSpawnReason() == SpawnReason.EGG) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!editorManager.hasListener() || !player.getWorld().equals(editorManager.getEditorWorld())) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		Projectile entity = event.getEntity();
		if (!editorManager.hasListener() || !entity.getWorld().equals(editorManager.getEditorWorld())) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		Entity entity = event.getEntity();
		if (!editorManager.hasListener() || !entity.getWorld().equals(editorManager.getEditorWorld())) {
			return;
		}
		event.setCancelled(true);
	}

	// PickupItemEvent see ModeListener
}
