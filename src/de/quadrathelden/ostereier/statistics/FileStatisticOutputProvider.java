package de.quadrathelden.ostereier.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.subsystems.ConfigStatistic;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class FileStatisticOutputProvider implements StatisticOutputProvider {

	public static final String FMT_STRING = "\"%s\"";
	public static final String FMT_INT = "%d";
	public static final String FILE_DIRECTORY = "statistic";
	public static final String FILE_NAME = "data-%s.csv";
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	protected Plugin plugin;
	protected ConfigStatistic configStatistic;

	public FileStatisticOutputProvider(Plugin plugin, ConfigStatistic configStatistic) {
		this.plugin = plugin;
		this.configStatistic = configStatistic;
	}

	protected String buildLine(AggregatedEntry entry) {
		List<String> elements = new ArrayList<>();
		elements.add(configStatistic.getPatternDateTime().format(entry.getDimensions().intervalStart()));
		elements.add(String.format(FMT_STRING, entry.getDimensions().world().getName()));
		elements.add(String.format(FMT_STRING, entry.getDimensions().egg().getName()));
		elements.add(String.format(FMT_STRING, entry.getDimensions().currency()));
		elements.add(String.format(FMT_INT, entry.getSegmentSize()));
		elements.add(String.format(FMT_INT, entry.getEggsCollected()));
		elements.add(String.format(FMT_INT, entry.getRewardsGiven()));
		elements.add(String.format(FMT_INT, entry.getActivePlayers()));
		return String.join(";", elements);
	}

	@Override
	public void writeAggregatedData(LocalDateTime intervalStart, Collection<AggregatedEntry> aggregateds)
			throws OstereierException {
		List<String> lines = new ArrayList<>();
		for (AggregatedEntry myEntry : aggregateds) {
			lines.add(buildLine(myEntry));
		}

		try {
			File directory = new File(plugin.getDataFolder(), FILE_DIRECTORY);
			if (!directory.exists()) {
				directory.mkdir();
			}
			String dateString = DateTimeFormatter.ofPattern(DATE_FORMAT).format(intervalStart);
			String fileName = String.format(FILE_NAME, dateString);
			File dataFile = new File(directory, fileName);
			try (BufferedWriter bufferedWriter = new BufferedWriter(
					new FileWriter(dataFile.getCanonicalPath(), dataFile.exists()));) {
				for (String myLine : lines) {
					bufferedWriter.write(myLine);
					bufferedWriter.newLine();
				}
			}
		} catch (Exception e) {
			throw new OstereierException(FILE_DIRECTORY, Message.JAVA_EXCEPTION, e.getMessage(), e);
		}
	}

	@Override
	public void close() {
		// Nothing to do
	}

}
