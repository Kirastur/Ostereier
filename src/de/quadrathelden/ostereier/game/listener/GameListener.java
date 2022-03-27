package de.quadrathelden.ostereier.game.listener;

import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.text.TextManager;

public class GameListener {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final DisplayManager displayManager;
	protected final GameManager gameManager;

	protected GamePlayerInteractListener gamePlayerInteractListener = null;
	protected GamePlayerMoveListener gamePlayerMoveListener = null;
	protected GamePlayerPickupItemListener gamePlayerPickupItemListener = null;
	protected GameProtectedAreaListener gameProtectedAreaListener = null;

	public GameListener(Plugin plugin, TextManager textManager, ConfigManager configManager,
			PermissionManager permissionManager, DisplayManager displayManager, GameManager gameManager) {
		this.plugin = plugin;
		this.textManager = textManager;
		this.configManager = configManager;
		this.permissionManager = permissionManager;
		this.displayManager = displayManager;
		this.gameManager = gameManager;
	}

	protected void updateGamePlayerInteractListener() {
		if (gameManager.hasActiveGames()
				&& (configManager.getConfigGame().isCollectUsingPlayerLeftClick()
						|| configManager.getConfigGame().isCollectUsingPlayerRightClick())
				&& (gamePlayerInteractListener == null)) {
			gamePlayerInteractListener = new GamePlayerInteractListener(plugin, textManager, configManager,
					permissionManager, displayManager, gameManager);
			gamePlayerInteractListener.registerListener();
		}
		if (!gameManager.hasActiveGames() && (gamePlayerInteractListener != null)) {
			gamePlayerInteractListener.unregisterListener();
			gamePlayerInteractListener = null;
		}
	}

	protected void updateGamePlayerMoveListener() {
		if (gameManager.hasActiveGames() && configManager.getConfigGame().isCollectUsingPlayerMoveToEgg()
				&& (gamePlayerMoveListener == null)) {
			gamePlayerMoveListener = new GamePlayerMoveListener(plugin, textManager, configManager, permissionManager,
					gameManager);
			gamePlayerMoveListener.registerListener();
		}
		if (!gameManager.hasActiveGames() && (gamePlayerMoveListener != null)) {
			gamePlayerMoveListener.unregisterListener();
			gamePlayerMoveListener = null;
		}
	}

	protected void updateGamePlayerPickupItemListener() {
		if (gameManager.hasActiveGames() && configManager.getConfigGame().isCollectUsingPlayerPickupItem()
				&& (gamePlayerPickupItemListener == null)) {
			gamePlayerPickupItemListener = new GamePlayerPickupItemListener(plugin, textManager, configManager,
					permissionManager, gameManager);
			gamePlayerPickupItemListener.registerListener();
		}
		if (!gameManager.hasActiveGames() && (gamePlayerPickupItemListener != null)) {
			gamePlayerPickupItemListener.unregisterListener();
			gamePlayerPickupItemListener = null;
		}
	}

	protected void updateGameProtectedAreaListener() {
		if (gameManager.hasActiveGames() && (configManager.getConfigGame().getProtectedAreaAroundEggs() >= 0)
				&& (gameProtectedAreaListener == null)) {
			gameProtectedAreaListener = new GameProtectedAreaListener(plugin, configManager, permissionManager,
					gameManager);
			gameProtectedAreaListener.registerListener();
		}
		if ((!gameManager.hasActiveGames() || (configManager.getConfigGame().getProtectedAreaAroundEggs() < 0))
				&& (gameProtectedAreaListener != null)) {
			gameProtectedAreaListener.unregisterListener();
			gameProtectedAreaListener = null;
		}

	}

	public void updateListener() {
		updateGamePlayerInteractListener();
		updateGamePlayerMoveListener();
		updateGamePlayerPickupItemListener();
		updateGameProtectedAreaListener();
	}
}
