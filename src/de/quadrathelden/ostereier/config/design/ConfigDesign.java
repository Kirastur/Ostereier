package de.quadrathelden.ostereier.config.design;

import java.util.List;

import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;

public class ConfigDesign {

	protected ConfigHeadCollection headCollection = new ConfigHeadCollection();
	protected ConfigEggCollection eggCollection = new ConfigEggCollection();
	protected ConfigTemplateCollection templateCollection = new ConfigTemplateCollection();

	public ConfigDesign() {
	}

	public ConfigDesign(Plugin plugin, String defaultCurrency) throws OstereierException {
		headCollection = new ConfigHeadCollection(plugin);
		eggCollection = new ConfigEggCollection(plugin, headCollection, defaultCurrency);
		templateCollection = new ConfigTemplateCollection(plugin, eggCollection);
	}

	public ConfigHead findHead(String headName) {
		return headCollection.findHead(headName);
	}

	public List<ConfigHead> getHeads() {
		return headCollection.getHeads();
	}

	public ConfigEgg findEgg(String eggName) {
		return eggCollection.findEgg(eggName);
	}

	public List<ConfigEgg> getEggs() {
		return eggCollection.getEggs();
	}

	public ConfigTemplate findTemplate(String templateName) {
		return templateCollection.findTemplate(templateName);
	}

	public List<ConfigTemplate> getTemplates() {
		return templateCollection.getTemplates();
	}

}
