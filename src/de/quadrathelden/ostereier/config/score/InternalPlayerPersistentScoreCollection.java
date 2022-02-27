package de.quadrathelden.ostereier.config.score;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class InternalPlayerPersistentScoreCollection {

	public static final String SCORE_FILENAME = "playerscores.yml";

	protected final Plugin plugin;
	protected List<InternalPlayerPersistentScore> scores = new ArrayList<>();
	protected Instant lastSync = Instant.now();

	protected InternalPlayerPersistentScoreCollection() {
		this.plugin = null;
	}

	protected InternalPlayerPersistentScoreCollection(Plugin plugin) {
		this.plugin = plugin;
	}

	public InternalPlayerPersistentScoreCollection(Plugin plugin, boolean loadFromFile) throws OstereierException {
		this.plugin = plugin;
		if (loadFromFile) {
			loadAllScores();
		}
	}

	protected void loadAllScores() throws OstereierException {
		File scoreFile = new File(plugin.getDataFolder(), SCORE_FILENAME);
		try {
			if (!scoreFile.exists() && !scoreFile.createNewFile()) {
				throw new OstereierException(null, Message.CONFIG_IPPS_FILE_CREATE_ERROR, SCORE_FILENAME);
			}
		} catch (Exception e) {
			throw new OstereierException(null, Message.CONFIG_IPPS_FILE_CREATE_ERROR, e.getMessage(), e);
		}
		FileConfiguration scoreFileConfiguration = YamlConfiguration.loadConfiguration(scoreFile);

		for (String myKey : scoreFileConfiguration.getKeys(false)) {
			if (scoreFileConfiguration.isConfigurationSection(myKey)) {
				InternalPlayerPersistentScore myScore = new InternalPlayerPersistentScore(
						scoreFileConfiguration.getConfigurationSection(myKey));
				scores.add(myScore);
			}
		}
		lastSync = Instant.now();
	}

	public InternalPlayerPersistentScore findScore(OfflinePlayer offlinePlayer) {
		for (InternalPlayerPersistentScore myScore : scores) {
			if (myScore.getOfflinePlayer().getUniqueId().equals(offlinePlayer.getUniqueId())) {
				return myScore;
			}
		}
		return null;
	}

	public InternalPlayerPersistentScore findOrCreateScore(OfflinePlayer offlinePlayer) {
		InternalPlayerPersistentScore playerScore = findScore(offlinePlayer);
		if (playerScore == null) {
			playerScore = new InternalPlayerPersistentScore(offlinePlayer);
			scores.add(playerScore);
		}
		return playerScore;
	}

	public List<InternalPlayerPersistentScore> getScores() {
		return new ArrayList<>(scores);
	}

	public void saveScores() throws OstereierException {
		File scoreFile = new File(plugin.getDataFolder(), SCORE_FILENAME);
		if (!scoreFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_IPPS_FILE_MISSING, SCORE_FILENAME);
		}

		try {
			FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(scoreFile);
			for (InternalPlayerPersistentScore myScore : scores) {
				if (myScore.lastChange.isAfter(lastSync)) {
					myScore.writeToFile(fileConfiguration);
				}
			}
			fileConfiguration.save(scoreFile);
		} catch (IOException e) {
			throw new OstereierException(null, Message.CONFIG_IPPS_FILE_WRITE_ERROR, e.getMessage(), e);
		}
		lastSync = Instant.now();
	}

	public void removeScore(InternalPlayerPersistentScore score) throws OstereierException {
		File scoreFile = new File(plugin.getDataFolder(), SCORE_FILENAME);
		if (!scoreFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_IPPS_FILE_MISSING, SCORE_FILENAME);
		}

		try {
			FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(scoreFile);
			score.removeFromFile(fileConfiguration);
			fileConfiguration.save(scoreFile);
		} catch (IOException e) {
			throw new OstereierException(null, Message.CONFIG_IPPS_FILE_WRITE_ERROR, e.getMessage(), e);
		}
		scores.remove(score);
	}

}
