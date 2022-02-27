package de.quadrathelden.ostereier.mode;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ModeScheduler extends BukkitRunnable {

	protected ModeManager modeManager;

	public ModeScheduler(Plugin plugin, ModeManager modeManager) {
		this.modeManager = modeManager;
		runTaskTimer(plugin, 20, 20);
	}

	@Override
	public void run() {
		modeManager.handleScheduler();
	}

}