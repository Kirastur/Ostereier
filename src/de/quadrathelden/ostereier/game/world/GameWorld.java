package de.quadrathelden.ostereier.game.world;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.quadrathelden.ostereier.bunny.Bunny;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigEggMode;
import de.quadrathelden.ostereier.config.spawnpoints.ConfigSpawnpoint;
import de.quadrathelden.ostereier.displays.DisplayManager;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.economy.EconomyProvider;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.game.egg.GameEgg;
import de.quadrathelden.ostereier.game.egg.GameHelper;
import de.quadrathelden.ostereier.scoreboard.ScoreboardManager;
import de.quadrathelden.ostereier.statistics.CollectDetailEntry;
import de.quadrathelden.ostereier.statistics.StatisticManager;
import de.quadrathelden.ostereier.tools.Coordinate;

public class GameWorld {

	protected final ConfigManager configManager;
	protected final EconomyManager economyManager;
	protected final StatisticManager statisticManager;
	protected final ScoreboardManager scoreboardManager;
	protected final GameManager gameManager;

	protected final World world;
	protected final Bunny bunny;

	protected List<GameEgg> gameEggs = new ArrayList<>();
	protected List<PlayerScore> playerScores = new ArrayList<>();

	public GameWorld(World world, ConfigManager configManager, EconomyManager economyManager,
			StatisticManager statisticManager, ScoreboardManager scoreboardManager, GameManager gameManager) {
		this.world = world;
		this.configManager = configManager;
		this.economyManager = economyManager;
		this.statisticManager = statisticManager;
		this.scoreboardManager = scoreboardManager;
		this.gameManager = gameManager;
		this.bunny = buildBunny();
	}

	public World getWorld() {
		return world;
	}

	public Bunny getBunny() {
		return bunny;
	}

	protected Bunny buildBunny() {
		return gameManager.buildBunny(world);
	}

	protected GameHelper buildGameHelper(ConfigSpawnpoint spawnpoint) {
		return gameManager.buildGameHelper(this, spawnpoint.getCoordinate());
	}

	protected GameEgg findGameEgg(Coordinate coordinate) {
		for (GameEgg myGameEgg : gameEggs) {
			if (myGameEgg.getCoordinate().equals(coordinate)) {
				return myGameEgg;
			}
		}
		return null;
	}

	protected GameEgg findGameEgg(UUID uuid) {
		for (GameEgg myGameEgg : gameEggs) {
			UUID myUUID = myGameEgg.getUUID();
			if ((myUUID != null) && myUUID.equals(uuid)) {
				return myGameEgg;
			}
		}
		return null;
	}

	protected GameEgg findPlacedGameEgg(Coordinate coordinate) {
		GameEgg gameEgg = findGameEgg(coordinate);
		if ((gameEgg == null) || !gameEgg.isPlaced()) {
			return null;
		} else {
			return gameEgg;
		}
	}

	protected GameEgg findNearGameEgg(Location location) {
		for (GameEgg myGameEgg : gameEggs) {
			Location myLocation = myGameEgg.getCoordinate().toLocation(world);
			if (myLocation.distance(location) <= configManager.getConfigGame().getProtectedAreaAroundEggs()) {
				return myGameEgg;
			}
		}
		return null;
	}

	protected List<ConfigSpawnpoint> getFreeSpawnpoints() {
		List<ConfigSpawnpoint> freeSpawnpoints = new ArrayList<>();
		for (ConfigSpawnpoint mySpawnpoint : configManager.getSpawnpointsForWorld(world)) {
			if (findGameEgg(mySpawnpoint.getCoordinate()) == null) {
				freeSpawnpoints.add(mySpawnpoint);
			}
		}
		return freeSpawnpoints;
	}

	protected void spawnEgg(ConfigSpawnpoint spawnpoint) throws OstereierException {
		ConfigEgg configEgg = spawnpoint.getTemplate().selectRandomEgg();
		GameEgg newGameEgg = gameManager.buildGameEgg(world, configEgg, spawnpoint);
		newGameEgg.setGameHelper(buildGameHelper(spawnpoint));
		gameEggs.add(newGameEgg);
		newGameEgg.placeEgg();
	}

	protected void distributeEggs() throws OstereierException {
		int activePlayer = getActivePlayer();
		int eggsNeeded = bunny.calculateEggsNeeded(world, activePlayer);
		int eggsToSpawn = eggsNeeded - gameEggs.size();
		for (int i = 0; i < eggsToSpawn; i++) {
			List<ConfigSpawnpoint> freeSpawnpoints = getFreeSpawnpoints();
			if (freeSpawnpoints.isEmpty()) {
				return;
			}
			int r = ThreadLocalRandom.current().nextInt(freeSpawnpoints.size());
			ConfigSpawnpoint mySpawnpoint = freeSpawnpoints.get(r);
			spawnEgg(mySpawnpoint);
		}
	}

	protected void decrementEggLifetime() {
		for (GameEgg myGameEgg : gameEggs) {
			myGameEgg.decrementLifetime();
		}
	}

