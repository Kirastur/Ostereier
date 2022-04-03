package de.quadrathelden.ostereier.config.design;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigHeadCollection {

	public static final String HEAD_FILENAME = "heads.yml";

	protected List<ConfigHead> heads = new ArrayList<>();

	protected ConfigHeadCollection() {
	}

	public ConfigHeadCollection(List<ConfigHead> heads) {
		this.heads.addAll(heads);
	}

	public ConfigHeadCollection(Plugin plugin) throws OstereierException {
		saveDefaultHeadConfig(plugin);
		loadAllHeads(plugin);
	}

	protected void saveDefaultHeadConfig(Plugin plugin) {
		if (!new File(plugin.getDataFolder(), HEAD_FILENAME).exists()) {
			plugin.saveResource(HEAD_FILENAME, false);
		}
	}

	protected void loadAllHeads(Plugin plugin) throws OstereierException {
		File headFile = new File(plugin.getDataFolder(), HEAD_FILENAME);
		if (!headFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_HEAD_FILE_MISSING, HEAD_FILENAME);
		}
		FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(headFile);

		for (String myKey : fileConfiguration.getKeys(false)) {
			if (fileConfiguration.isString(myKey)) {
				String myData = fileConfiguration.getString(myKey);
				ConfigHead myHead = new ConfigHead(myKey, myData);
				heads.add(myHead);
			}
		}
	}

	public ConfigHead findHead(String headName) {
		for (ConfigHead myHead : heads) {
			if (myHead.getName().equals(headName)) {
				return myHead;
			}
		}
		return null;
	}

	public List<ConfigHead> getHeads() {
		return new ArrayList<>(heads);
	}
}
