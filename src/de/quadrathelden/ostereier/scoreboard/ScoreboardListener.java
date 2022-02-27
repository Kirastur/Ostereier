package de.quadrathelden.ostereier.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;

public class ScoreboardListener implements Listener {

	protected final Plugin plugin;
	protected final ConfigManager configManager;
	protected final ScoreboardManager scoreboardManager;

	public ScoreboardListener(Plugin plugin, ConfigManager configManager, ScoreboardManager scoreboardManager) {
		this.plugin = plugin;
		this.configManager = configManager;
		this.scoreboardManager = scoreboardManager;
	}

	public void enableListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void disableListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if (configManager.getConfigScoreboard().isPermanent()) {
			Player player = event.getPlayer();
			if (player.getLocation() != null) { // NOSONAR
				scoreboardManager.addScheduledUpdate(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
		if (configManager.getConfigScoreboard().isPermanent()) {
			Player player = event.getPlayer();
			scoreboardManager.addScheduledUpdate(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLocaleChangeEvent(PlayerLocaleChangeEvent event) {
		Player player = event.getPlayer();
		scoreboardManager.removeScoreboard(player);
		if (configManager.getConfigScoreboard().isPermanent()) {
			scoreboardManager.addScheduledUpdate(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		scoreboardManager.removeScoreboard(player);
	}
}
