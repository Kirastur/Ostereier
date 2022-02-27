package de.quadrathelden.ostereier.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.quadrathelden.ostereier.main.Main;
import de.quadrathelden.ostereier.tools.Message;

public class OstereierTabCompleter implements TabCompleter {

	public OstereierTabCompleter(Main main, OstereierCommand ostereierCommand) {
		main.getCommand(ostereierCommand.getCommandName()).setTabCompleter(this);
	}

	protected List<String> handleTabComplete(CommandSender sender, String[] args) {
		Player player = null;
		if (sender instanceof Player senderPlayer) {
			player = senderPlayer;
		}
		if (args.length < 1) {
			return new ArrayList<>();
		}
		if (args.length == 1) {
			return CommandUtils.enumUserActions(sender);
		}

		String actionName = args[0];
		UserAction action = CommandUtils.findUserAction(actionName);
		if ((action == null) || (args.length - 1 > action.getParamCount())) {
			return new ArrayList<>();
		}

		ParamType paramType = action.getParam(args.length - 1);
		if (paramType == ParamType.STARTSTOP) {
			return CommandUtils.enumParamStartStopNames();
		}
		if (paramType == ParamType.STARTSTOPAUTO) {
			return CommandUtils.enumParamStartStopAutoNames();
		}
		if (paramType == ParamType.CONFIG) {
			return CommandUtils.enumParamConfigNames(player);
		}
		if (paramType == ParamType.WORLDS) {
			return CommandUtils.enumWorlds();
		}
		if (paramType == ParamType.TEMPLATES) {
			return CommandUtils.enumTemplates();
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			return handleTabComplete(sender, args);
		} catch (Exception e) {
			sender.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
