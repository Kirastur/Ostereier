package de.quadrathelden.ostereier.integrations.heliumballoon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import de.polarwolf.heliumballoon.api.HeliumBalloonAPI;
import de.polarwolf.heliumballoon.api.HeliumBalloonProvider;
import de.polarwolf.heliumballoon.balloons.walls.Wall;
import de.polarwolf.heliumballoon.config.ConfigTemplate;
import de.polarwolf.heliumballoon.config.ConfigWall;
import de.polarwolf.heliumballoon.exception.BalloonException;
import de.polarwolf.heliumballoon.observers.Observer;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.displays.DisplayEggBalloon;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

public class HeliumBalloonHook implements Listener, HeliumBalloonInterface {

	public static final String BALLOON_FILENAME = "balloons.yml";
	public static final String SECTION_NAME = "Ostereier";

	protected final Plugin plugin;

	protected HeliumBalloonListener heliumBalloonListener;
	protected Map<DisplayEggBalloon, Wall> walls = new HashMap<>();

	public HeliumBalloonHook(Plugin plugin) {
		this.plugin = plugin;
		if (!new File(plugin.getDataFolder(), BALLOON_FILENAME).exists()) {
			plugin.saveResource(BALLOON_FILENAME, false);
		}
		heliumBalloonListener = new HeliumBalloonListener(plugin, this);
	}

	@Override
	public void addWall(DisplayEggBalloon displayEggBalloon) throws OstereierException {
		HeliumBalloonAPI api = HeliumBalloonProvider.getAPI();
		ConfigEgg configEgg = displayEggBalloon.getConfigEgg();
		String eggName = configEgg.getName();
		Location location = displayEggBalloon.getCoordinate().toLocation(displayEggBalloon.getWorld())
				.add(new Vector(0.5, 0, 0.5));
		ConfigTemplate template = api.findConfigTemplateInSection(HeliumBalloonHook.SECTION_NAME,
				configEgg.getBalloon());
		if (template == null) {
			throw new OstereierException(eggName, Message.BALLOON_TEMPLATE_NOT_FOUND, configEgg.getBalloon());
		}
		try {
			ConfigWall configWall = new ConfigWall(eggName, SECTION_NAME + "." + eggName, template, location);
			Wall activeWall = api.createWall(configWall, location.getWorld());
			walls.put(displayEggBalloon, activeWall);
		} catch (BalloonException be) {
			throw new OstereierException(eggName, Message.JAVA_EXCEPTION, be.getMessage(), be);
		}
	}

	@Override
	public void removeWall(DisplayEggBalloon displayEggBalloon) {
		HeliumBalloonAPI api = HeliumBalloonProvider.getAPI();
		Wall activeWall = walls.get(displayEggBalloon);
		if (activeWall != null) {
			walls.remove(displayEggBalloon);
			api.destroyWall(activeWall);
		}
	}

	protected DisplayEggBalloon getDisplayEggBalloonFromWall(Wall wall) {
		for (Entry<DisplayEggBalloon, Wall> myEntry : walls.entrySet()) {
			if (myEntry.getValue().equals(wall)) {
				return myEntry.getKey();
			}
		}
		return null;
	}

	@Override
	public DisplayEggBalloon findWall(Entity entity) {
		HeliumBalloonAPI api = HeliumBalloonProvider.getAPI();
		Observer observer = api.findObserverByEntity(entity);
		if (observer == null) {
			return null;
		}
		for (Wall myWall : api.getAllWalls()) {
			for (Observer myObserver : myWall.getObservers()) {
				if (myObserver.equals(observer)) {
					return getDisplayEggBalloonFromWall(myWall);
				}
			}
		}
		return null;
	}

	public void disable() {
		if (heliumBalloonListener != null) {
			heliumBalloonListener.disableListener();
			heliumBalloonListener = null;
		}
	}

}
