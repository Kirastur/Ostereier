package de.quadrathelden.ostereier.integrations.heliumballoon;

import java.io.File;
import java.util.HashSet;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.polarwolf.heliumballoon.config.ConfigSection;
import de.polarwolf.heliumballoon.events.BalloonRebuildConfigEvent;
import de.polarwolf.heliumballoon.events.BalloonRefreshAllEvent;
import de.polarwolf.heliumballoon.exception.BalloonException;
import de.quadrathelden.ostereier.displays.DisplayEggBalloon;

public class HeliumBalloonListener implements Listener {

	protected final Plugin plugin;
	protected final HeliumBalloonHook heliumBalloonHook;

	public HeliumBalloonListener(Plugin plugin, HeliumBalloonHook heliumBalloonHook) {
		this.plugin = plugin;
		this.heliumBalloonHook = heliumBalloonHook;
		enableListener();
	}

	public void enableListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void disableListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBalloonRebuildConfigEvent(BalloonRebuildConfigEvent event) {
		try {
			File eggFile = new File(plugin.getDataFolder(), HeliumBalloonHook.BALLOON_FILENAME);
			if (!eggFile.exists()) {
				return;
			}
			FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(eggFile);
			ConfigSection newSection = event.buildConfigSectionFromFileSection(HeliumBalloonHook.SECTION_NAME,
					fileConfiguration.getRoot());
			event.addSection(newSection);
		} catch (BalloonException be) {
			be.printStackTrace();
			event.cancelWithReason(be);
		} catch (Exception e) {
			e.printStackTrace();
			event.cancelWithReason(new BalloonException(null, BalloonException.JAVA_EXCEPTION, null, e));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBalloonRefreshEvent(BalloonRefreshAllEvent event) {
		try {
			for (DisplayEggBalloon myBalloon : new HashSet<>(heliumBalloonHook.walls.keySet())) {
				heliumBalloonHook.removeWall(myBalloon);
				heliumBalloonHook.addWall(myBalloon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
