package de.quadrathelden.ostereier.config.design;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.configuration.ConfigurationSection;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigTemplate {

	public static final String EGG_NAME = "egg";
	public static final String PROBABILITY_NAME = "probability";

	protected final String name;
	protected List<ConfigTemplateEntry> entries = new ArrayList<>();

	protected ConfigTemplate(String name) {
		this.name = name;
	}

	public ConfigTemplate(String name, List<ConfigTemplateEntry> entries) {
		this.name = name;
		this.entries.addAll(entries);
	}

	public ConfigTemplate(ConfigurationSection configurationSection, ConfigEggCollection eggCollection)
			throws OstereierException {
		this.name = configurationSection.getName();
		for (String myKey : configurationSection.getKeys(false)) {
			if (configurationSection.isConfigurationSection(myKey)) {
				ConfigurationSection myEntrySection = configurationSection.getConfigurationSection(myKey);
				String myEggName = myEntrySection.getString(EGG_NAME);
				ConfigEgg myEgg = eggCollection.findEgg(myEggName);
				if (myEgg == null) {
					throw new OstereierException(name, Message.CONFIG_TEMPLATE_EGG_NOT_FOUND, myEggName);
				}
				int myProbability = myEntrySection.getInt(PROBABILITY_NAME);
				if (myProbability < 1) {
					throw new OstereierException(name, Message.CONFIG_TEMPLATE_PROBABILITY_RANGE, myEggName);
				}
				ConfigTemplateEntry myEntry = new ConfigTemplateEntry(myEgg, myProbability);
				entries.add(myEntry);
			}
		}
	}

	public String getName() {
		return name;
	}

	public List<ConfigTemplateEntry> getEntries() {
		return entries;
	}

	public void validate() throws OstereierException {
		if (entries.isEmpty()) {
			throw new OstereierException(getName(), Message.CONFIG_TEMPLATE_NO_EGGS, null);
		}
	}

	protected int getProbabilitySum() {
		int probabilitySum = 0;
		for (ConfigTemplateEntry myEntry : entries) {
			probabilitySum = probabilitySum + myEntry.probability();
		}
		return probabilitySum;
	}

	public ConfigEgg selectRandomEgg() {
		int probabilitySum = getProbabilitySum();
		int probabilityCurrent = ThreadLocalRandom.current().nextInt(probabilitySum);
		for (ConfigTemplateEntry myEntry : entries) {
			probabilityCurrent = probabilityCurrent - myEntry.probability();
			if (probabilityCurrent < 0) {
				return myEntry.configEgg();
			}
		}
		return null; // will never reached
	}

}
