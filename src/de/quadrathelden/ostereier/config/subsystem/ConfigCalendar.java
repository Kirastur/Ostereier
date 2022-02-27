package de.quadrathelden.ostereier.config.subsystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigCalendar {

	// To avoid an unintended start of the game if the config.yml was not loaded
	// properly,
	// the calendar is empty by default.
	// You must set an calendar entry in config.yml to use this feature.

	public static final String ENABLE_CALENDAR_NAME = "enableCalendar";
	public static final String EVENT_START_NAME = "eventStart";
	public static final String EVENT_STOP_NAME = "eventEnd";
	public static final String WORLDS_NAME = "worlds";

	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

	protected boolean enableCalendar = false;
	protected LocalDateTime eventStart = null;
	protected LocalDateTime eventStop = null;
	protected List<String> worldNames = new ArrayList<>();

	public ConfigCalendar() {
	}

	public ConfigCalendar(boolean enableCalendar, LocalDateTime eventStart, LocalDateTime eventStop,
			List<String> worldNames) {
		this.enableCalendar = enableCalendar;
		this.eventStart = eventStart;
		this.eventStop = eventStop;
		this.worldNames.addAll(worldNames);
	}

	public ConfigCalendar(ConfigurationSection configurationSection) throws OstereierException {
		this.enableCalendar = configurationSection.getBoolean(ENABLE_CALENDAR_NAME, enableCalendar);
		if (!this.enableCalendar) {
			return;
		}
		this.worldNames.addAll(configurationSection.getStringList(WORLDS_NAME));
		String eventStartInput = configurationSection.getString(EVENT_START_NAME);
		String eventStopInput = configurationSection.getString(EVENT_STOP_NAME);
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
			eventStart = LocalDateTime.from(dateTimeFormatter.parse(eventStartInput));
			eventStop = LocalDateTime.from(dateTimeFormatter.parse(eventStopInput));
		} catch (Exception e) {
			throw new OstereierException(Message.CONFIG_CALENDAR_DATEFORMAT_WRONG);
		}
	}

	public boolean isEnableCalendar() {
		return enableCalendar;
	}

	public LocalDateTime getEventStart() {
		return eventStart;
	}

	public LocalDateTime getEventStop() {
		return eventStop;
	}

	public List<String> getWorldNames() {
		return worldNames;
	}

	public String getFormattedEventStart() {
		if (eventStart == null) {
			return "";
		} else {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
			return dateTimeFormatter.format(eventStart);
		}
	}

	public String getFormattedEventStop() {
		if (eventStop == null) {
			return "";
		} else {
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
			return dateTimeFormatter.format(eventStop);
		}
	}

	public boolean hasWorld(World world) {
		String testWorldName = world.getName();
		for (String myWorldName : worldNames) {
			if (myWorldName.equals(testWorldName)) {
				return true;
			}
		}
		return false;
	}

}
