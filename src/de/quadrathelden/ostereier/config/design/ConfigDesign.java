package de.quadrathelden.ostereier.config.design;

import java.util.List;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigDesign {

	protected ConfigEggCollection eggCollection = new ConfigEggCollection();
	protected ConfigTemplateCollection templateCollection = new ConfigTemplateCollection();
	protected ConfigSpawnpointCollection spawnpointCollection = new ConfigSpawnpointCollection();

	public ConfigDesign() {
	}

	public ConfigDesign(Plugin plugin, String defaultCurrency) throws OstereierException {
		eggCollection = new ConfigEggCollection(plugin, defaultCurrency);
		templateCollection = new ConfigTemplateCollection(plugin, eggCollection);
		spawnpointCollection = new ConfigSpawnpointCollection(plugin, templateCollection);
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

	public ConfigSpawnpoint findSpawnpoint(World world, Coordinate coordinate) {
		return spawnpointCollection.findSpawnpoint(world, coordinate);
	}

	public List<ConfigSpawnpoint> getSpawnpoints() {
		return spawnpointCollection.getSpawnpoints();
	}

	public List<ConfigSpawnpoint> getSpawnpointsForWorld(World world) {
		return spawnpointCollection.getSpawnpointsForWorld(world);
	}

	public List<World> getPopulatedWorlds() {
		return spawnpointCollection.getPopulatedWorld();
	}

	public void addSpawnpoint(ConfigSpawnpoint spawnpoint) throws OstereierException {
		if (findSpawnpoint(spawnpoint.getWorld(), spawnpoint.getCoordinate()) != null) {
			throw new OstereierException(null, Message.CONFIG_SPAWNPOINT_DUPLICATE,
					spawnpoint.getCoordinate().toString());
		}
		spawnpointCollection.addSpawnpoint(spawnpoint);
	}

	public void removeSpawnpoint(ConfigSpawnpoint spawnpoint) throws OstereierException {
		spawnpointCollection.removeSpawnpoint(spawnpoint);
	}

}
