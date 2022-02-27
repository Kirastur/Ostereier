package de.quadrathelden.ostereier.commands;

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.quadrathelden.ostereier.config.design.ConfigSpawnpoint;
import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.main.Main;
import de.quadrathelden.ostereier.tools.Message;

public class OstereierCommand implements CommandExecutor {

	public static final String TEXT_EDITOR_START = "commandEditorStart";
	public static final String TEXT_EDITOR_STOP = "commandEditorStop";
	public static final String TEXT_GAME_START = "commandGameStart";
	public static final String TEXT_GAME_STOP = "commandGameStop";
	public static final String TEXT_INFO_EDITOR = "commandInfoEditor";
	public static final String TEXT_INFO_GAME = "commandInfoGame";
	public static final String TEXT_INFO_NONE = "commandInfoNone";
	public static final String TEXT_HELP = "commandHelp";

	public static final String FORMAT_LIST_WORLDS = "%s: %d";
	public static final String FORMAT_LIST_EMPTY = "-";
	public static final String API_MISSING = "Ostereier API missing - is orchestrator running?";

	protected final Main main;
	protected final String commandName;
	protected OstereierTabCompleter tabCompleter;

	public OstereierCommand(Main main, String commandName) {
		this.main = main;
		this.commandName = commandName;
		main.getCommand(commandName).setExecutor(this);
		tabCompleter = new OstereierTabCompleter(main, this);
	}

	public String getCommandName() {
		return commandName;
	}

	protected void cmdHelp(CommandSender sender) {
		String s = String.join(" ", CommandUtils.enumUserActions(sender));
		sender.sendMessage(String.format(CommandUtils.findText(TEXT_HELP, sender), s));
	}

	protected void cmdEditor(CommandSender sender, ParamStartStop editorMode) throws OstereierException {
		if (editorMode == ParamStartStop.STOP) {
			CommandUtils.leaveEditorMode();
			sender.sendMessage(CommandUtils.findText(TEXT_EDITOR_STOP, sender));
			return;
		}
		if (sender instanceof Player player) {
			World world = player.getWorld();
			CommandUtils.enterEditorMode(world);
			sender.sendMessage(CommandUtils.findText(TEXT_EDITOR_START, sender));
		} else {
			throw new OstereierException(Message.CMD_WORLD_NOT_DETECTED);
		}
	}

	protected void cmdTemplate(ConfigTemplate template) {
		CommandUtils.setCurrentTemplate(template);
	}

	protected void cmdGame(CommandSender sender, ParamStartStopAuto gameMode) throws OstereierException {
		if (sender instanceof Player player) {
			World world = player.getWorld();
			if (gameMode == ParamStartStopAuto.START) {
				CommandUtils.startGame(world);
			}
			if (gameMode == ParamStartStopAuto.STOP) {
				CommandUtils.stopGame(world);
			}
			if (gameMode == ParamStartStopAuto.AUTO) {
				if (!CommandUtils.adjustGameToCalendar(world)) { //NOSONAR
					return;
				}
			}
			if (CommandUtils.isGameActive(world)) {
				sender.sendMessage(String.format(CommandUtils.findText(TEXT_GAME_START, sender), world.getName()));
			} else {
				sender.sendMessage(String.format(CommandUtils.findText(TEXT_GAME_STOP, sender), world.getName()));
			}
		} else {
			throw new OstereierException(Message.CMD_WORLD_NOT_DETECTED);
		}
	}

	protected void cmdShop(CommandSender sender) throws OstereierException {
		if (sender instanceof Player player) {
			CommandUtils.openShopGui(player);
		} else {
			throw new OstereierException(Message.CMD_SHOP_NOT_A_PLAYER);
		}
	}

