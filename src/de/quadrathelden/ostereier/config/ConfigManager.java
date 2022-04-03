package de.quadrathelden.ostereier.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.config.currency.ConfigCurrency;
import de.quadrathelden.ostereier.config.design.ConfigDesign;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigHead;
import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.config.score.InternalPlayerPersistentScore;
import de.quadrathelden.ostereier.config.score.InternalPlayerPersistentScoreCollection;
import de.quadrathelden.ostereier.config.shop.ConfigShopOffer;
import de.quadrathelden.ostereier.config.shop.ConfigShopOfferCollection;
import de.quadrathelden.ostereier.config.spawnpoints.ConfigSpawnpoint;
import de.quadrathelden.ostereier.config.spawnpoints.ConfigSpawnpointCollection;
import de.quadrathelden.ostereier.config.subsystems.ConfigBunny;
import de.quadrathelden.ostereier.config.subsystems.ConfigCalendar;
import de.quadrathelden.ostereier.config.subsystems.ConfigEconomy;
import de.quadrathelden.ostereier.config.subsystems.ConfigEditor;
import de.quadrathelden.ostereier.config.subsystems.ConfigGame;
import de.quadrathelden.ostereier.config.subsystems.ConfigIntegration;
import de.quadrathelden.ostereier.config.subsystems.ConfigNpc;
import de.quadrathelden.ostereier.config.subsystems.ConfigSanity;
import de.quadrathelden.ostereier.config.subsystems.ConfigScoreboard;
import de.quadrathelden.ostereier.config.subsystems.ConfigStatistic;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.events.ReloadDesignResult;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigManager {

	public static final String MESSAGE_FILENAME = "messages.yml";

	public static final String SECTION_STARTUP = "startup";
	public static final String SECTION_EDITOR = "editor";
	public static final String SECTION_BUNNY = "bunny";
	public static final String SECTION_GAME = "game";
	public static final String SECTION_ECONOMY = "economy";
	public static final String SECTION_SCOREBOARD = "scoreboard";
	public static final String SECTION_CALENDAR = "calendar";
	public static final String SECTION_SANITY = "sanity";
	public static final String SECTION_NPC = "npc";
	public static final String SECTION_STATISTIC = "statistic";
	public static final String SECTION_INTEGRATION = "integration";
	public static final String PARAM_STARTUP_PASSIVEMODE = "passiveMode";
	public static final String PARAM_STARTUP_MULTIWORLD = "multiWorld";
	public static final String PARAM_STARTUP_SANITYDELAY = "initialSanityCheckDelay";

	public static final boolean DEFAULT_PASSIVEMODE = false;

	private static boolean multiworld = false;
	private static int initialSanityCheckDelay = 10;

	protected final Plugin plugin;
	protected final TextManager textManager;

	protected ConfigEditor configEditor = new ConfigEditor();
	protected ConfigBunny configBunny = new ConfigBunny();
	protected ConfigGame configGame = new ConfigGame();
	protected ConfigEconomy configEconomy = new ConfigEconomy();
	protected ConfigScoreboard configScoreboard = new ConfigScoreboard();
	protected ConfigCalendar configCalendar = new ConfigCalendar();
	protected ConfigSanity configSanity = new ConfigSanity();
	protected ConfigNpc configNpc = new ConfigNpc();
	protected ConfigStatistic configStatistic = new ConfigStatistic();
	protected ConfigIntegration configIntegration = new ConfigIntegration();

	protected ConfigDesign configDesign = new ConfigDesign();
	protected ConfigSpawnpointCollection configSpawnpointCollection;
	protected ConfigCurrency configCurrency = new ConfigCurrency();
	protected InternalPlayerPersistentScoreCollection internalPlayerPersistentScoreCollection = null;
	protected ConfigShopOfferCollection configShopOfferCollection = new ConfigShopOfferCollection();

	public ConfigManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		configSpawnpointCollection = new ConfigSpawnpointCollection(plugin);
		initializeStaticVariables(); // SafeDefaultConfig is done in Main
		saveDefaultMessageConfig(plugin);
		try {
			reloadMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadInitialIntegrationConfig();
	}

	//
	// Startup section
	//

	public static boolean isPassiveMode(Plugin startupPlugin) {
		return startupPlugin.getConfig().getConfigurationSection(SECTION_STARTUP).getBoolean(PARAM_STARTUP_PASSIVEMODE,
				DEFAULT_PASSIVEMODE);
	}

	public static boolean isMultiworld() {
		return multiworld;
	}

	protected static void setMultiworld(boolean multiworld) {
		ConfigManager.multiworld = multiworld;
	}

	public static int getInitialSanityCheckDelay() {
		return initialSanityCheckDelay;
	}

	protected static void setInitialSanityCheckDelay(int initialSanityCheckDelay) {
		ConfigManager.initialSanityCheckDelay = initialSanityCheckDelay;
	}

	protected void initializeStaticVariables() {
		ConfigurationSection startupConfigurationSection = plugin.getConfig().getConfigurationSection(SECTION_STARTUP);
		setMultiworld(startupConfigurationSection.getBoolean(PARAM_STARTUP_MULTIWORLD, multiworld));
		setInitialSanityCheckDelay(
				startupConfigurationSection.getInt(PARAM_STARTUP_SANITYDELAY, initialSanityCheckDelay));
	}

	//
	// Design section
	//

	public ConfigHead findHead(String headName) {
		return configDesign.findHead(headName);
	}

	public List<ConfigHead> getHeads() {
		return configDesign.getHeads();
	}

	public ConfigEgg findEgg(String eggName) {
		return configDesign.findEgg(eggName);
	}

	public List<ConfigEgg> getEggs() {
		return configDesign.getEggs();
	}

	public ConfigTemplate findTemplate(String templateName) {
		return configDesign.findTemplate(templateName);
	}

	public List<ConfigTemplate> getTemplates() {
		return configDesign.getTemplates();
	}

	//
	// Spawnpoint section
	//

	public ConfigSpawnpoint findSpawnpoint(World world, Coordinate coordinate) {
		return configSpawnpointCollection.findSpawnpoint(world, coordinate);
	}

	public List<ConfigSpawnpoint> getSpawnpoints() {
		return configSpawnpointCollection.getSpawnpoints();
	}

	public List<ConfigSpawnpoint> getSpawnpointsForWorld(World world) {
		return configSpawnpointCollection.getSpawnpointsForWorld(world);
	}

	public List<World> getPopulatedWorlds() {
		return configSpawnpointCollection.getPopulatedWorlds();
	}

	public void addSpawnpoint(ConfigSpawnpoint spawnpoint) throws OstereierException {
		configSpawnpointCollection.addSpawnpoint(spawnpoint);
	}

	public void removeSpawnpoint(ConfigSpawnpoint spawnpoint) throws OstereierException {
		configSpawnpointCollection.removeSpawnpoint(spawnpoint);
	}

	//
	// Subsystem section
	//

	public ConfigEditor getConfigEditor() {
		return configEditor;
	}

	public ConfigBunny getConfigBunny() {
		return configBunny;
	}

	public ConfigGame getConfigGame() {
		return configGame;
	}

	public ConfigEconomy getConfigEconomy() {
		return configEconomy;
	}

	public ConfigScoreboard getConfigScoreboard() {
		return configScoreboard;
	}

	public ConfigCalendar getConfigCalendar() {
		return configCalendar;
	}

	public ConfigSanity getConfigSanity() {
		return configSanity;
	}

	public ConfigNpc getConfigNpc() {
		return configNpc;
	}

	public ConfigStatistic getConfigStatistic() {
		return configStatistic;
	}

	public ConfigIntegration getConfigIntegration() {
		return configIntegration;
	}

	//
	// Currency Section
	//

	public String findCurrency(int amount, String currencyName, CommandSender sender) {
		return configCurrency.findCurrency(amount, currencyName, sender);
	}

	//
	// Shop Section
	//

	public ConfigShopOffer findShopOffer(String offerId) {
		return configShopOfferCollection.findShopOffer(offerId);
	}

	public List<ConfigShopOffer> getShopOffers() {
		return configShopOfferCollection.getShopOffers();
	}

	//
	// Score Section (Internal Economy Provider)
	//

	public InternalPlayerPersistentScore findIPPScore(OfflinePlayer offlinePlayer) {
		if (internalPlayerPersistentScoreCollection != null) {
			return internalPlayerPersistentScoreCollection.findScore(offlinePlayer);
		} else {
			return null;
		}
	}

	public InternalPlayerPersistentScore findOrCreateIPPScore(OfflinePlayer offlinePlayer) {
		if (internalPlayerPersistentScoreCollection != null) {
			return internalPlayerPersistentScoreCollection.findOrCreateScore(offlinePlayer);
		} else {
			return null;
		}
	}

	public List<InternalPlayerPersistentScore> getIPPScores() {
		if (internalPlayerPersistentScoreCollection != null) {
			return internalPlayerPersistentScoreCollection.getScores();
		} else {
			return new ArrayList<>();
		}
	}

	public void saveIPPScores() throws OstereierException {
		if (internalPlayerPersistentScoreCollection != null) {
			internalPlayerPersistentScoreCollection.saveScores();
		}
	}

	public void removeIPPScore(InternalPlayerPersistentScore score) throws OstereierException {
		if (internalPlayerPersistentScoreCollection != null) {
			internalPlayerPersistentScoreCollection.removeScore(score);
		}
	}

	public void openInternalPlayerPersistentScore() throws OstereierException {
		internalPlayerPersistentScoreCollection = new InternalPlayerPersistentScoreCollection(plugin, true);
	}

	public void closeInternalPlayerPersistentScore() throws OstereierException {
		if (internalPlayerPersistentScoreCollection != null) {
			internalPlayerPersistentScoreCollection.saveScores();
			internalPlayerPersistentScoreCollection = null;
		}
	}

	//
	// Admin and reload section
	//

	protected void saveDefaultMessageConfig(Plugin plugin) {
		if (!new File(plugin.getDataFolder(), MESSAGE_FILENAME).exists()) {
			plugin.saveResource(MESSAGE_FILENAME, false);
		}
	}

	protected void reloadMessages() throws OstereierException {
		File messageFile = new File(plugin.getDataFolder(), MESSAGE_FILENAME);
		if (!messageFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_MESSAGE_FILE_MISSING, MESSAGE_FILENAME);
		}
		FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(messageFile);

		textManager.clear();
		for (String myKey : fileConfiguration.getKeys(false)) {
			String myText = fileConfiguration.getString(myKey);
			textManager.addText(myKey, myText);
		}
	}

	protected void loadInitialIntegrationConfig() {
		if (plugin.getConfig().contains(SECTION_INTEGRATION, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_INTEGRATION)) {
			configIntegration = new ConfigIntegration(plugin.getConfig().getConfigurationSection(SECTION_INTEGRATION));
		} else {
			configIntegration = new ConfigIntegration();
		}
	}

	protected void reloadSubsystemConfig() throws OstereierException { // NOSONAR
		plugin.reloadConfig();

		if (plugin.getConfig().contains(SECTION_EDITOR, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_EDITOR)) {
			configEditor = new ConfigEditor(plugin.getConfig().getConfigurationSection(SECTION_EDITOR), textManager);
		} else {
			configEditor = new ConfigEditor();
		}

		if (plugin.getConfig().contains(SECTION_BUNNY, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_BUNNY)) {
			configBunny = new ConfigBunny(plugin.getConfig().getConfigurationSection(SECTION_BUNNY));
		} else {
			configBunny = new ConfigBunny();
		}

		if (plugin.getConfig().contains(SECTION_GAME, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_GAME)) {
			configGame = new ConfigGame(plugin.getConfig().getConfigurationSection(SECTION_GAME));
		} else {
			configGame = new ConfigGame();
		}

		if (plugin.getConfig().contains(SECTION_ECONOMY, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_ECONOMY)) {
			configEconomy = new ConfigEconomy(plugin.getConfig().getConfigurationSection(SECTION_ECONOMY));
		} else {
			configEconomy = new ConfigEconomy();
		}

		if (plugin.getConfig().contains(SECTION_SCOREBOARD, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_SCOREBOARD)) {
			configScoreboard = new ConfigScoreboard(plugin.getConfig().getConfigurationSection(SECTION_SCOREBOARD),
					textManager);
		} else {
			configScoreboard = new ConfigScoreboard();
		}

		if (plugin.getConfig().contains(SECTION_CALENDAR, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_CALENDAR)) {
			configCalendar = new ConfigCalendar(plugin.getConfig().getConfigurationSection(SECTION_CALENDAR));
		} else {
			configCalendar = new ConfigCalendar();
		}

		if (plugin.getConfig().contains(SECTION_SANITY, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_SANITY)) {
			configSanity = new ConfigSanity(plugin.getConfig().getConfigurationSection(SECTION_SANITY));
		} else {
			configSanity = new ConfigSanity();
		}

		if (plugin.getConfig().contains(SECTION_NPC, true) && plugin.getConfig().isConfigurationSection(SECTION_NPC)) {
			configNpc = new ConfigNpc(plugin.getConfig().getConfigurationSection(SECTION_NPC));
		} else {
			configNpc = new ConfigNpc();
		}

		if (plugin.getConfig().contains(SECTION_STATISTIC, true)
				&& plugin.getConfig().isConfigurationSection(SECTION_STATISTIC)) {
			configStatistic = new ConfigStatistic(plugin.getConfig().getConfigurationSection(SECTION_STATISTIC));
		} else {
			configStatistic = new ConfigStatistic();
		}
	}

	public ConfigDesign buildConfigDesignFromLocalConfigFile(Plugin filePlugin) throws OstereierException {
		return new ConfigDesign(filePlugin, getConfigEconomy().getDefaultRewardCurrencyName());
	}

	public ConfigSpawnpointCollection buildDefaultSpawnpointCollection() throws OstereierException {
		return new ConfigSpawnpointCollection(plugin, configDesign);
	}

	protected void reloadDesign(EventManager eventManager) throws OstereierException {
		ReloadDesignResult reloadDesignResult = eventManager.sendReloadDesignEvent();
		if (reloadDesignResult == null) {
			return;
		}
		if (reloadDesignResult.configDesign() != null) {
			configDesign = reloadDesignResult.configDesign();
		} else {
			configDesign = buildConfigDesignFromLocalConfigFile(plugin);
		}
		if (reloadDesignResult.configSpawnpointCollection() != null) {
			configSpawnpointCollection = reloadDesignResult.configSpawnpointCollection();
		} else {
			configSpawnpointCollection = buildDefaultSpawnpointCollection();
		}
	}

	public void reloadConfig(EventManager eventManager) throws OstereierException {

		// 1. Reload Messages
		reloadMessages();

		// 2. Reload System config
		reloadSubsystemConfig();

		// 2. Reload other config parts
		configCurrency = new ConfigCurrency(plugin);
		configShopOfferCollection = new ConfigShopOfferCollection(plugin, configEconomy.getDefaultRewardCurrencyName());

		// 4. Reload Design and Spawnpoints
		reloadDesign(eventManager);

	}

}
