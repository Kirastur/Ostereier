package de.quadrathelden.ostereier.config.spawnpoints;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import de.quadrathelden.ostereier.config.design.ConfigDesign;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigSpawnpoint {

	public static final String WORLD_NAME = "world";
	public static final String X_NAME = "x";
	public static final String Y_NAME = "y";
	public static final String Z_NAME = "z";
	public static final String TEMPLATE_NAME = "template";

	protected final UUID uuid;
	protected World world;
	protected Coordinate coordinate;
	protected ConfigTemplate template;

	protected ConfigSpawnpoint(UUID uuid) {
		if (uuid == null) {
			uuid = UUID.randomUUID();
		}
		this.uuid = uuid;
	}

	public ConfigSpawnpoint(World world, Coordinate coordinate, ConfigTemplate template, UUID uuid) {
		this.world = world;
		this.coordinate = coordinate;
		this.template = template;
		if (uuid == null) {
			uuid = UUID.randomUUID();
		}
		this.uuid = uuid;
	}

	public ConfigSpawnpoint(ConfigurationSection configurationSection, ConfigDesign design) throws OstereierException {
		String uuidName = configurationSection.getName();
		uuid = UUID.fromString(uuidName);

		String worldName = configurationSection.getString(WORLD_NAME);
		world = Bukkit.getWorld(worldName);
		if (world == null) {
			throw new OstereierException(uuidName, Message.CONFIG_SPAWNPOINT_WORLD_UNKNOWN, worldName);
		}

		int x = configurationSection.getInt(X_NAME);
		int y = configurationSection.getInt(Y_NAME);
		int z = configurationSection.getInt(Z_NAME);
		coordinate = new Coordinate(x, y, z);

		String templateName = configurationSection.getString(TEMPLATE_NAME);
		template = design.findTemplate(templateName);
		if (template == null) {
			throw new OstereierException(uuidName, Message.CONFIG_SPAWNPOINT_TEMPLATE_MISSING, templateName);
		}
	}

	public UUID getUuid() {
		return uuid;
	}

	public World getWorld() {
		return world;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public ConfigTemplate getTemplate() {
		return template;
	}

	public boolean isSameLocation(World testWorld, Coordinate testCoordinate) {
		return (world.equals(testWorld) && coordinate.equals(testCoordinate));
	}

	public boolean isSameLocation(ConfigSpawnpoint testSpawnpoint) {
		return isSameLocation(testSpawnpoint.getWorld(), testSpawnpoint.getCoordinate());
	}

	public ConfigEgg getEditorEgg() {
		return template.getEntries().get(0).configEgg();
	}

	protected void writeToFile(ConfigurationSection parentConfigurationSection) {
		String uuidName = uuid.toString();
		ConfigurationSection thisConfigurationSection;
		if (parentConfigurationSection.isConfigurationSection(uuidName)) {
			thisConfigurationSection = parentConfigurationSection.getConfigurationSection(uuidName);
		} else {
			thisConfigurationSection = parentConfigurationSection.createSection(uuidName);
		}
		thisConfigurationSection.set(WORLD_NAME, world.getName());
		thisConfigurationSection.set(X_NAME, coordinate.x());
		thisConfigurationSection.set(Y_NAME, coordinate.y());
		thisConfigurationSection.set(Z_NAME, coordinate.z());
		thisConfigurationSection.set(TEMPLATE_NAME, template.getName());
	}

	protected void removeFromFile(ConfigurationSection parentConfigurationSection) {
		String uuidName = uuid.toString();
		if (parentConfigurationSection.isConfigurationSection(uuidName)) {
			parentConfigurationSection.set(uuidName, null);
		}
	}

}
