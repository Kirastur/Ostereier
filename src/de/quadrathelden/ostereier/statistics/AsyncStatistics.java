package de.quadrathelden.ostereier.statistics;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncStatistics extends BukkitRunnable {

	protected final StatisticManager statisticManager;
	protected final World world;

	public AsyncStatistics(Plugin plugin, StatisticManager statisticManager, World world) {
		this.statisticManager = statisticManager;
		this.world = world;
		runTaskAsynchronously(plugin);
	}

	@Override
	public void run() {
		try {
			statisticManager.doAggregation(world, true);
		} catch (Exception e) {
			statisticManager.lastAsyncException = e;
		}
	}

}
