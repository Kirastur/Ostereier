package de.quadrathelden.ostereier.commands;

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.main.Main;
import de.quadrathelden.ostereier.tools.Message;

public class AdminCommand implements CommandExecutor {

	protected final Main main;
	protected final String commandName;
	protected AdminTabCompleter tabCompleter;

	public AdminCommand(Main main, String commandName) {
		this.main = main;
		this.commandName = commandName;
		main.getCommand(commandName).setExecutor(this);
		tabCompleter = new AdminTabCompleter(main, this);
	}

	public String getCommandName() {
		return commandName;
	}

	protected void cmdHelp(CommandSender sender) {
		String s = String.join(" ", CommandUtils.enumAdminActions());
		sender.sendMessage(String.format(CommandUtils.findText(OstereierCommand.TEXT_HELP, sender), s));
	}

	protected void cmdEditorStart(CommandSender sender, World world) throws OstereierException {
		CommandUtils.enterEditorMode(sender, world);
	}

	protected void cmdEditorStop(CommandSender sender) throws OstereierException {
		CommandUtils.leaveEditorMode(sender);
	}

	protected void cmdGameStart(CommandSender sender, World world) throws OstereierException {
		CommandUtils.startGame(sender, world);
	}

	protected void cmdGameStop(CommandSender sender, World world) throws OstereierException {
		CommandUtils.stopGame(sender, world);
	}

	protected void cmdGameAuto(CommandSender sender, World world) throws OstereierException {
		CommandUtils.adjustGameToCalendar(sender, world);
	}

	protected void cmdGameAutoAll(CommandSender sender) throws OstereierException {
		CommandUtils.adjustGameToCalendarInAllWorlds(sender);
	}

	protected void cmdCheckWorld(CommandSender sender, World world) {
		int i = CommandUtils.performSanityCheck(sender, world);
		if (i == 0) {
			sender.sendMessage(CommandUtils.findText(Message.OK.name(), sender));
		}
	}

	protected void cmdShop(Player player) throws OstereierException {
		CommandUtils.openShopGui(player);
	}

	protected void cmdShopNearest(CommandSender sender) throws OstereierException {
		Location sourceLocation = null;
		if (sender instanceof Entity entity) {
			sourceLocation = entity.getLocation();
		}
		if (sender instanceof BlockCommandSender blockCommandSender) {
			sourceLocation = blockCommandSender.getBlock().getLocation().add(new Vector(0.5, 0.0, 0.5));
		}
		if (sourceLocation != null) {
			Player player = CommandUtils.getNearestPlayer(sourceLocation);
			if (player != null) {
				CommandUtils.openShopGui(player);
			}
		}
	}

	protected void cmdInfo(CommandSender sender) {
		if (CommandUtils.isEditorActive()) {
			String s = CommandUtils.findText(OstereierCommand.TEXT_INFO_EDITOR, sender);
			s = String.format(s, CommandUtils.getEditorWorld().getName());
			sender.sendMessage(s);
			return;
		}
		if (CommandUtils.hasActiveGames()) {
			Set<String> worldNames = new TreeSet<>();
			for (World myWorld : CommandUtils.getWorldsWithGame()) {
				worldNames.add(myWorld.getName());
			}
			String s = CommandUtils.findText(OstereierCommand.TEXT_INFO_GAME, sender);
			s = String.format(s, String.join(" ", worldNames));
			sender.sendMessage(s);
			return;
		}
		sender.sendMessage(CommandUtils.findText(OstereierCommand.TEXT_INFO_NONE, sender));
	}

	protected void cmdReload(CommandSender sender) throws OstereierException {
		CommandUtils.reload(sender);
	}

	protected void dispatchCommand(CommandSender sender, AdminAction action, World world, Player player)
			throws OstereierException {
		switch (action) {
		case EDITORSTART:
			cmdEditorStart(sender, world);
			break;
		case EDITORSTOP:
			cmdEditorStop(sender);
			break;
		case GAMESTART:
			cmdGameStart(sender, world);
			break;
		case GAMESTOP:
			cmdGameStop(sender, world);
			break;
		case GAMEAUTO:
			cmdGameAuto(sender, world);
			break;
		case GAMEAUTOALL:
			cmdGameAutoAll(sender);
			break;
		case CHECKWORLD:
			cmdCheckWorld(sender, world);
			break;
		case SHOP:
			cmdShop(player);
			break;
		case SHOPNEAREST:
			cmdShopNearest(sender);
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
			sender.sendMessage(OstereierCommand.API_MISSING);
			return true;
		}
		if (args.length == 0) {
			return false;
		}
		try {
			AdminAction action;
			World world;
			Player player;
			String actionName = args[0];
			action = CommandUtils.findAdminAction(actionName);
			if (action == null) {
				throw new OstereierException(Message.CMD_ACTION_UNKNOWN);
			}
			if (args.length - 1 > action.getParamCount()) {
				throw new OstereierException(Message.CMD_TOO_MANY_PARAMETERS);
			}

			world = CommandUtils.parseWorld(action, args);
			player = CommandUtils.parsePlayer(action, args);

			dispatchCommand(sender, action, world, player);
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