	protected void cmdList(CommandSender sender, ParamConfig paramConfig) {
		if (paramConfig == ParamConfig.EGGS) {
			String s = String.join(" ", CommandUtils.getEggNames());
			sender.sendMessage(s);
			return;
		}
		if (paramConfig == ParamConfig.TEMPLATES) {
			String s = String.join(" ", CommandUtils.getTemplateNames());
			sender.sendMessage(s);
			return;
		}
		if (paramConfig == ParamConfig.WORLDS) {
			for (World myWorld : CommandUtils.getPopulatedWorlds()) {
				int count = 0;
				for (ConfigSpawnpoint mySpawnpoint : CommandUtils.getSpawnpoints()) {
					if (mySpawnpoint.getWorld().equals(myWorld)) {
						count = count + 1;
					}
				}
				String s = String.format(FORMAT_LIST_WORLDS, myWorld.getName(), count);
				if (s.isEmpty()) {
					s = FORMAT_LIST_EMPTY;
				}
				sender.sendMessage(s);
			}
			return;
		}
		sender.sendMessage(Message.ERROR.toString());
	}

	protected void cmdInfo(CommandSender sender) {
		if (CommandUtils.isEditorActive()) {
			String s = CommandUtils.findText(TEXT_INFO_EDITOR, sender);
			s = String.format(s, CommandUtils.getEditorWorld().getName());
			sender.sendMessage(s);
			return;
		}
		if (CommandUtils.hasActiveGames()) {
			Set<String> worldNames = new TreeSet<>();
			for (World myWorld : CommandUtils.getWorldsWithGame()) {
				worldNames.add(myWorld.getName());
			}
			String s = CommandUtils.findText(TEXT_INFO_GAME, sender);
			s = String.format(s, String.join(" ", worldNames));
			sender.sendMessage(s);
			return;
		}
		sender.sendMessage(CommandUtils.findText(TEXT_INFO_NONE, sender));
	}

	protected void cmdReload(CommandSender sender) throws OstereierException {
		CommandUtils.reload(sender);
	}

	protected void dispatchCommand(CommandSender sender, UserAction action, ParamStartStop editorMode,
			ParamStartStopAuto gameMode, ParamConfig paramConfig, ConfigTemplate template) throws OstereierException {
		switch (action) {
		case EDITOR:
			cmdEditor(sender, editorMode);
			break;
		case TEMPLATE:
			cmdTemplate(template);
			break;
		case GAME:
			cmdGame(sender, gameMode);
			break;
		case SHOP:
			cmdShop(sender);
			break;
		case LIST:
			cmdList(sender, paramConfig);
			break;
		case INFO:
			cmdInfo(sender);
			break;
		case RELOAD:
			cmdReload(sender);
			break;
		case HELP:
			cmdHelp(sender);
			break;
		default:
			sender.sendMessage(Message.ERROR.toString());
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!CommandUtils.hasAPI()) {
			sender.sendMessage(API_MISSING);
			return true;
		}
		if (args.length == 0) {
			return false;
		}
		try {
			UserAction action;
			ParamStartStop paramStartStop;
			ParamStartStopAuto paramStartStopAuto;
			ParamConfig paramConfig;
			ConfigTemplate template;
			String actionName = args[0];
			action = CommandUtils.findUserAction(actionName);
			if (action == null) {
				throw new OstereierException(Message.CMD_ACTION_UNKNOWN);
			}
			if (!CommandUtils.hasCommandPermission(sender, action)) {
				throw new OstereierException(Message.CMD_FORBIDDEN);
			}
			if (args.length - 1 > action.getParamCount()) {
				throw new OstereierException(Message.CMD_TOO_MANY_PARAMETERS);
			}

			paramStartStop = CommandUtils.parseParamStartStop(action, args);
			paramStartStopAuto = CommandUtils.parseParamStartStopAuto(action, args);
			paramConfig = CommandUtils.parseParamConfig(action, args);
			template = CommandUtils.parseTemplate(action, args);
			dispatchCommand(sender, action, paramStartStop, paramStartStopAuto, paramConfig, template);
		} catch (OstereierException oe) {
			sender.sendMessage(CommandUtils.getLocalizedFullErrorMessage(oe, sender));
			if (oe.getCause() != null) {
				oe.printStackTrace();
			}
		} catch (Exception e) {
			sender.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
		return true;
	}

}
