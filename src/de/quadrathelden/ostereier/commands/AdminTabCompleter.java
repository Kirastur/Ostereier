package de.quadrathelden.ostereier.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.quadrathelden.ostereier.main.Main;
import de.quadrathelden.ostereier.tools.Message;

public class AdminTabCompleter implements TabCompleter {

	public AdminTabCompleter(Main main, AdminCommand adminCommand) {
		main.getCommand(adminCommand.getCommandName()).setTabCompleter(this);
	}

	protected List<String> handleTabComplete(String[] args) {
		if (args.length < 1) {
			return new ArrayList<>();
		}
		if (args.length == 1) {
			return CommandUtils.enumAdminActions();
		}

		String actionName = args[0];
		AdminAction action = CommandUtils.findAdminAction(actionName);
		if ((action == null) || (args.length - 1 > action.getParamCount())) {
			return new ArrayList<>();
		}

		ParamType paramType = action.getParam(args.length - 1);
		if (paramType == ParamType.WORLDS) {
			return CommandUtils.enumWorlds();
		}
		if (paramType == ParamType.PLAYER) {
			return null; // NOSONAR
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			return handleTabComplete(args);
		} catch (Exception e) {
			sender.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
