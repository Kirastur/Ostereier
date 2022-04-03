package de.quadrathelden.ostereier.api;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.quadrathelden.ostereier.commands.UserAction;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigHead;
import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.config.shop.ConfigShopOffer;
import de.quadrathelden.ostereier.config.spawnpoints.ConfigSpawnpoint;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.economy.EconomyProvider;
import de.quadrathelden.ostereier.editor.EditorManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.game.egg.GameEgg;
import de.quadrathelden.ostereier.game.world.PlayerScore;
import de.quadrathelden.ostereier.integrations.IntegrationManager;
import de.quadrathelden.ostereier.mode.ModeManager;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.scoreboard.ScoreboardManager;
import de.quadrathelden.ostereier.shop.ItemSeal;
import de.quadrathelden.ostereier.shop.ShopManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;

public class OstereierAPI {

	private final TextManager textManager;
	private final ConfigManager configManager;
	private final PermissionManager permissionManager;
	private final IntegrationManager integrationManager;
	private final EconomyManager economyManager;
	private final ScoreboardManager scoreboardManager;
	private final DisplayManager displayManager;
	private final EditorManager editorManager;
	private final GameManager gameManager;
	private final ShopManager shopManager;
	private final ModeManager modeManager;

	public OstereierAPI(OstereierOrchestrator orchestrator) {
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
		this.permissionManager = orchestrator.getPermissionManager();
		this.integrationManager = orchestrator.getIntegrationManager();
		this.economyManager = orchestrator.getEconomyManager();
		this.scoreboardManager = orchestrator.getScoreboardManager();
		this.displayManager = orchestrator.getDisplayManager();
		this.editorManager = orchestrator.getEditorManager();
		this.gameManager = orchestrator.getGameManager();
		this.shopManager = orchestrator.getShopManager();
		this.modeManager = orchestrator.getModeManager();
	}

	// TextManager
	public String findText(String name, CommandSender sender) {
		return textManager.findText(name, sender);
	}

	public String getLocalizedFullErrorMessage(OstereierException oe, CommandSender sender) {
		return (oe.getLocalizedFullErrorMessage(textManager, sender));
	}

	// ConfigManager
	public ConfigHead findHead(String headName) {
		return configManager.findHead(headName);
	}

	public List<ConfigHead> getHeads() {
		return configManager.getHeads();
	}

	public ConfigEgg findEgg(String eggName) {
		return configManager.findEgg(eggName);
	}

	public List<ConfigEgg> getEggs() {
		return configManager.getEggs();
	}

	public ConfigTemplate findTemplate(String templateName) {
		return configManager.findTemplate(templateName);
	}

	public List<ConfigTemplate> getTemplates() {
		return configManager.getTemplates();
	}

	public ConfigSpawnpoint findSpawnpoint(World world, Coordinate coordinate) {
		return configManager.findSpawnpoint(world, coordinate);
	}

	public List<ConfigSpawnpoint> getSpawnpoints() {
		return configManager.getSpawnpoints();
	}

	public List<ConfigSpawnpoint> getSpawnpointsForWorld(World world) {
		return configManager.getSpawnpointsForWorld(world);
	}

	public List<World> getPopulatedWorlds() {
		return configManager.getPopulatedWorlds();
	}

	public String findCurrency(int amount, String currencyName, CommandSender sender) {
		return configManager.findCurrency(amount, currencyName, sender);
	}

	public ConfigShopOffer findShopeOffer(String offerId) {
		return configManager.findShopOffer(offerId);
	}

	public List<ConfigShopOffer> getShopOffers() {
		return configManager.getShopOffers();
	}

	// PermissionManager
	public boolean hasCommandPermission(CommandSender sender, UserAction action) {
		return permissionManager.hasCommandPermission(sender, action);
	}

	public boolean hasEditorPermission(CommandSender sender) {
		return permissionManager.hasEditorPermission(sender);
	}

	public boolean hasGamePermission(CommandSender sender) {
		return permissionManager.hasGamePermission(sender);
	}

	public boolean hasShopPermission(CommandSender sender) {
		return permissionManager.hasShopPermission(sender);
	}

	public boolean hasAdminPermission(CommandSender sender) {
		return permissionManager.hasAdminPermission(sender);
	}

	public boolean hasNotifyPermission(CommandSender sender) {
		return permissionManager.hasNotifyPermission(sender);
	}

	// IntegrationManager
	public Set<String> getActiveIntegrations() {
		return integrationManager.getActiveIntegrations();
	}

	// EconomyProvider
	public EconomyProvider getEconomyProvider() {
		return economyManager.getEconomyProvider();
	}

	public String refineRewardCurrencyName(String currencyName) {
		return economyManager.refineRewardCurrencyName(currencyName);
	}

	// ScoreboardManager
	public void updateScoreboard(Player player) throws OstereierException {
		scoreboardManager.updateScoreboard(player);
	}

	public void removeScoreboard(Player player) {
		scoreboardManager.removeScoreboard(player);
	}

	public void addScheduledUpdate(Player player) {
		scoreboardManager.addScheduledUpdate(player);
	}

