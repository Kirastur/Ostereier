package de.quadrathelden.ostereier.config.design;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigTemplateCollection {

	public static final String TEMPLATE_FILENAME = "templates.yml";

	protected List<ConfigTemplate> templates = new ArrayList<>();

	protected ConfigTemplateCollection() {
	}

	public ConfigTemplateCollection(List<ConfigTemplate> templates) {
		this.templates.addAll(templates);
	}

	public ConfigTemplateCollection(Plugin plugin, ConfigEggCollection eggCollection) throws OstereierException {
		saveDefaultTemplateConfig(plugin);
		loadAllTemplates(plugin, eggCollection);
		validate();
	}

	protected void saveDefaultTemplateConfig(Plugin plugin) {
		if (!new File(plugin.getDataFolder(), TEMPLATE_FILENAME).exists()) {
			plugin.saveResource(TEMPLATE_FILENAME, false);
		}
	}

	protected void loadAllTemplates(Plugin plugin, ConfigEggCollection eggCollection) throws OstereierException {
		File templateFile = new File(plugin.getDataFolder(), TEMPLATE_FILENAME);
		if (!templateFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_TEMPLATE_FILE_MISSING, TEMPLATE_FILENAME);
		}
		FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(templateFile);

		for (String myKey : fileConfiguration.getKeys(false)) {
			if (fileConfiguration.isConfigurationSection(myKey)) {
				ConfigTemplate myTemplate = new ConfigTemplate(fileConfiguration.getConfigurationSection(myKey),
						eggCollection);
				templates.add(myTemplate);
			}
		}
	}

	public ConfigTemplate findTemplate(String templateName) {
		for (ConfigTemplate myTemplate : templates) {
			if (myTemplate.getName().equals(templateName)) {
				return myTemplate;
			}
		}
		return null;
	}

	public List<ConfigTemplate> getTemplates() {
		return new ArrayList<>(templates);
	}

	public void validate() throws OstereierException {
		for (ConfigTemplate myTemplate : templates) {
			myTemplate.validate();
		}
	}

}
