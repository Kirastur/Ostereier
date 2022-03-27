package de.quadrathelden.ostereier.main;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.bstats.Metrics;
import de.quadrathelden.ostereier.commands.AdminCommand;
import de.quadrathelden.ostereier.commands.OstereierCommand;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;

public final class Main extends JavaPlugin {

	public static final String COMMAND_NAME = "ostereier";
	public static final String ADMIN_NAME = "osteradmin";

	protected OstereierOrchestrator orchestrator = null;
	protected OstereierCommand ostereierCommand = null;
	protected AdminCommand adminCommand = null;

	@Override
	public void onEnable() {

		// Prepare Configuration
		saveDefaultConfig();

		// Register Command and TabCompleter
		ostereierCommand = new OstereierCommand(this, COMMAND_NAME);
		adminCommand = new AdminCommand(this, ADMIN_NAME);

		// Enable bStats Metrics
		// Please download the bstats-code direct form their homepage
		// or disable the following instruction
		new Metrics(this, Metrics.PLUGINID_OSTEREIER);

		// Check for passiveMode
		if (ConfigManager.isPassiveMode(this)) {
			getLogger().info("Ostereier is in passive mode. You must register your own orchestrator.");
			return;
		}

		// Initialize the Orchestrator
		try {
			orchestrator = new OstereierOrchestrator(this);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		Set<String> activeIntegrations = orchestrator.getIntegrationManager().getActiveIntegrations();
		if (!activeIntegrations.isEmpty()) {
			String s = String.format("Hooked into %s!", String.join(", ", activeIntegrations));
			getLogger().info(s);
		}

		// Load config
		try {
			orchestrator.getModeManager().reload(null);
		} catch (OstereierException e) {
			getLogger().warning("Error loading configuration");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR " + e.getMessage());
			return;
		}

		// Final welcome message
		getLogger().info("We're done. Easter can come.");
	}

	@Override
	public void onDisable() {
		if (orchestrator != null) {
			orchestrator.disable();
			orchestrator = null;
		}
	}

}
