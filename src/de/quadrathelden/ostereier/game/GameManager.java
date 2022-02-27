package de.quadrathelden.ostereier.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.bunny.Bunny;
import de.quadrathelden.ostereier.bunny.DefaultBunny;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigSpawnpoint;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.egg.GameEgg;
import de.quadrathelden.ostereier.game.egg.GameEggDefault;
import de.quadrathelden.ostereier.game.egg.GameHelper;
import de.quadrathelden.ostereier.game.listener.GameListener;
import de.quadrathelden.ostereier.game.world.GameWorld;
import de.quadrathelden.ostereier.game.world.PlayerScore;
import de.quadrathelden.ostereier.permissions.PermissionManager;
import de.quadrathelden.ostereier.scoreboard.ScoreboardManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Coordinate;

public class GameManager {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final PermissionManager permissionManager;
	protected final EventManager eventManager;
	protected final EconomyManager economyManager;
	protected final ScoreboardManager scoreboardManager;
	protected final DisplayManager displayManager;

	protected GameListener gameListener = null;
	protected List<GameWorld> gameWorlds = new ArrayList<>();

	public GameManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
		this.permissionManager = orchestrator.getPermissionManager();
		this.eventManager = orchestrator.getEventManager();
		this.economyManager = orchestrator.getEconomyManager();
		this.scoreboardManager = orchestrator.getScoreboardManager();
		this.displayManager = orchestrator.getDisplayManager();
		this.gameListener = new GameListener(plugin, textManager, configManager, permissionManager, this);
	}

	public Bunny buildBunny(World world) {
		Bunny newBunny = eventManager.sendGameStartEvent(world);
		if (newBunny == null) {
			newBunny = new DefaultBunny(configManager.getConfigBunny());
		}
		return newBunny;
	}

	public GameHelper buildGameHelper(GameWorld gameWorld, Coordinate coordinate) {
		return new GameHelper(gameWorld, configManager.getConfigGame(), eventManager, displayManager, coordinate);
	}

	public GameEgg buildGameEgg(World world, ConfigEgg configEgg, ConfigSpawnpoint configSpawnpoint) {
		GameEgg newGameEgg = eventManager.sendEggSpawnEvent(world, configEgg, configSpawnpoint);
		if (newGameEgg == null) {
			newGameEgg = new GameEggDefault(configEgg);
		}
		return newGameEgg;
	}

	public GameWorld findGameWorld(World world) {
		for (GameWorld myGameWorld : gameWorlds) {
			if (myGameWorld.getWorld().equals(world)) {
				return myGameWorld;
			}
		}
		return null;
	}

	public boolean hasActiveGames() {
		return !gameWorlds.isEmpty();
	}

	public boolean isGameActive(World world) {
		return (findGameWorld(world) != null);
	}

	public List<World> getWorldsWithGame() {
		List<World> worldsWithGame = new ArrayList<>();
		for (GameWorld myGameWorld : gameWorlds) {
			worldsWithGame.add(myGameWorld.getWorld());
		}
		return worldsWithGame;
	}

	public List<GameEgg> getGameEggs(World world) {
		GameWorld gameWorldWithEggs = findGameWorld(world);
		if (gameWorldWithEggs != null) {
			return gameWorldWithEggs.getGameEggs();
		} else {
			return new ArrayList<>();
		}
	}

	public void startGame(World world) {
		GameWorld newGameWorld = new GameWorld(world, configManager, economyManager, scoreboardManager, this);
		gameWorlds.add(newGameWorld);
		gameListener.updateListener();
	}

	public void stopGame(GameWorld oldGameWorld) {
		oldGameWorld.cancel();
		gameWorlds.remove(oldGameWorld);
		eventManager.sendGameStopEvent(oldGameWorld.getWorld(), oldGameWorld.getPlayerScores());
		gameListener.updateListener();
	}

	public void stopGame(World world) {
		GameWorld oldGameWorld = findGameWorld(world);
		if (oldGameWorld != null) {
			stopGame(oldGameWorld);
		}
	}

	public void stopAllGames() {
		for (GameWorld myGameWorld : new ArrayList<>(gameWorlds)) {
			stopGame(myGameWorld);
		}
	}

	public boolean playerClickToCollect(Player player, Location clickLocation, BlockFace blockFace)
			throws OstereierException {
		GameWorld gameWorld = findGameWorld(clickLocation.getWorld());
		if (gameWorld != null) {
			return gameWorld.playerClickToCollect(player, clickLocation, blockFace);
		} else {
			return false;
		}
	}

	public int playerMoveToCollect(Player player, Location toLocation) throws OstereierException {
		GameWorld gameWorld = findGameWorld(toLocation.getWorld());
		if (gameWorld != null) {
			return gameWorld.playerMoveToCollect(player, toLocation);
		} else {
			return 0;
		}
	}

	public boolean playerPickupItemToCollect(Player player, Item item) throws OstereierException {
		GameWorld gameWorld = findGameWorld(player.getWorld());
		if (gameWorld != null) {
			return gameWorld.playerPickupItemToCollect(player, item);
		} else {
			return false;
		}
	}

	public boolean isProtectedArea(Location location) {
		GameWorld gameWorld = findGameWorld(location.getWorld());
		if (gameWorld != null) {
			return gameWorld.isProtectedArea(location);
		} else {
			return false;
		}
	}

	public List<PlayerScore> getPlayerScores(World world) {
		GameWorld gameWorld = findGameWorld(world);
		if (gameWorld != null) {
			return gameWorld.getPlayerScores();
		} else {
			return new ArrayList<>();
		}
	}

	public void handleScheduler() throws OstereierException {
		for (GameWorld myGameWorld : gameWorlds) {
			myGameWorld.doScheduler();
		}
	}

	public void disable() {
		gameListener = null;
	}

}
