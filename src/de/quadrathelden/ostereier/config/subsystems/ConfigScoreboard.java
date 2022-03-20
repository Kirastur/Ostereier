package de.quadrathelden.ostereier.config.subsystems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import de.quadrathelden.ostereier.text.OsterText;
import de.quadrathelden.ostereier.text.TextManager;

public class ConfigScoreboard {

	public static final String MESSAGE_SCOREBOARD_TITLE = "scoreboardTitle";
	public static final String MESSAGE_CURRENCY_PREFIX = "currency";

	public static final String PARAM_ENABLE_SCOREBOARD = "enableScoreboard";
	public static final String PARAM_IS_PERMANENT = "isPermanant";
	public static final String PARAM_ELEMENTS = "elements";
	public static final String PARAM_DISPLAY_TIMEOUT = "displayTimeout";

	public static final String DEFAULT_SCOREBOARD_TITLE = "Eastereggs";

	public static final boolean DEFAULT_ENABLE_SCOREBOARD = true;
	public static final boolean DEFAULT_IS_PERMANENT = false;
	public static final int DEFAULT_DISPLAY_TIMEOUT = 10;

	protected boolean enableScoreboard = DEFAULT_ENABLE_SCOREBOARD;
	protected boolean permanent = DEFAULT_IS_PERMANENT;
	protected int displayTimeout = DEFAULT_DISPLAY_TIMEOUT;
	protected List<String> elements = new ArrayList<>();
	protected OsterText scoreboardTitelText = new OsterText(MESSAGE_SCOREBOARD_TITLE, DEFAULT_SCOREBOARD_TITLE);

	public ConfigScoreboard() {
	}

	public ConfigScoreboard(boolean enableScoreboard, boolean permanent, OsterText scoreboardTitelText,
			List<String> elements, int displayTimeout) {
		this.enableScoreboard = enableScoreboard;
		this.permanent = permanent;
		this.scoreboardTitelText = scoreboardTitelText;
		this.elements.addAll(elements);
		this.displayTimeout = displayTimeout;
	}

	public ConfigScoreboard(ConfigurationSection configurationSection, TextManager textManager) {
		enableScoreboard = configurationSection.getBoolean(PARAM_ENABLE_SCOREBOARD, DEFAULT_ENABLE_SCOREBOARD);
		permanent = configurationSection.getBoolean(PARAM_IS_PERMANENT, DEFAULT_IS_PERMANENT);
		displayTimeout = configurationSection.getInt(PARAM_DISPLAY_TIMEOUT, DEFAULT_DISPLAY_TIMEOUT);

		OsterText newScoreboardTitelText = textManager.findOsterText(MESSAGE_SCOREBOARD_TITLE);
		if (newScoreboardTitelText != null) {
			scoreboardTitelText = newScoreboardTitelText;
		}

		elements.addAll(configurationSection.getStringList(PARAM_ELEMENTS));
	}

	public boolean isEnableScoreboard() {
		return enableScoreboard;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public String getScoreboardTitelText(Player player) {
		return scoreboardTitelText.getText(player);
	}

	public List<String> getElements() {
		return new ArrayList<>(elements);
	}

	public int getDisplayTimeout() {
		return displayTimeout;
	}

}
