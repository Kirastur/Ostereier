package de.quadrathelden.ostereier.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.commands.UserAction;

public class PermissionManager {

	public static final String COMMAND_PREFIX = "ostereier.command";
	public static final String EDITOR_PERMISSON = "ostereier.editor";
	public static final String GAME_PERMISSION = "ostereier.game";
	public static final String SHOP_PERMISSION = "ostereier.shop";
	public static final String ADMIN_PERMISSION = "ostereier.admin";
	public static final String NOTIFY_PERMISSION = "ostereier.notify";

	protected Plugin plugin;

	public PermissionManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
	}

	public boolean hasCommandPermission(CommandSender sender, UserAction action) {
		String permissionName = String.format("%s,%s", COMMAND_PREFIX, action.getCommand());
		return sender.hasPermission(permissionName);
	}

	public boolean hasEditorPermission(CommandSender sender) {
		return sender.hasPermission(EDITOR_PERMISSON);
	}

	public boolean hasGamePermission(CommandSender sender) {
		return sender.hasPermission(GAME_PERMISSION);
	}

	public boolean hasShopPermission(CommandSender sender) {
		return sender.hasPermission(SHOP_PERMISSION);
	}

	public boolean hasAdminPermission(CommandSender sender) {
		return sender.hasPermission(ADMIN_PERMISSION);
	}

	public boolean hasNotifyPermission(CommandSender sender) {
		return sender.hasPermission(NOTIFY_PERMISSION);
	}

}