	// DisplayManafer
	public Coordinate findSpawnpointCoordinate(Entity entity) {
		return displayManager.findSpawnpointCoordinate(entity);
	}

	// EditorManager
	public boolean isEditorActive() {
		return editorManager.isEditorActive();
	}

	public World getEditorWorld() {
		return editorManager.getEditorWorld();
	}

	public ConfigTemplate getCurrentTemplate() {
		return editorManager.getCurrentTemplate();
	}

	public void setCurrentTemplate(ConfigTemplate newTemplate) {
		editorManager.setTemplate(newTemplate);
	}

	public void placeEgg(Coordinate coordinate) throws OstereierException {
		if (!isEditorActive()) {
			throw new OstereierException(Message.API_EDITOR_NOT_ACTIVE);
		}
		editorManager.placeEgg(coordinate);
	}

	public void removeEgg(Coordinate coordinate) throws OstereierException {
		if (!isEditorActive()) {
			throw new OstereierException(Message.API_EDITOR_NOT_ACTIVE);
		}
		editorManager.removeEgg(coordinate);
	}

	public void playerPlaceClick(Location clickLocation, BlockFace blockFace) throws OstereierException {
		if (!isEditorActive()) {
			throw new OstereierException(Message.API_EDITOR_NOT_ACTIVE);
		}
		editorManager.playerPlaceClick(clickLocation, blockFace);
	}

	public void playerRemoveClick(Location clickLocation, BlockFace blockFace) throws OstereierException {
		if (!isEditorActive()) {
			throw new OstereierException(Message.API_EDITOR_NOT_ACTIVE);
		}
		editorManager.playerRemoveClick(clickLocation, blockFace);
	}

	public void refreshEditorEggs() throws OstereierException {
		if (!isEditorActive()) {
			throw new OstereierException(Message.API_EDITOR_NOT_ACTIVE);
		}
		editorManager.refreshEggs();
	}

	// GameManager
	public boolean hasActiveGames() {
		return gameManager.hasActiveGames();
	}

	public boolean isGameActive(World world) {
		return gameManager.isGameActive(world);
	}

	public List<World> getWorldsWithGame() {
		return gameManager.getWorldsWithGame();
	}

	public List<GameEgg> getGameEggs(World world) {
		return gameManager.getGameEggs(world);
	}

	public List<PlayerScore> getPlayerScores(World world) {
		return gameManager.getPlayerScores(world);
	}

	public boolean playerClickToCollect(Player player, Location clickLocation, BlockFace blockFace)
			throws OstereierException {
		return gameManager.playerClickToCollect(player, clickLocation, blockFace);
	}

	public int playerMoveToCollect(Player player, Location toLocation) throws OstereierException {
		return gameManager.playerMoveToCollect(player, toLocation);
	}

	public boolean playerPickupItemToCollect(Player player, Item item) throws OstereierException {
		return gameManager.playerPickupItemToCollect(player, item);
	}

	public boolean isProtectedArea(Location location) {
		return gameManager.isProtectedArea(location);
	}

	// ShopManager
	public ItemSeal readItemSeal(ItemStack itemStack) {
		return shopManager.readItemSeal(itemStack);
	}

	public void localizeSealedItemStack(ItemStack itemStack, CommandSender sender) {
		shopManager.localizeSealedItemStack(itemStack, sender);
	}

	public ItemStack buildItemStack(Player player, ConfigShopOffer offer) {
		return shopManager.buildItemStack(player, offer);
	}

	public Inventory openShopGui(Player player) throws OstereierException {
		return shopManager.openShopGui(player);
	}

	// Mode Manager StateEngine
	public void enterEditorMode(CommandSender initiator, World world) throws OstereierException {
		modeManager.enterEditorMode(initiator, world);
	}

	public void enterEditorMode(CommandSender initiator, World world, ConfigTemplate initialTemplate, boolean isHidden)
			throws OstereierException {
		modeManager.enterEditorMode(initiator, world, initialTemplate, isHidden);
	}

	public void leaveEditorMode(CommandSender initiator) throws OstereierException {
		modeManager.leaveEditorMode(initiator);
	}

	public void startGame(CommandSender initiator, World world) throws OstereierException {
		modeManager.startGame(initiator, world);
	}

	public boolean stopGame(CommandSender initiator, World world) throws OstereierException {
		return modeManager.stopGame(initiator, world);
	}

	// ModeManager Calendar
	public boolean isCalendarActive() {
		return modeManager.isCalendarActive();
	}

	public boolean adjustGameToCalendar(CommandSender initiator, World world) throws OstereierException {
		return modeManager.adjustGameToCalendar(initiator, world);
	}

	public void adjustGameToCalendarInAllWorlds(CommandSender initiator) throws OstereierException {
		modeManager.adjustGameToCalendarInAllWorlds(initiator);
	}

	public void reload(CommandSender initiator) throws OstereierException {
		modeManager.reload(initiator);
	}

	public int performSanityCheck(CommandSender initiator, World world) {
		return modeManager.performSanityCheck(initiator, world);
	}

	// Disable
	public boolean isDisabled() {
		return modeManager.isDisabled();
	}

}