	protected void removeFinishedEggs() {
		for (GameEgg myGameEgg : new ArrayList<>(gameEggs)) {
			if (myGameEgg.isFinished()) {
				gameEggs.remove(myGameEgg);
			}
		}
	}

	public void doScheduler() throws OstereierException {
		decrementEggLifetime();
		removeFinishedEggs();
		distributeEggs();
	}

	public List<GameEgg> getGameEggs() {
		return new ArrayList<>(gameEggs);
	}

	public void cancel() {
		for (GameEgg myGameEgg : new ArrayList<>(gameEggs)) {
			myGameEgg.cancelEgg();
			gameEggs.remove(myGameEgg);
		}
	}

	public boolean playerClickToCollect(Player player, Location clickLocation, BlockFace blockFace)
			throws OstereierException {
		Coordinate clickCoordinate = Coordinate.of(clickLocation);
		GameEgg clickGameEgg = findPlacedGameEgg(clickCoordinate);
		if ((clickGameEgg == null) && (blockFace != null)) {
			clickGameEgg = findPlacedGameEgg(clickCoordinate.neighbor(blockFace));
		}
		if (clickGameEgg == null) {
			return false;
		}
		return clickGameEgg.pickupEgg(player);
	}

	public int playerMoveToCollect(Player player, Location toLocation) throws OstereierException {
		int count = 0;
		for (GameEgg myGameEgg : gameEggs) {
			if (myGameEgg.isPlaced()) {
				Location myGameEggLocation = myGameEgg.getCoordinate().toLocation(world);
				ConfigEggMode myConfigEggMode = myGameEgg.getConfigEgg().getMode();
				if ((myConfigEggMode == ConfigEggMode.BLOCK) || (myConfigEggMode == ConfigEggMode.BALLOON)
						|| (myConfigEggMode == ConfigEggMode.ANIMAL)) {
					myGameEggLocation = myGameEggLocation.add(new Vector(0.5, 0.0, 0.5));
				}
				double distance = toLocation.distance(myGameEggLocation);
				if (distance < 1.0) {
					if (myGameEgg.pickupEgg(player)) { // NOSONAR
						count = count + 1;
					}
				}
			}
		}
		return count;
	}

	public boolean playerPickupItemToCollect(Player player, Item item) throws OstereierException {
		UUID itemUUID = DisplayManager.readDisplaySeal(item.getItemStack());
		if (itemUUID == null) {
			return false;
		}
		GameEgg itemGameEgg = findGameEgg(itemUUID);
		if (itemGameEgg == null) {
			return false;
		}
		return itemGameEgg.pickupEgg(player);
	}

	public boolean isProtectedArea(Location location) {
		if (configManager.getConfigGame().getProtectedAreaAroundEggs() >= 0) {
			return (findNearGameEgg(location) != null);
		} else {
			return false;
		}
	}

	public PlayerScore findPlayerScore(OfflinePlayer offlinePlayer) {
		if (offlinePlayer == null) {
			return null;
		}
		UUID playerUUID = offlinePlayer.getUniqueId();
		for (PlayerScore myPlayerScore : playerScores) {
			if (myPlayerScore.getOfflinePlayer().getUniqueId().equals(playerUUID)) {
				return myPlayerScore;
			}
		}
		return null;
	}

	public void scorePlayerEggCollect(Player player, GameEgg gameEgg) throws OstereierException {

		ConfigEgg configEgg = gameEgg.getConfigEgg();
		String rewardCurrency = economyManager.refineRewardCurrencyName(configEgg.getRewardCurrency());

		// Step 1: Realtime PlayerScore
		PlayerScore playerScore = findPlayerScore(player);
		if (playerScore == null) {
			playerScore = new PlayerScore(player, economyManager);
			playerScores.add(playerScore);
		}
		playerScore.rewardPlayer(gameEgg);

		// Step 2: Persistent Economy Provider
		EconomyProvider economyProvider = economyManager.getEconomyProvider();
		economyProvider.incrementEggs(player);
		if (configEgg.getRewardAmount() > 0) {
			economyProvider.addPoints(player, configEgg.getRewardAmount(), rewardCurrency);
		}
		economyProvider.commit();

		// Step 3: Statistics
		CollectDetailEntry cde = new CollectDetailEntry(LocalDateTime.now(), getWorld(), gameEgg.getCoordinate(),
				configEgg, configEgg.getRewardAmount(), rewardCurrency, player);
		statisticManager.addCollectDetailEntry(cde);

		// Step 4: Player Scoreboard
		scoreboardManager.updateScoreboard(player);
	}

	public int getActivePlayer() {
		int playerCount = 0;
		Instant now = Instant.now();
		for (PlayerScore myPlayerScore : playerScores) {
			Duration duration = Duration.between(myPlayerScore.lastChange, now);
			if (duration.toMinutes() < 15) {
				playerCount = playerCount + 1;
			}
		}
		playerCount = Math.min(playerCount, world.getPlayers().size());
		return playerCount;
	}

	public List<PlayerScore> getPlayerScores() {
		return new ArrayList<>(playerScores);
	}
}
