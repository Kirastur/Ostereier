package de.quadrathelden.ostereier.game.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class GamePlayerInteractListener implements Listener {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final GameManager gameManager;

	public GamePlayerInteractListener(Plugin plugin, TextManager textManager, ConfigManager configManager,
			PermissionManager permissionManager, GameManager gameManager) {
		this.plugin = plugin;
		this.textManager = textManager;
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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((player.getGameMode() == GameMode.SPECTATOR) || !permissionManager.hasGamePermission(player)) {
			return;
		}

		Block block = event.getClickedBlock();
		EquipmentSlot slot = event.getHand();
		if ((block == null) || (block.getType() == Material.AIR) || (slot != EquipmentSlot.HAND)) {
			return;
		}

		Action action = event.getAction();
		if ((action != Action.LEFT_CLICK_BLOCK) && (action != Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		try {
			boolean wasEgg = false;
			if ((action == Action.LEFT_CLICK_BLOCK) && configManager.getConfigGame().isCollectUsingPlayerLeftClick()) {
				wasEgg = gameManager.playerClickToCollect(player, block.getLocation(), event.getBlockFace());
			}
			if ((action == Action.RIGHT_CLICK_BLOCK) && configManager.getConfigGame().isCollectUsingPlayerLeftClick()) {
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

}
