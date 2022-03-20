package de.quadrathelden.ostereier.mode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.chunktickets.ChunkTicketManager;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigDesign;
import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.config.subsystems.ConfigCalendar;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.editor.EditorManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.game.world.GameWorld;
import de.quadrathelden.ostereier.integrations.CitizensIntegration;
import de.quadrathelden.ostereier.integrations.IntegrationManager;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.scoreboard.ScoreboardManager;
import de.quadrathelden.ostereier.shop.ShopManager;
import de.quadrathelden.ostereier.statistics.GameDetailEntry;
import de.quadrathelden.ostereier.statistics.StatisticManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class ModeManager {

	public static final String TEXT_EDITOR_START = "stateEditorStart";
	public static final String TEXT_EDITOR_STOP = "stateEditorStop";
	public static final String TEXT_GAME_START = "stateGameStart";
	public static final String TEXT_GAME_STOP = "stateGameStop";

	public static final String TEXT_CALENDAR_INFO = "calendarInfo";
	public static final String TEXT_SANITY_EGGS_REMOVED = "sanityEggsRemoved";

	public static final String TEXT_DESIGN_RELOAD_INFO = "reloadDesignInfo";
	public static final String TEXT_CALENDAR_RELOAD_INFO = "reloadCalendarInfo";

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final ChunkTicketManager chunkTicketManager;
	protected final IntegrationManager integrationManager;
	protected final EconomyManager economyManager;
	protected final StatisticManager statisticManager;
	protected final ScoreboardManager scoreboardManager;
	protected final DisplayManager displayManager;
	protected final EditorManager editorManager;
	protected final GameManager gameManager;
	protected final ShopManager shopManager;

	protected ModeScheduler modeScheduler = null;
	protected ModeListener modeListener = null;
	protected boolean lastCalendarActive = false;
	protected int calendarInitialCountdown = 10;
	protected int sanityCountdown = -1;
	protected boolean isSanityInitial = false;
	private boolean disabled = false;
	protected CitizensIntegration citizensIntegration = null;

	public ModeManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
		this.permissionManager = orchestrator.getPermissionManager();
		this.chunkTicketManager = orchestrator.getChunkTicketManager();
		this.integrationManager = orchestrator.getIntegrationManager();
		this.economyManager = orchestrator.getEconomyManager();
		this.statisticManager = orchestrator.getStatisticManager();
		this.scoreboardManager = orchestrator.getScoreboardManager();
		this.displayManager = orchestrator.getDisplayManager();
		this.editorManager = orchestrator.getEditorManager();
		this.gameManager = orchestrator.getGameManager();
		this.shopManager = orchestrator.getShopManager();
		modeScheduler = new ModeScheduler(plugin, this);
		modeListener = new ModeListener(plugin, this);
		modeListener.enableListener();
		if (ConfigManager.getInitialSanityCheckDelay() > 0) {
			sanityCountdown = ConfigManager.getInitialSanityCheckDelay();
			isSanityInitial = true;
		}
	}

	//
	// Notify Subsystem
	//

	protected Set<CommandSender> getNotifyReceivers(CommandSender initiator) {
		Set<CommandSender> notifyReceivers = new HashSet<>();
		for (Player myPlayer : plugin.getServer().getOnlinePlayers()) {
			if (permissionManager.hasNotifyPermission(myPlayer)) {
				notifyReceivers.add(myPlayer);
			}
		}
		if (initiator instanceof Player) {
			notifyReceivers.add(initiator);
		}
		notifyReceivers.remove(plugin.getServer().getConsoleSender());
		return notifyReceivers;
	}

	protected void printInfoNoParam(CommandSender initiator, String name) {
		for (CommandSender myCommandSender : getNotifyReceivers(initiator)) {
			String textInfo = textManager.findText(name, myCommandSender);
			if (!textInfo.isEmpty()) {
				myCommandSender.sendMessage(textInfo);
			}
		}
		String textInfoConsole = textManager.findText(name, null);
		if (!textInfoConsole.isEmpty()) {
			plugin.getLogger().info(textInfoConsole);
		}
	}

	protected void printInfoStringParam(CommandSender initiator, String name, String param) {
		for (CommandSender myCommandSender : getNotifyReceivers(initiator)) {
			String textInfo = textManager.findText(name, myCommandSender);
			if (!textInfo.isEmpty()) {
				myCommandSender.sendMessage(String.format(textInfo, param));
			}
		}
		String textInfoConsole = textManager.findText(name, null);
		if (!textInfoConsole.isEmpty()) {
			String s = String.format(textInfoConsole, param);
			plugin.getLogger().info(s);
		}
	}

	protected void printDesignInfo(CommandSender initiator) {
		int eggCount = configManager.getEggs().size();
		int templateCount = configManager.getTemplates().size();
		int spawnpointCount = configManager.getSpawnpoints().size();
		for (CommandSender myCommandSender : getNotifyReceivers(initiator)) {
			String designInfo = textManager.findText(TEXT_DESIGN_RELOAD_INFO, myCommandSender);
			if (!designInfo.isEmpty()) {
				myCommandSender.sendMessage(String.format(designInfo, eggCount, templateCount, spawnpointCount));
			}
		}
		String designInfoConsole = textManager.findText(TEXT_DESIGN_RELOAD_INFO, null);
		if (!designInfoConsole.isEmpty()) {
			String s = String.format(designInfoConsole, eggCount, templateCount, spawnpointCount);
			plugin.getLogger().info(s);
		}
	}

	protected void printCalendarInfo(CommandSender initiator) {
		if (!configManager.getConfigCalendar().isEnableCalendar()) {
			return;
		}
		String formattedEventStart = configManager.getConfigCalendar().getFormattedEventStart();
		String formattedEventStop = configManager.getConfigCalendar().getFormattedEventStop();
		for (CommandSender myCommandSender : getNotifyReceivers(initiator)) {
			String calendarInfo = textManager.findText(TEXT_CALENDAR_RELOAD_INFO, myCommandSender);
			if (!calendarInfo.isEmpty()) {
				myCommandSender.sendMessage(String.format(calendarInfo, formattedEventStart, formattedEventStop));
			}
		}
		String calendarInfo = textManager.findText(TEXT_CALENDAR_RELOAD_INFO, null);
		if (!calendarInfo.isEmpty()) {
			String s = String.format(calendarInfo, formattedEventStart, formattedEventStop);
			plugin.getLogger().info(s);
		}
	}

	protected void printSanityInfo(CommandSender initiator, World world, int count) {
		if (count <= 0) {
			return;
		}
		String worldName = world.getName();
		for (CommandSender myCommandSender : getNotifyReceivers(initiator)) {
			String sanityInfo = textManager.findText(TEXT_SANITY_EGGS_REMOVED, myCommandSender);
			if (!sanityInfo.isEmpty()) {
				myCommandSender.sendMessage(String.format(sanityInfo, count, worldName));
			}
		}
		String sanityInfo = textManager.findText(TEXT_SANITY_EGGS_REMOVED, null);
		if (!sanityInfo.isEmpty()) {
			String s = String.format(sanityInfo, count, worldName);
			plugin.getLogger().info(s);
		}
	}

	//
	// StateEngine Subsystem
	//

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

	protected void validateEditorPrerequisits(World world) throws OstereierException {
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

	public void enterEditorMode(CommandSender initiator, World world) throws OstereierException {
		validateEditorPrerequisits(world);
		editorManager.enterEditorMode(world);
		printInfoStringParam(initiator, TEXT_EDITOR_START, world.getName());
	}

	public void enterEditorMode(CommandSender initiator, World world, ConfigTemplate initialTemplate, boolean isHidden)
			throws OstereierException {
		validateEditorPrerequisits(world);
		editorManager.enterEditorMode(world, initialTemplate, isHidden);
		printInfoStringParam(initiator, TEXT_EDITOR_START, world.getName());
	}

	public void leaveEditorMode(CommandSender initiator) throws OstereierException {
		if (!editorManager.isEditorActive()) {
			throw new OstereierException(Message.API_EDITOR_NOT_ACTIVE);
		}
		editorManager.leaveEditorMode();
		printInfoNoParam(initiator, TEXT_EDITOR_STOP);
	}

	public void startGame(CommandSender initiator, World world) throws OstereierException {
		validateGamePrerequisits(world);
		gameManager.startGame(world);
		statisticManager.addGameDetailEntry(new GameDetailEntry(LocalDateTime.now(), world, true));
		printInfoStringParam(initiator, TEXT_GAME_START, world.getName());
	}

	public boolean stopGame(CommandSender initiator, World world) throws OstereierException {
		if (gameManager.findGameWorld(world) == null) {
			return false;
		}
		gameManager.stopGame(world);
		statisticManager.addGameDetailEntry(new GameDetailEntry(LocalDateTime.now(), world, false));
		statisticManager.summarizeWorld(world, isDisabled());
		printInfoStringParam(initiator, TEXT_GAME_STOP, world.getName());
		return true;
	}

	public void stopAllGames(CommandSender initiator) throws OstereierException {
		for (World myWorld : gameManager.getWorldsWithGame()) {
			stopGame(initiator, myWorld);
		}
	}

	//
	// Calendar Subsystem
	//

	public boolean isCalendarActive() {
		ConfigCalendar configCalendar = configManager.getConfigCalendar();
		if (!configCalendar.isEnableCalendar()) {
			return false;
		}
		LocalDateTime nowDateTime = LocalDateTime.now();
		return (nowDateTime.isAfter(configCalendar.getEventStart())
				&& nowDateTime.isBefore(configCalendar.getEventStop()));
	}

	public boolean adjustGameToCalendar(CommandSender initiator, World world) throws OstereierException {
		ConfigCalendar configCalendar = configManager.getConfigCalendar();
		if (!configCalendar.isEnableCalendar() || !configCalendar.hasWorld(world)) {
			return false;
		}
		boolean isActive = isCalendarActive();
		GameWorld gameWorld = gameManager.findGameWorld(world);
		if (isActive && (gameWorld == null)) {
			startGame(initiator, world);
			return true;
		}
		if (!isActive && (gameWorld != null)) {
			stopGame(initiator, world);
			return true;
		}
		return false;
	}

	public void adjustGameToCalendarInAllWorlds(CommandSender initiator) throws OstereierException {
		List<World> modifiedWorlds = new ArrayList<>();
		for (World myWorld : plugin.getServer().getWorlds()) {
			if (adjustGameToCalendar(initiator, myWorld)) {
				modifiedWorlds.add(myWorld);
			}
		}
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
		printInfoNoParam(null, TEXT_CALENDAR_INFO);
		adjustGameToCalendarInAllWorlds(null);
	}

	//
	// Sanity Subsystem
	//

	public int performSanityCheck(CommandSender initiator, World world) {
		int count = displayManager.repairEggs(world);
		printSanityInfo(initiator, world, count);
		return count;
	}

	protected void resetSanityCheck() {
		if (isSanityInitial) {
			return;
		}
		if (configManager.getConfigSanity().isEnablePeriodicSanityCheck()) {
			sanityCountdown = configManager.getConfigSanity().getCheckInterval();
		} else {
			sanityCountdown = -1;
		}
	}

	protected void handleSanityScheduler() {
		if (sanityCountdown < 0) {
			return;
		}
		if (sanityCountdown > 0) {
			sanityCountdown = sanityCountdown - 1;
			return;
		}
		isSanityInitial = false;
		resetSanityCheck();
		for (World myWorld : plugin.getServer().getWorlds()) {
			performSanityCheck(null, myWorld);
		}
	}

	//
	// NPC Citizens Integration
	//

	public boolean isCitizensIntegrationActive() {
		return (citizensIntegration != null);
	}

	protected void startCitizensIntegration() throws OstereierException {
		if (!configManager.getConfigNpc().isEnableCitizensIntegration()) {
			return;
		}
		if (!integrationManager.hasCitizens()) {
			throw new OstereierException(Message.MODE_CITIZENS_INTEGRATION_FAILED);
		}
		citizensIntegration = integrationManager.getCitizensImplementation(shopManager);
	}

	protected void stopCitizensIntegration() {
		if (citizensIntegration != null) {
			citizensIntegration.disable();
			citizensIntegration = null;
		}
	}

	protected void restartCitizensIntegration() throws OstereierException {
		stopCitizensIntegration();
		startCitizensIntegration();
	}

	//
	// Reload
	//

	public void reload(CommandSender initiator) throws OstereierException {
		checkNotDisabled();
		checkNotEditor();
		checkNotGame();

		try {
			configManager.reloadMessages();
			configManager.reloadConfig();
			economyManager.updateProvider();
			statisticManager.updateProvider();
			restartCitizensIntegration();
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
		resetSanityCheck();
	}

	//
	// Scheduler
	//

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
			statisticManager.handleScheduler();
			adjustGameToCalendarScheduler();
			handleSanityScheduler();
		} catch (OstereierException oe) {
			printInfoNoParam(null, oe.getMessage());
			oe.printStackTrace();
		} catch (Exception e) {
			printInfoNoParam(null, Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}

	public boolean isDisabled() {
		return disabled;
	}

	protected final void setDisable() {
		disabled = true;
	}

	public void disable() {
		try {
			setDisable();
			modeScheduler.cancel();
			modeScheduler = null;
			modeListener.disableListener();
			modeListener = null;

			shopManager.disable();
			stopAllGames(null);
			gameManager.disable();
			if (editorManager.isEditorActive()) {
				editorManager.leaveEditorMode();
			}
			editorManager.disable();
			displayManager.disable();
			scoreboardManager.disable();
			statisticManager.disable();
			economyManager.disable();
			chunkTicketManager.disable();
		} catch (OstereierException oe) {
			printInfoNoParam(null, oe.getMessage());
			oe.printStackTrace();
		} catch (Exception e) {
			printInfoNoParam(null, Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}

}
