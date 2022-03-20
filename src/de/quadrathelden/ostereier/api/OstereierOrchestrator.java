package de.quadrathelden.ostereier.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.chunktickets.ChunkTicketManager;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.editor.EditorManager;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.integrations.IntegrationManager;
import de.quadrathelden.ostereier.mode.ModeManager;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.scoreboard.ScoreboardManager;
import de.quadrathelden.ostereier.shop.ShopManager;
import de.quadrathelden.ostereier.statistics.StatisticManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class OstereierOrchestrator {

	public static final String PLUGIN_NAME = "Ostereier";

	private final Plugin plugin;
	private final TextManager textManager;
	private final ConfigManager configManager;
	private final PermissionManager permissionManager;
	private final EventManager eventManager;
	private final ChunkTicketManager chunkTicketManager;
	private final IntegrationManager integrationManager;
	private final EconomyManager economyManager;
	private final StatisticManager statisticManager;
	private final ScoreboardManager scoreboardManager;
	private final DisplayManager displayManager;
	private final EditorManager editorManager;
	private final GameManager gameManager;
	private final ShopManager shopManager;
	private final ModeManager modeManager;

	public OstereierOrchestrator(Plugin plugin) throws OstereierException {
		if (plugin != null) {
			this.plugin = plugin;
		} else {
			this.plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
		}
		testAPI();

		textManager = createTextManager();
		configManager = createConfigManager();
		permissionManager = createPermissionManager();
		eventManager = createEventManager();
		chunkTicketManager = createChunkTicketManager();
		integrationManager = createIntegrationManager();
		economyManager = createEconomyManager();
		statisticManager = createStatisticManager();
		scoreboardManager = createScoreboardManager();
		displayManager = createDisplayManager();
		editorManager = createEditorManager();
		gameManager = createGameManager();
		shopManager = createShopManager();
		modeManager = createModeManager();
		setAPI();
	}

	// Getter
	public Plugin getPlugin() {
		return plugin;
	}

	public TextManager getTextManager() {
		return textManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public ChunkTicketManager getChunkTicketManager() {
		return chunkTicketManager;
	}

	public IntegrationManager getIntegrationManager() {
		return integrationManager;
	}

	public EconomyManager getEconomyManager() {
		return economyManager;
	}

	public StatisticManager getStatisticManager() {
		return statisticManager;
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public DisplayManager getDisplayManager() {
		return displayManager;
	}

	public EditorManager getEditorManager() {
		return editorManager;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public ShopManager getShopManager() {
		return shopManager;
	}

	public ModeManager getModeManager() {
		return modeManager;
	}

	// Creator
	protected TextManager createTextManager() {
		return new TextManager(this);
	}

	protected ConfigManager createConfigManager() {
		return new ConfigManager(this);
	}

	protected PermissionManager createPermissionManager() {
		return new PermissionManager(this);
	}

	protected EventManager createEventManager() {
		return new EventManager(this);
	}

	protected ChunkTicketManager createChunkTicketManager() {
		return new ChunkTicketManager(this);
	}

	protected IntegrationManager createIntegrationManager() {
		return new IntegrationManager(this);
	}

	protected EconomyManager createEconomyManager() {
		return new EconomyManager(this);
	}

	protected StatisticManager createStatisticManager() {
		return new StatisticManager(this);
	}

	protected ScoreboardManager createScoreboardManager() {
		return new ScoreboardManager(this);
	}

	protected DisplayManager createDisplayManager() {
		return new DisplayManager(this);
	}

	protected EditorManager createEditorManager() {
		return new EditorManager(this);
	}

	protected GameManager createGameManager() {
		return new GameManager(this);
	}

	protected ShopManager createShopManager() {
		return new ShopManager(this);
	}

	protected ModeManager createModeManager() {
		return new ModeManager(this);
	}

	protected OstereierAPI createAPI() {
		return new OstereierAPI(this);
	}

	// API
	protected void testAPI() throws OstereierException {
		if (OstereierProvider.hasAPI()) {
			throw new OstereierException(this.plugin.getName(), Message.ORCHESTRATOR_ALREADY_RUNNING, null);
		}
	}

	protected void setAPI() {
		OstereierProvider.setAPI(createAPI());
	}

	protected void clearAPI() {
		OstereierProvider.setAPI(null);
	}

	// Deactivation
	public void disable() {
		modeManager.disable();
		clearAPI();
	}

}
