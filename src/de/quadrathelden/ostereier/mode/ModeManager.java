package de.quadrathelden.ostereier.mode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.chunktickets.ChunkTicketManager;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigDesign;
import de.quadrathelden.ostereier.config.subsystem.ConfigCalendar;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.editor.EditorManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.game.world.GameWorld;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.scoreboard.ScoreboardManager;
import de.quadrathelden.ostereier.shop.ShopManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class ModeManager {

	public static final String TEXT_DESIGN_RELOAD_INFO = "reloadDesignInfo";
	public static final String TEXT_CALENDAR_RELOAD_INFO = "reloadCalendarInfo";
	public static final String TEXT_GAME_START = "calendarGameStart";
	public static final String TEXT_GAME_STOP = "calendarGameStop";
	public static final String TEXT_SANITY_EGGS_REMOVED = "sanityEggsRemoved";

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final ChunkTicketManager chunkTicketManager;
	protected final EconomyManager economyManager;
	protected final DisplayManager displayManager;
	protected final EditorManager editorManager;
	protected final GameManager gameManager;
	protected final ScoreboardManager scoreboardManager;
	protected final ShopManager shopManager;

	protected ModeScheduler modeScheduler = null;
	protected ModeListener modeListener = null;
	protected boolean lastCalendarActive = false;
	protected int calendarInitialCountdown = 10;
	private boolean disabled = false;

	public ModeManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
		this.permissionManager = orchestrator.getPermissionManager();
		this.chunkTicketManager = orchestrator.getChunkTicketManager();
		this.economyManager = orchestrator.getEconomyManager();
		this.displayManager = orchestrator.getDisplayManager();
		this.editorManager = orchestrator.getEditorManager();
		this.gameManager = orchestrator.getGameManager();
		this.scoreboardManager = orchestrator.getScoreboardManager();
		this.shopManager = orchestrator.getShopManager();
		modeScheduler = new ModeScheduler(plugin, this);
		modeListener = new ModeListener(plugin);
		modeListener.enableListener();
	}

	public boolean isDisabled() {
		return disabled;
	}

	protected final void setDisable() {
		disabled = true;
	}

	protected void checkNotDisabled() throws OstereierException {
		if (isDisabled()) {
			throw new OstereierException(null, Message.MODE_DISABLED, null);
		}
	}

	protected void checkNoEconomy() throws OstereierException {
		if (!economyManager.getEconomyProvider().isReady()) {
			throw new OstereierException(Message.MODE_ECONOMY_PROVIDER_NOT_READY);
		}
	}

	protected void checkNotEditor() throws OstereierException {
		if (editorManager.isEditorActive()) {
			throw new OstereierException(Message.EDITOR_ALREADY_ACTIVE);
		}
	}

	protected void checkNotGame() throws OstereierException {
		if (gameManager.hasActiveGames()) {
			throw new OstereierException(Message.MODE_GAME_RUNNING);
		}
	}

	public void validateEditorPrerequisits(World world) throws OstereierException {
		checkNotDisabled();
		checkNoEconomy();
		checkNotGame();

		if (ConfigManager.isMultiworld()) {
			List<World> populatedWorlds = configManager.getPopulatedWorlds();
			if (populatedWorlds.size() > 1) {
				throw new OstereierException(Message.MODE_MULTIWORLD_SETUP_WRONG);
			}
			if ((populatedWorlds.size() == 1) && !populatedWorlds.get(0).equals(world)) {
				throw new OstereierException(world.getName(), Message.MODE_MULTIWORLD_EDITOR_WRONG,
						populatedWorlds.get(0).getName());
			}
		}
	}

	public void validateGamePrerequisits(World world) throws OstereierException {
		checkNotDisabled();
		checkNoEconomy();
		checkNotEditor();

		if (gameManager.findGameWorld(world) != null) {
			throw new OstereierException(null, Message.MODE_GAME_ALREADY_ACTIVE, world.getName());
		}
	}

	protected void printDesignInfo(CommandSender initiator) {
		int eggCount = configManager.getEggs().size();
		int templateCount = configManager.getTemplates().size();
		int spawnpointCount = configManager.getSpawnpoints().size();
		String designInfo = textManager.findText(TEXT_DESIGN_RELOAD_INFO, initiator);
		if (!designInfo.isEmpty()) {
			designInfo = String.format(designInfo, eggCount, templateCount, spawnpointCount);
			if (initiator != null) {
				initiator.sendMessage(designInfo);
			}
			if (!plugin.getServer().getConsoleSender().equals(initiator)) {
				plugin.getLogger().info(designInfo);
			}
		}
	}

	protected void printCalendarInfo(CommandSender initiator) {
		if (configManager.getConfigCalendar().isEnableCalendar()) {
			String calendarInfo = textManager.findText(TEXT_CALENDAR_RELOAD_INFO, initiator);
			if (!calendarInfo.isEmpty()) {
				String formattedEventStart = configManager.getConfigCalendar().getFormattedEventStart();
				String formattedEventStop = configManager.getConfigCalendar().getFormattedEventStop();
				calendarInfo = String.format(calendarInfo, formattedEventStart, formattedEventStop);
				if (initiator != null) {
					initiator.sendMessage(calendarInfo);
				}
				if (!plugin.getServer().getConsoleSender().equals(initiator)) {
					plugin.getLogger().info(calendarInfo);
				}
			}
		}
	}

	public void reload(CommandSender initiator) throws OstereierException {
		checkNotDisabled();
		checkNotEditor();
		checkNotGame();

		try {
			configManager.reloadMessages();
			configManager.reloadConfig();
			economyManager.updateProvider();
			String defaultRewardCurrency = configManager.getConfigEconomy().getDefaultRewardCurrencyName();
			ConfigDesign newDesign = configManager.buildConfigDesignFromLocalConfigFile(plugin, defaultRewardCurrency);
			configManager.replaceDesign(newDesign);
		} catch (OstereierException oe) {
			throw oe;
		} catch (Exception e) {
			throw new OstereierException(null, Message.JAVA_EXCEPTION, e.getMessage(), e);
		}
		printDesignInfo(initiator);
		printCalendarInfo(initiator);
	}

	public int sanityCheck(CommandSender initiator, World world) {
		int count = displayManager.repairEggs(world);
		if ((count > 0) && (initiator != null)) {
			String sanityInfo = textManager.findText(TEXT_SANITY_EGGS_REMOVED, initiator);
			if (!sanityInfo.isEmpty()) {
				sanityInfo = String.format(sanityInfo, count, world.getName());
				initiator.sendMessage(sanityInfo);
			}
		}
		return count;
	}

	protected void sendNotify(String s) {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (permissionManager.hasNotifyPermission(player)) {
				player.sendMessage(s);
			}
		}
	}

	protected void sendCalendarChange(List<World> modifiedWorlds) {
		Set<String> startedWorldNames = new TreeSet<>();
		Set<String> stoppedWorldNames = new TreeSet<>();
		for (World myWorld : modifiedWorlds) {
			if (gameManager.findGameWorld(myWorld) != null) {
				startedWorldNames.add(myWorld.getName());
			} else {
				stoppedWorldNames.add(myWorld.getName());
			}
		}
		if (!startedWorldNames.isEmpty()) {
			String calendarStartInfo = textManager.findText(TEXT_GAME_START, null);
			String s = String.join(" ", startedWorldNames);
			s = String.format(calendarStartInfo, s);
			sendNotify(s);
		}
		if (!stoppedWorldNames.isEmpty()) {
			String calendarStopInfo = textManager.findText(TEXT_GAME_STOP, null);
			String s = String.join(" ", stoppedWorldNames);
			s = String.format(calendarStopInfo, s);
			sendNotify(s);
		}
	}

	public boolean isCalendarActive() {
		ConfigCalendar configCalendar = configManager.getConfigCalendar();
		if (!configCalendar.isEnableCalendar()) {
			return false;
		}
		LocalDateTime nowDateTime = LocalDateTime.now();
		return (nowDateTime.isAfter(configCalendar.getEventStart())
				&& nowDateTime.isBefore(configCalendar.getEventStop()));
	}

	public boolean adjustGameToCalendar(World world) throws OstereierException {
		ConfigCalendar configCalendar = configManager.getConfigCalendar();
		if (!configCalendar.isEnableCalendar() || !configCalendar.hasWorld(world)) {
			return false;
		}
		boolean isActive = isCalendarActive();
		GameWorld gameWorld = gameManager.findGameWorld(world);
		if (isActive && (gameWorld == null)) {
			validateGamePrerequisits(world);
			gameManager.startGame(world);
			return true;
		}
		if (!isActive && (gameWorld != null)) {
			gameManager.stopGame(gameWorld);
			return true;
		}
		return false;
	}

	public void adjustGameToCalendarInAllWorlds() throws OstereierException {
		List<World> modifiedWorlds = new ArrayList<>();
		for (World myWorld : plugin.getServer().getWorlds()) {
			if (adjustGameToCalendar(myWorld)) {
				modifiedWorlds.add(myWorld);
			}
		}
		sendCalendarChange(modifiedWorlds);
	}

	public void adjustGameToCalendarScheduler() throws OstereierException {
		if (calendarInitialCountdown > 0) {
			calendarInitialCountdown = calendarInitialCountdown - 1;
			return;
		}
		if (!configManager.getConfigCalendar().isEnableCalendar()) {
			return;
		}
		boolean isActive = isCalendarActive();
		if (isActive == lastCalendarActive) {
			return;
		}
		lastCalendarActive = isActive;
		adjustGameToCalendarInAllWorlds();
	}

	public void handleScheduler() {
		try {
			ThreadLocalRandom.current().nextInt();
			displayManager.handleScheduler();
			if (editorManager.isEditorActive()) {
				editorManager.handleScheduler();
			}
			if (gameManager.hasActiveGames()) {
				gameManager.handleScheduler();
			}
			scoreboardManager.handleScheduler();
			adjustGameToCalendarScheduler();
		} catch (OstereierException oe) {
			sendNotify(oe.getMessage());
			oe.printStackTrace();
		} catch (Exception e) {
			sendNotify(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}

	public void disable() {
		setDisable();
		modeScheduler.cancel();
		modeScheduler = null;
		modeListener.disableListener();
		modeListener = null;

		shopManager.disable();
		gameManager.stopAllGames();
		gameManager.disable();
		if (editorManager.isEditorActive()) {
			editorManager.leaveEditorMode();
		}
		editorManager.disable();
		displayManager.disable();
		scoreboardManager.disable();
		economyManager.disable();
		chunkTicketManager.disable();
	}

}
