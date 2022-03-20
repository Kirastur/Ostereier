package de.quadrathelden.ostereier.config.subsystems;

import java.time.format.DateTimeFormatter;

import org.bukkit.configuration.ConfigurationSection;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigStatistic {

	public static final String PARAM_ENABLE_STATISTIC = "enableStatistic";
	public static final String PARAM_INTERVAL_SIZE = "intervalSize";
	public static final String PARAM_OUTPUT_PROVIDER = "statisticOutputProvider";
	public static final String PARAM_FORMAT_DATE_TIME = "formatDateTime";

	public static final boolean DEFAULT_ENABLE_STATISTIC = false;
	public static final int DEFAULT_INTERVAL_SIZE = 5;
	public static final ConfigStatisticOutputProvider DEFAULT_OUTPUT_PROVIDER = ConfigStatisticOutputProvider.FILE;
	public static final String DEFAULT_FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";

	protected boolean enableStatistic = DEFAULT_ENABLE_STATISTIC;
	protected int intervalSize = DEFAULT_INTERVAL_SIZE;
	protected ConfigStatisticOutputProvider outputProvider = DEFAULT_OUTPUT_PROVIDER;
	protected String formatDateTime = DEFAULT_FORMAT_DATE_TIME;
	protected DateTimeFormatter patternDateTime = DateTimeFormatter.ofPattern(DEFAULT_FORMAT_DATE_TIME);

	public ConfigStatistic() {
	}

	public ConfigStatistic(boolean enableStatistic, int intervalSize, ConfigStatisticOutputProvider outputProvider,
			String formatDateTime) throws OstereierException {
		this.enableStatistic = enableStatistic;
		this.intervalSize = intervalSize;
		this.outputProvider = outputProvider;
		this.formatDateTime = formatDateTime;
		validate();
	}

	public ConfigStatistic(ConfigurationSection configurationSection) throws OstereierException {
		enableStatistic = configurationSection.getBoolean(PARAM_ENABLE_STATISTIC, DEFAULT_ENABLE_STATISTIC);
		intervalSize = configurationSection.getInt(PARAM_INTERVAL_SIZE, DEFAULT_INTERVAL_SIZE);
		formatDateTime = configurationSection.getString(PARAM_FORMAT_DATE_TIME, DEFAULT_FORMAT_DATE_TIME);

		String outputProviderName = configurationSection.getString(PARAM_OUTPUT_PROVIDER);
		if ((outputProviderName == null) || outputProviderName.isEmpty()) {
			throw new OstereierException(configurationSection.getName(),
					Message.CONFIG_STATISTIC_OUTPUT_PROVIDER_MISSING, null);
		}
		outputProviderName = outputProviderName.toUpperCase();
		try {
			outputProvider = ConfigStatisticOutputProvider.valueOf(outputProviderName);
		} catch (Exception e) {
			throw new OstereierException(configurationSection.getName(),
					Message.CONFIG_STATISTIC_OUTPUT_PROVIDER_UNKNOWN, outputProviderName);
		}
		validate();
	}

	protected void validate() throws OstereierException {
		if (outputProvider == null) {
			throw new OstereierException(Message.CONFIG_STATISTIC_OUTPUT_PROVIDER_MISSING);
		}

		try {
			patternDateTime = DateTimeFormatter.ofPattern(formatDateTime);

		} catch (Exception e) {
			throw new OstereierException(null, Message.CONFIG_STATISTIC_ILLEGAL_DATE_TIME_FORMAT, formatDateTime);
		}

		if ((60 % intervalSize) > 0) {
			throw new OstereierException(null, Message.CONFIG_STATISTIC_ILLEGAL_INTERVAL_SIZE, formatDateTime);
		}
	}

	public boolean isEnableStatistic() {
		return enableStatistic;
	}

	public int getIntervalSize() {
		return intervalSize;
	}

	public ConfigStatisticOutputProvider getOutputProvider() {
		return outputProvider;
	}

	public String getFormatDateTime() {
		return formatDateTime;
	}

	public DateTimeFormatter getPatternDateTime() {
		return patternDateTime;
	}

}