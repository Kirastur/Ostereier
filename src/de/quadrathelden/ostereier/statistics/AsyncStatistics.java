package de.quadrathelden.ostereier.statistics;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.tools.Message;

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
		} catch (OstereierException oe) {
			statisticManager.lastAsyncException = oe;
		} catch (Exception e) {
			statisticManager.lastAsyncException = new OstereierException(null, Message.JAVA_EXCEPTION, e.getMessage(),
					e);
		}
	}

}
