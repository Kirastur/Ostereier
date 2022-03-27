package de.quadrathelden.ostereier.integrations.placeholderapi;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.game.world.GameWorld;
import de.quadrathelden.ostereier.game.world.PlayerScore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderHook extends PlaceholderExpansion {

	public static final String HOOK_AUTHOR = "Kirastur";
	public static final String HOOK_PREFIX = "ostereier";
	public static final String TOPIC_REALTIME = "realtime";
	public static final String ATTRIBUTE_EGGS = "eggs";

	protected final Plugin plugin;
	protected final GameManager gameManager;

	public PlaceholderHook(Plugin plugin, GameManager gameManager) {
		this.plugin = plugin;
		this.gameManager = gameManager;
	}

	@Override
	public @NotNull String getAuthor() {
		return HOOK_AUTHOR;
	}

	@Override
	public @NotNull String getIdentifier() {
		return HOOK_PREFIX;
	}

	@Override
	public @NotNull String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public boolean persist() {
		return true; // This is required or else PAPI will unregister the Expansion on reload
	}

	@Override
	public String onRequest(OfflinePlayer player, String param) {
		String[] words = param.split("_", 3);
		if (words.length != 3) {
			return null;
		}
		String topic = words[0];
		String attribute = words[1];
		String worldName = words[2];
		if (!topic.equalsIgnoreCase(TOPIC_REALTIME) || !attribute.equalsIgnoreCase(ATTRIBUTE_EGGS)) {
			return null;
		}
		World world = plugin.getServer().getWorld(worldName);
		GameWorld gameWorld = gameManager.findGameWorld(world);
		if (gameWorld == null) {
			return "0";
		}
		PlayerScore playerScore = gameWorld.findPlayerScore(player);
		if (playerScore == null) {
			return "0";
		}
		return Integer.toString(playerScore.getEggsCollected());
	}

}
