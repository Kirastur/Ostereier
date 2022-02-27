package de.quadrathelden.ostereier.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.text.TextManager;

public class ScoreboardManager {

	public static final String OBJECTIVE_NAME = "ostereier";

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final EventManager eventManager;
	protected final EconomyManager economyManager;

	protected ScoreboardListener scoreboardListener = null;
	protected List<PlayerScoreboard> scoreboards = new ArrayList<>();
	protected List<ScheduledUpdate> scheduledUpdates = new ArrayList<>();

	public ScoreboardManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
		this.eventManager = orchestrator.getEventManager();
		this.economyManager = orchestrator.getEconomyManager();
		this.scoreboardListener = new ScoreboardListener(plugin, configManager, this);
		scoreboardListener.enableListener();
	}

	protected Objective buildObjective(Player player) {
		Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		String title = configManager.getConfigScoreboard().getScoreboardTitelText(player);
		Objective objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy", title, RenderType.INTEGER);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(scoreboard);
		return objective;
	}

	protected PlayerScoreboard findScoreboard(Player player) {
		for (PlayerScoreboard myScoreboard : scoreboards) {
			if (myScoreboard.getPlayer().equals(player)) {
				return myScoreboard;
			}
		}
		return null;
	}

	protected PlayerScoreboard findOrCreateScoreboard(Player player) {
		PlayerScoreboard playerScoreboard = findScoreboard(player);
		if (playerScoreboard == null) {
			Objective objective = buildObjective(player);
			playerScoreboard = new PlayerScoreboard(player, objective, textManager, configManager, eventManager,
					economyManager);
			scoreboards.add(playerScoreboard);
		}
		return playerScoreboard;
	}

	public void updateScoreboard(Player player) throws OstereierException {
		if (configManager.getConfigScoreboard().isEnableScoreboard()) {
			PlayerScoreboard playerScoreboard = findOrCreateScoreboard(player);
			playerScoreboard.updateScoreboard();
		}
	}

	public void removeScoreboard(PlayerScoreboard playerScoreboard) {
		playerScoreboard.getPlayer().setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
		scoreboards.remove(playerScoreboard);
	}

	public void removeScoreboard(Player player) {
		PlayerScoreboard playerScoreboard = findScoreboard(player);
		if (playerScoreboard != null) {
			removeScoreboard(playerScoreboard);
		}
	}

	public ScheduledUpdate findScheduledUpdate(Player player) {
		for (ScheduledUpdate myScheduledUpdate : scheduledUpdates) {
			if (myScheduledUpdate.getPlayer().equals(player)) {
				return myScheduledUpdate;
			}
		}
		return null;
	}

	public void addScheduledUpdate(Player player) {
		if (findScheduledUpdate(player) == null) {
			ScheduledUpdate newScheduledUpdate = new ScheduledUpdate(player);
			scheduledUpdates.add(newScheduledUpdate);
		}
	}

	public void handleScheduledUpdates() throws OstereierException {
		for (ScheduledUpdate myScheduledUpdate : new ArrayList<>(scheduledUpdates)) {
			myScheduledUpdate.handleScheduler();
			if (myScheduledUpdate.isEOL()) {
				Player player = myScheduledUpdate.getPlayer();
				removeScoreboard(player);
				scheduledUpdates.remove(myScheduledUpdate);
				updateScoreboard(player);
			}
		}
	}

	public void handleScheduler() throws OstereierException {
		handleScheduledUpdates();
		for (PlayerScoreboard myPlayerScoreboard : scoreboards) {
			myPlayerScoreboard.handleScheduler();
		}
		for (PlayerScoreboard myPlayerScoreboard : new ArrayList<>(scoreboards)) {
			if (myPlayerScoreboard.isEOL()) {
				removeScoreboard(myPlayerScoreboard);
			}
		}
	}

	public void disable() {
		for (PlayerScoreboard myPlayerScoreboard : new ArrayList<>(scoreboards)) {
			removeScoreboard(myPlayerScoreboard);
		}
		scoreboardListener.disableListener();
		scoreboardListener = null;
	}

}
