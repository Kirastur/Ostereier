package de.quadrathelden.ostereier.editor;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.chunktickets.ChunkTicketManager;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigSpawnpoint;
import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.config.subsystem.ConfigEditor;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.events.EditorChangeResult;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class EditorManager {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final EventManager eventManager;
	protected final ChunkTicketManager chunkTicketManager;
	protected final DisplayManager displayManager;

	protected EditorListener editorListener = null;
	protected World editorWorld = null;
	protected ConfigTemplate template = null;
	protected boolean hidden;
	protected boolean displayActive;
	protected boolean statusbarActive;
	protected boolean listenerActive;

	public EditorManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
		this.permissionManager = orchestrator.getPermissionManager();
		this.eventManager = orchestrator.getEventManager();
		this.chunkTicketManager = orchestrator.getChunkTicketManager();
		this.displayManager = orchestrator.getDisplayManager();
		this.editorListener = new EditorListener(plugin, textManager, configManager, permissionManager, this);
	}

	//
	// General Functions
	//

	public boolean isEditorActive() {
		return (editorWorld != null);
	}

	public World getEditorWorld() {
		return editorWorld;
	}

	public boolean hasListener() {
		return listenerActive;
	}

	//
	// Statusbar Subsystem
	//

	protected void sendStatusMessageToPlayer(Player player) {
		BaseComponent[] component;
		ConfigEditor configEditor = configManager.getConfigEditor();
		if (isEditorActive()) {
			if (permissionManager.hasEditorPermission(player)) {
				String s = configEditor.getStatusbarReadWriteText(player);
				s = String.format(s, template.getName());
				component = new ComponentBuilder().appendLegacy(s).create();
			} else {
				String s = configEditor.getStatusbarReadOnlyText(player);
				component = new ComponentBuilder().appendLegacy(s).create();
			}
		} else {
			component = new ComponentBuilder().create();
		}
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
	}

	protected void sendStatusMessageToAll() {
		if (!statusbarActive) {
			return;
		}
		for (Player myPlayer : plugin.getServer().getOnlinePlayers()) {
			if ((editorWorld == null) || editorWorld.equals(myPlayer.getWorld())) {
				sendStatusMessageToPlayer(myPlayer);
			}
		}
	}

	//
	// Draw Subsystem
	//

	protected void drawEgg(Coordinate coordinate, ConfigEgg egg) {
		if (displayActive) {
			displayManager.drawEgg(editorWorld, coordinate, egg, true, false);
		}
	}

	protected void undrawEgg(Coordinate coordinate) {
		displayManager.undrawEgg(editorWorld, coordinate); // Ignore displayActive here
	}

	protected void drawAllEggs() {
		if (displayActive) {
			displayManager.drawAllEditorEggs(editorWorld);
		}
	}

	protected void undrawAllEggs() {
		displayManager.undrawAllEggs(editorWorld); // Ignore displayActive here
	}

	public void refreshEggs() {
		undrawAllEggs();
		drawAllEggs();
	}

	//
	// Data Subsystem
	//

	protected ConfigSpawnpoint findSpawnpoint(Coordinate coordinate) {
		return configManager.findSpawnpoint(editorWorld, coordinate);
	}

	protected void addSpawnpoint(ConfigSpawnpoint newSpawnpoint) throws OstereierException {
		configManager.addSpawnpoint(newSpawnpoint);
	}

	protected void removeSpawnpoint(ConfigSpawnpoint oldSpawnpoint) throws OstereierException {
		configManager.removeSpawnpoint(oldSpawnpoint);
	}

	protected boolean configContains(ConfigTemplate testTemplate) {
		return configManager.getTemplates().contains(testTemplate);
	}

	protected ConfigTemplate getInitialConfigTemplate() throws OstereierException {
		if (configManager.getTemplates().isEmpty()) {
			throw new OstereierException(Message.EDITOR_NO_TEMPLATES);
		}
		return configManager.getTemplates().get(0);
	}

	//
	// Business Logic
	//

	public void placeEgg(Coordinate coordinate) throws OstereierException {
		Location thisLocation = coordinate.toLocation(editorWorld);
		Block thisBlock = thisLocation.getBlock();
		Coordinate belowCoordinate = coordinate.down();
		Location belowLocation = belowCoordinate.toLocation(editorWorld);
		Block belowBlock = belowLocation.getBlock();

		if (findSpawnpoint(coordinate) != null) {
			throw new OstereierException(template.getName(), Message.EDITOR_PLACE_DUPLICATE, coordinate.toString());
		}

		if (findSpawnpoint(belowCoordinate) != null) {
			throw new OstereierException(template.getName(), Message.EDITOR_PLACE_EGG_BELOW, coordinate.toString());
		}

		if (thisBlock.getType().isSolid()) {
			throw new OstereierException(template.getName(), Message.EDITOR_PLACE_NOT_EMPTY, coordinate.toString());
		}

		if (belowBlock.getType().isAir()) {
			throw new OstereierException(template.getName(), Message.EDITOR_PLACE_WOULD_HOVER, coordinate.toString());
		}

		if (!belowBlock.getType().isSolid()) {
			throw new OstereierException(template.getName(), Message.EDITOR_PLACE_GROUND_NOT_SOLID,
					coordinate.toString());
		}

		ConfigSpawnpoint newSpawnpoint = new ConfigSpawnpoint(editorWorld, coordinate, template, null);
		addSpawnpoint(newSpawnpoint);
		drawEgg(coordinate, newSpawnpoint.getEditorEgg());
	}

	public void removeEgg(Coordinate coordinate) throws OstereierException {
		ConfigSpawnpoint oldSpawnpoint = findSpawnpoint(coordinate);
		if (oldSpawnpoint == null) {
			throw new OstereierException(null, Message.EDITOR_REMOVE_NO_EGG, coordinate.toString());
		}
		removeSpawnpoint(oldSpawnpoint);
		undrawEgg(coordinate);
	}

	//
	// Trigger Subsystem
	//

	public void playerPlaceClick(Location clickLocation, BlockFace blockFace) throws OstereierException {
		if (!clickLocation.getWorld().equals(editorWorld)) {
			return;
		}

		Coordinate clickCoordinate = Coordinate.of(clickLocation);
		if (findSpawnpoint(clickCoordinate) != null) {
			return;
		}

		Coordinate blockCoordinate = clickCoordinate;
		if (clickLocation.getBlock().getType().isSolid()) {
			blockCoordinate = clickCoordinate.neighbor(blockFace);
		}

		placeEgg(blockCoordinate);
	}

	public void playerRemoveClick(Location clickLocation, BlockFace blockFace) throws OstereierException {
		if (!clickLocation.getWorld().equals(editorWorld)) {
			return;
		}

		Coordinate clickCoordinate = Coordinate.of(clickLocation);
		if (findSpawnpoint(clickCoordinate) == null) {
			clickCoordinate = clickCoordinate.neighbor(blockFace);
		}

		removeEgg(clickCoordinate);
	}

	protected void enableListener() {
		if (!hidden) {
			editorListener.enableListener();
		}
	}

	protected void disableListener() {
		if (!hidden) {
			editorListener.disableListener();
		}
	}

	//
	// Admin Subsystem
	//

	protected void sendEditorChangeEvent() {
		if (hidden) {
			displayActive = false;
			statusbarActive = false;
			listenerActive = false;
		} else {
			EditorChangeResult ecr = eventManager.sendEditorChangeEvent(editorWorld, template);
			displayActive = ecr.displayActive();
			statusbarActive = ecr.statusbarActive();
			listenerActive = ecr.listenerActive();
		}
	}

	public ConfigTemplate getCurrentTemplate() {
		return template;
	}

	public void setTemplate(ConfigTemplate newTemplate) {
		if (newTemplate != null) {
			template = newTemplate;
			sendEditorChangeEvent();
			sendStatusMessageToAll();
		}
	}

	public void enterEditorMode(World world, ConfigTemplate initialTemplate, boolean isHidden)
			throws OstereierException {
		if (isEditorActive()) {
			throw new OstereierException(null, Message.EDITOR_ALREADY_ACTIVE, editorWorld.getName());
		}
		editorWorld = world;
		template = initialTemplate;
		hidden = isHidden;
		sendEditorChangeEvent();
		drawAllEggs();
		sendStatusMessageToAll();
		enableListener();
	}

	public void enterEditorMode(World world) throws OstereierException {
		ConfigTemplate initialTemplate = template;
		if ((initialTemplate == null) || !configContains(initialTemplate)) {
			initialTemplate = getInitialConfigTemplate();
		}
		enterEditorMode(world, initialTemplate, false);
	}

	public void leaveEditorMode() {
		if (!isEditorActive()) {
			return;
		}
		disableListener();
		undrawAllEggs();
		chunkTicketManager.removeAllFromWorld(editorWorld);
		editorWorld = null;
		sendStatusMessageToAll();
		sendEditorChangeEvent();
	}

	public void disable() {
		editorListener = null;
	}

	//
	// Scheduler Subsystem
	//

	public void handleScheduler() {
		sendStatusMessageToAll();
	}

}