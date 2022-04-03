package de.quadrathelden.ostereier.config.spawnpoints;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigDesign;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigSpawnpointCollection {

	public static final String SPAWNPOINT_FILENAME = "spawnpoints.yml";

	protected final Plugin plugin;
	protected List<ConfigSpawnpoint> spawnpoints = new ArrayList<>();

	protected ConfigSpawnpointCollection() {
		this.plugin = null;
	}

	public ConfigSpawnpointCollection(Plugin plugin) {
		this.plugin = plugin;
	}

	public ConfigSpawnpointCollection(Plugin plugin, ConfigDesign design) throws OstereierException {
		this.plugin = plugin;
		loadAllSpawnpoints(design);
	}

	protected void loadAllSpawnpoints(ConfigDesign design) throws OstereierException {
		File spawnpointFile = new File(plugin.getDataFolder(), SPAWNPOINT_FILENAME);
		try {
			if (!spawnpointFile.exists() && !spawnpointFile.createNewFile()) {
				throw new OstereierException(null, Message.CONFIG_SPAWNPOINT_FILE_CREATE_ERROR, SPAWNPOINT_FILENAME);
			}
		} catch (Exception e) {
			throw new OstereierException(null, Message.CONFIG_SPAWNPOINT_FILE_CREATE_ERROR, e.getMessage(), e);
		}
		FileConfiguration spawnpointFileConfiguration = YamlConfiguration.loadConfiguration(spawnpointFile);

		for (String myKey : spawnpointFileConfiguration.getKeys(false)) {
			if (spawnpointFileConfiguration.isConfigurationSection(myKey)) {
				ConfigSpawnpoint mySpawnpoint = new ConfigSpawnpoint(
						spawnpointFileConfiguration.getConfigurationSection(myKey), design);
				spawnpoints.add(mySpawnpoint);
			}
		}
	}

	public ConfigSpawnpoint findSpawnpoint(World world, Coordinate coordinate) {
		for (ConfigSpawnpoint mySpawnpoint : spawnpoints) {
			if (mySpawnpoint.isSameLocation(world, coordinate)) {
				return mySpawnpoint;
			}
		}
		return null;
	}

	public ConfigSpawnpoint findSpawnpoint(UUID uuid) {
		for (ConfigSpawnpoint mySpawnpoint : spawnpoints) {
			if (mySpawnpoint.getUuid().equals(uuid)) {
				return mySpawnpoint;
			}
		}
		return null;
	}

	public List<ConfigSpawnpoint> getSpawnpoints() {
		return new ArrayList<>(spawnpoints);
	}

	public List<World> getPopulatedWorlds() {
		Set<World> populatedWorlds = new HashSet<>();
		for (ConfigSpawnpoint mySpawnpoint : spawnpoints) {
			populatedWorlds.add(mySpawnpoint.getWorld());
		}
		return new ArrayList<>(populatedWorlds);
	}

	public void addSpawnpoint(ConfigSpawnpoint spawnpoint) throws OstereierException {
		if (findSpawnpoint(spawnpoint.getWorld(), spawnpoint.getCoordinate()) != null) {
			throw new OstereierException(null, Message.CONFIG_SPAWNPOINT_DUPLICATE,
					spawnpoint.getCoordinate().toString());
		}
		File spawnpointFile = new File(plugin.getDataFolder(), SPAWNPOINT_FILENAME);
		if (!spawnpointFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_SPAWNPOINT_FILE_MISSING, SPAWNPOINT_FILENAME);
		}

		try {
			FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(spawnpointFile);
			spawnpoint.writeToFile(fileConfiguration);
			fileConfiguration.save(spawnpointFile);
		} catch (IOException e) {
			throw new OstereierException(null, Message.CONFIG_SPAWNPOINT_FILE_WRITE_ERROR, e.getMessage(), e);
		}
		spawnpoints.add(spawnpoint);
	}

	public void removeSpawnpoint(ConfigSpawnpoint spawnpoint) throws OstereierException {
		File spawnpointFile = new File(plugin.getDataFolder(), SPAWNPOINT_FILENAME);
		if (!spawnpointFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_SPAWNPOINT_FILE_MISSING, SPAWNPOINT_FILENAME);
		}

		try {
			FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(spawnpointFile);
			spawnpoint.removeFromFile(fileConfiguration);
			fileConfiguration.save(spawnpointFile);
		} catch (IOException e) {
			throw new OstereierException(null, Message.CONFIG_SPAWNPOINT_FILE_WRITE_ERROR, e.getMessage(), e);
		}
		spawnpoints.remove(spawnpoint);
	}

	protected List<ConfigSpawnpoint> getSpawnpointsForNormalWorld(World world) {
		List<ConfigSpawnpoint> worldSpawnpoints = new ArrayList<>();
		for (ConfigSpawnpoint mySpawnpoint : spawnpoints) {
			if (mySpawnpoint.getWorld().equals(world)) {
				worldSpawnpoints.add(mySpawnpoint);
			}
		}
		return worldSpawnpoints;
	}

	protected List<ConfigSpawnpoint> getSpawnpointsForMultiWorld() {
		return getSpawnpoints();
	}

	public List<ConfigSpawnpoint> getSpawnpointsForWorld(World world) {
		if (ConfigManager.isMultiworld()) {
			return getSpawnpointsForMultiWorld();
		} else {
			return getSpawnpointsForNormalWorld(world);
		}
	}

}
