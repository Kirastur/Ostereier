package de.quadrathelden.ostereier.config.design;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigEggCollection {

	public static final String EGG_FILENAME = "eggs.yml";

	protected List<ConfigEgg> eggs = new ArrayList<>();

	protected ConfigEggCollection() {
	}

	public ConfigEggCollection(List<ConfigEgg> eggs) {
		this.eggs.addAll(eggs);
	}

	public ConfigEggCollection(Plugin plugin, String defaultCurrency) throws OstereierException {
		saveDefaultEggConfig(plugin);
		loadAllEggs(plugin, defaultCurrency);
	}

	protected void saveDefaultEggConfig(Plugin plugin) {
		if (!new File(plugin.getDataFolder(), EGG_FILENAME).exists()) {
			plugin.saveResource(EGG_FILENAME, false);
		}
	}

	protected void loadAllEggs(Plugin plugin, String defaultCurrency) throws OstereierException {
		File eggFile = new File(plugin.getDataFolder(), EGG_FILENAME);
		if (!eggFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_EGG_FILE_MISSING, EGG_FILENAME);
		}
		FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(eggFile);

		for (String myKey : fileConfiguration.getKeys(false)) {
			if (fileConfiguration.isConfigurationSection(myKey)) {
				ConfigEgg myEgg = new ConfigEgg(fileConfiguration.getConfigurationSection(myKey), defaultCurrency);
				eggs.add(myEgg);
			}
		}
	}

	public ConfigEgg findEgg(String eggName) {
		for (ConfigEgg myEgg : eggs) {
			if (myEgg.getName().equals(eggName)) {
				return myEgg;
			}
		}
		return null;
	}

	public List<ConfigEgg> getEggs() {
		return new ArrayList<>(eggs);
	}
}
