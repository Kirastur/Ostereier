package de.quadrathelden.ostereier.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.quadrathelden.ostereier.api.OstereierAPI;
import de.quadrathelden.ostereier.api.OstereierProvider;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigSpawnpoint;
import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class CommandUtils {

	private CommandUtils() {
		throw new IllegalStateException("Utility class");
	}

	private static OstereierAPI getAPI() {
		return OstereierProvider.getAPI();
	}

	protected static boolean hasAPI() {
		return (OstereierProvider.getAPI() != null);
	}

	//
	// UserAction
	//

	protected static UserAction findUserAction(String actionName) {
		for (UserAction myAction : UserAction.values()) {
			if (myAction.getCommand().equalsIgnoreCase(actionName)) {
				return myAction;
			}
		}
		return null;
	}

	protected static List<String> enumUserActions(CommandSender sender) {
		List<String> actionNames = new ArrayList<>();
		for (UserAction myAction : UserAction.values()) {
			if ((sender == null) || getAPI().hasCommandPermission(sender, myAction)) {
				actionNames.add(myAction.getCommand());
			}
		}
		return actionNames;
	}

	//
	// AdminAction
	//

	protected static AdminAction findAdminAction(String actionName) {
		for (AdminAction myAction : AdminAction.values()) {
			if (myAction.getCommand().equalsIgnoreCase(actionName)) {
				return myAction;
			}
		}
		return null;
	}

	protected static List<String> enumAdminActions() {
		List<String> actionNames = new ArrayList<>();
		for (AdminAction myAction : AdminAction.values()) {
			actionNames.add(myAction.getCommand());
		}
		return actionNames;
	}

	//
	// StartStop
	//

	protected static ParamStartStop findParamStartStop(String paramText) {
		for (ParamStartStop myParam : ParamStartStop.values()) {
			if (myParam.hasText(paramText)) {
				return myParam;
			}
		}
		return null;
	}

	protected static List<String> enumParamStartStopNames() {
		List<String> paramNames = new ArrayList<>();
		for (ParamStartStop myParam : ParamStartStop.values()) {
			paramNames.add(myParam.getParam());
		}
		return paramNames;
	}

	protected static ParamStartStop parseParamStartStop(Action action, String[] args) throws OstereierException {
		int paramPosition = action.findPosition(ParamType.STARTSTOP);
		if (paramPosition == 0) {
			return null;
		}
		if (args.length < paramPosition + 1) {
			throw new OstereierException(Message.CMD_MODE_MISSING);
		}
		String paramName = args[paramPosition];
		ParamStartStop param = findParamStartStop(paramName);
		if (param == null) {
			throw new OstereierException(Message.CMD_MODE_UNKNOWN);
		}
		return param;
	}

	//
	// StartStopAuto
	//

	protected static ParamStartStopAuto findParamOnOffAuto(String paramText) {
		for (ParamStartStopAuto myParam : ParamStartStopAuto.values()) {
			if (myParam.hasText(paramText)) {
				return myParam;
			}
		}
		return null;
	}

	protected static List<String> enumParamStartStopAutoNames() {
		List<String> paramNames = new ArrayList<>();
		for (ParamStartStopAuto myParam : ParamStartStopAuto.values()) {
			paramNames.add(myParam.getParamName());
		}
		return paramNames;
	}

	protected static ParamStartStopAuto parseParamStartStopAuto(Action action, String[] args)
			throws OstereierException {
		int paramPosition = action.findPosition(ParamType.STARTSTOPAUTO);
		if (paramPosition == 0) {
			return null;
		}
		if (args.length < paramPosition + 1) {
			throw new OstereierException(Message.CMD_MODE_MISSING);
		}
		String paramName = args[paramPosition];
		ParamStartStopAuto param = findParamOnOffAuto(paramName);
		if (param == null) {
			throw new OstereierException(Message.CMD_MODE_UNKNOWN);
		}
		return param;
	}

	//
	// Config
	//

	protected static ParamConfig findParamConfig(String paramText) {
		for (ParamConfig myParam : ParamConfig.values()) {
			if (myParam.hasText(paramText)) {
				return myParam;
			}
		}
		return null;
	}

	protected static List<String> enumParamConfigNames(Player player) {
		List<String> paramNames = new ArrayList<>();
		for (ParamConfig myParam : ParamConfig.values()) {
			paramNames.add(myParam.getParamName(player));
		}
		return paramNames;
	}

	protected static ParamConfig parseParamConfig(Action action, String[] args) throws OstereierException {
		int paramPosition = action.findPosition(ParamType.CONFIG);
		if (paramPosition == 0) {
			return null;
		}
		if (args.length < paramPosition + 1) {
			throw new OstereierException(Message.CMD_ELEMENT_MISSING);
		}
		String paramName = args[paramPosition];
		ParamConfig param = findParamConfig(paramName);
		if (param == null) {
			throw new OstereierException(Message.CMD_ELEMENT_UNKNOWN);
		}
		return param;
	}

	//
	// World
	//

	protected static World findWorld(String worldName) {
		for (World myWorld : Bukkit.getWorlds()) {
			if (myWorld.getName().equals(worldName)) {
				return myWorld;
			}
		}
		return null;
	}

	protected static List<String> enumWorlds() {
		List<String> worldNames = new ArrayList<>();
		for (World myWorld : Bukkit.getWorlds()) {
			worldNames.add(myWorld.getName());
		}
		return worldNames;
	}

	protected static World parseWorld(Action action, String[] args) throws OstereierException {
		int worldPosition = action.findPosition(ParamType.WORLDS);
		if (worldPosition == 0) {
			return null;
		}
		if (args.length < worldPosition + 1) {
			throw new OstereierException(Message.CMD_WORLD_MISSING);
		}
		String worldName = args[worldPosition];
		World world = findWorld(worldName);
		if (world == null) {
			throw new OstereierException(Message.CMD_WORLD_UNKNOWN);
		}
		return world;
	}

	//
	// Player
	//

	protected static Player findPlayer(String playerName) {
		return Bukkit.getPlayer(playerName);
	}

	protected static Player parsePlayer(Action action, String[] args) throws OstereierException {
		int playerPosition = action.findPosition(ParamType.PLAYER);
		if (playerPosition == 0) {
			return null;
		}
		if (args.length < playerPosition + 1) {
			throw new OstereierException(Message.CMD_PLAYER_MISSING);
		}
		String playerName = args[playerPosition];
		Player player = findPlayer(playerName);
		if (player == null) {
			throw new OstereierException(Message.CMD_PLAYER_UNKNOWN);
		}
		return player;
	}

	//
	// Eggs
	//

	protected static Set<String> getEggNames() {
		Set<String> eggNames = new TreeSet<>();
		for (ConfigEgg myEgg : getAPI().getEggs()) {
			eggNames.add(myEgg.getName());
		}
		return eggNames;
	}

	//
	// Templates
	//

	protected static Set<String> getTemplateNames() {
		Set<String> templateNames = new TreeSet<>();
		for (ConfigTemplate myTemplate : getAPI().getTemplates()) {
			templateNames.add(myTemplate.getName());
		}
		return templateNames;
	}

	protected static ConfigTemplate findTemplate(String templateName) {
		for (ConfigTemplate myTemplate : getAPI().getTemplates()) {
			if (myTemplate.getName().equals(templateName)) {
				return myTemplate;
			}
		}
		return null;
	}

	protected static List<String> enumTemplates() {
		List<String> templateNames = new ArrayList<>();
		for (ConfigTemplate myTemplate : getAPI().getTemplates()) {
			templateNames.add(myTemplate.getName());
		}
		return templateNames;
	}

	protected static ConfigTemplate parseTemplate(Action action, String[] args) throws OstereierException {
		int templatePosition = action.findPosition(ParamType.TEMPLATES);
		if (templatePosition == 0) {
			return null;
		}
		if (args.length < templatePosition + 1) {
			throw new OstereierException(Message.CMD_TEMPLATE_MISSING);
		}
		String templateName = args[templatePosition];
		ConfigTemplate template = findTemplate(templateName);
		if (template == null) {
			throw new OstereierException(Message.CMD_TEMPLATE_UNKNOWN);
		}
		return template;
	}

	//
	// Other calculations
	//

	public static Player getNearestPlayer(Location sourcelocation) {
		Player targetPlayer = null;
		double targetDistance = 0.0;
		for (Player myPlayer : sourcelocation.getWorld().getPlayers()) {
			if (targetPlayer == null) {
				targetPlayer = myPlayer;
				targetDistance = targetPlayer.getLocation().distance(sourcelocation);
			} else {
				double myDistance = myPlayer.getLocation().distance(sourcelocation);
				if (myDistance < targetDistance) {
					targetPlayer = myPlayer;
					targetDistance = myDistance;
				}
			}
		}
		return targetPlayer;
	}

	//
	// Wrap API Calls
	//

	protected static String findText(String name, CommandSender sender) {
		return getAPI().findText(name, sender);
	}

	protected static String getLocalizedFullErrorMessage(OstereierException oe, CommandSender sender) {
		return getAPI().getLocalizedFullErrorMessage(oe, sender);
	}

	protected static List<World> getPopulatedWorlds() {
		return getAPI().getPopulatedWorlds();
	}

	protected static List<ConfigSpawnpoint> getSpawnpoints() {
		return getAPI().getSpawnpoints();
	}

	protected static boolean hasCommandPermission(CommandSender sender, UserAction userAction) {
		return getAPI().hasCommandPermission(sender, userAction);
	}

	protected static boolean isEditorActive() {
		return getAPI().isEditorActive();
	}

	protected static World getEditorWorld() {
		return getAPI().getEditorWorld();
	}

	protected static void enterEditorMode(CommandSender initiator, World world) throws OstereierException {
		getAPI().enterEditorMode(initiator, world);
	}

	protected static void leaveEditorMode(CommandSender initiator) throws OstereierException {
		getAPI().leaveEditorMode(initiator);
	}

	protected static void setCurrentTemplate(ConfigTemplate template) {
		getAPI().setCurrentTemplate(template);
	}

	protected static boolean hasActiveGames() {
		return getAPI().hasActiveGames();
	}

	protected static boolean isGameActive(World world) {
		return getAPI().isGameActive(world);
	}

	protected static List<World> getWorldsWithGame() {
		return getAPI().getWorldsWithGame();
	}

	protected static void startGame(CommandSender initiator, World world) throws OstereierException {
		getAPI().startGame(initiator, world);
	}

	protected static void stopGame(CommandSender initiator, World world) throws OstereierException {
		getAPI().stopGame(initiator, world);
	}

	protected static boolean adjustGameToCalendar(CommandSender initiator, World world) throws OstereierException {
		return getAPI().adjustGameToCalendar(initiator, world);
	}

	public static void adjustGameToCalendarInAllWorlds(CommandSender initiator) throws OstereierException {
		getAPI().adjustGameToCalendarInAllWorlds(initiator);
	}

	protected static void openShopGui(Player player) throws OstereierException {
		getAPI().openShopGui(player);
	}

	protected static int performSanityCheck(CommandSender initiator, World world) {
		return getAPI().performSanityCheck(initiator, world);
	}

	protected static void reload(CommandSender sender) throws OstereierException {
		getAPI().reload(sender);
	}

}
