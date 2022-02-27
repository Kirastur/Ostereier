package de.quadrathelden.ostereier.game.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class GamePlayerMoveListener implements Listener {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final GameManager gameManager;

	public GamePlayerMoveListener(Plugin plugin, TextManager textManager, ConfigManager configManager,
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if ((player.getGameMode() == GameMode.SPECTATOR) || !permissionManager.hasGamePermission(player)) {
			return;
		}
		Location toLocation = event.getTo();
		try {
			gameManager.playerMoveToCollect(player, toLocation);
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
