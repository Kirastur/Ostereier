package de.quadrathelden.ostereier.integrations;

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.GameManager;
import de.quadrathelden.ostereier.integrations.betonquest.BetonQuestHook;
import de.quadrathelden.ostereier.integrations.citizens.CitizensHook;
import de.quadrathelden.ostereier.integrations.heliumballoon.HeliumBalloonHook;
import de.quadrathelden.ostereier.integrations.heliumballoon.HeliumBalloonInterface;
import de.quadrathelden.ostereier.integrations.placeholderapi.PlaceholderHook;
import de.quadrathelden.ostereier.integrations.theneweconomy.TNEHook;
import de.quadrathelden.ostereier.integrations.theneweconomy.TNEIntegration;
import de.quadrathelden.ostereier.integrations.vault.VaultHook;
import de.quadrathelden.ostereier.integrations.vault.VaultIntegration;
import de.quadrathelden.ostereier.shop.ShopManager;
import de.quadrathelden.ostereier.text.TextManager;

public class IntegrationManager {

	public static final String TNE_NAME = "TheNewEconomy";
	public static final String VAULT_NAME = "Vault";
	public static final String CITIZENS_NAME = "Citizens";
	public static final String PAPI_NAME = "PlaceholderAPI";
	public static final String BETONQUEST_NAME = "BetonQuest";
	public static final String HELIUMBALLOON_NAME = "HeliumBalloon";

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigManager configManager;

	protected CitizensHook citizensHook = null;
	protected PlaceholderHook placeholderHook = null;
	protected BetonQuestHook betonQuestHook = null;
	protected HeliumBalloonHook heliumBalloonHook = null;

	public IntegrationManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.textManager = orchestrator.getTextManager();
		this.configManager = orchestrator.getConfigManager();
	}

	// Vault

	public boolean hasVault() {
		return (configManager.getConfigIntegration().hasVault()
				&& plugin.getServer().getPluginManager().getPlugin(VAULT_NAME) != null);
	}

	public VaultIntegration getVaultIntegrationHook() throws OstereierException {
		if (hasVault()) {
			return new VaultHook(plugin);
		} else {
			return null;
		}
	}

	// TheNewEconpmy(TNE)

	public boolean hasTNE() {
		return (configManager.getConfigIntegration().hasTNE()
				&& plugin.getServer().getPluginManager().getPlugin(TNE_NAME) != null);
	}

	public TNEIntegration getTNEIntegrationHook() throws OstereierException {
		if (hasTNE()) {
			return new TNEHook();
		} else {
			return null;
		}
	}

	// Citizens

	public boolean hasCitizens() {
		return (configManager.getConfigIntegration().hasCitizens()
				&& plugin.getServer().getPluginManager().getPlugin(CITIZENS_NAME) != null);
	}

	public void initializeCitizensHook(ShopManager shopManager) {
		if (hasCitizens()) {
			citizensHook = new CitizensHook(plugin, textManager, configManager, shopManager);
		}
	}

	// PlaceholderAPI (PAPI)
	public boolean hasPAPI() {
		return (configManager.getConfigIntegration().hasPAPI()
				&& plugin.getServer().getPluginManager().getPlugin(PAPI_NAME) != null);
	}

	protected void initializePAPIHook(GameManager gameManager) {
		if (hasPAPI()) {
			placeholderHook = new PlaceholderHook(plugin, gameManager);
			placeholderHook.register();
		}
	}

	// BetonQuest

	public boolean hasBetonQuest() {
		return (configManager.getConfigIntegration().hasPAPI()
				&& plugin.getServer().getPluginManager().getPlugin(BETONQUEST_NAME) != null);
	}

	protected void initializeBetonQuestHook() {
		if (hasBetonQuest()) {
			betonQuestHook = new BetonQuestHook(textManager);
			boolean hasChanged = betonQuestHook.initializeIntegration();
			if (hasChanged) {
				plugin.getLogger().info("Updating BetonQuest messages");
			}
		}
	}

	// HeliumBalloon

	public boolean hasHeliumBalloon() {
		return (configManager.getConfigIntegration().hasHeliumBalloon()
				&& plugin.getServer().getPluginManager().getPlugin(HELIUMBALLOON_NAME) != null);
	}

	protected void initializeHeliumBalloonHook(Plugin plugin) {
		if (hasHeliumBalloon()) {
			heliumBalloonHook = new HeliumBalloonHook(plugin);
		}
	}

	public HeliumBalloonInterface getHeliumBalloonIntegrationHook() {
		return heliumBalloonHook;
	}

	// Main

	public void initializeStaticIntegrations(OstereierOrchestrator orchestrator) {
		initializePAPIHook(orchestrator.getGameManager());
		initializeBetonQuestHook();
		initializeHeliumBalloonHook(orchestrator.getPlugin());
	}

	public void disable() {
		if (heliumBalloonHook != null) {
			heliumBalloonHook.disable();
			heliumBalloonHook = null;
		}
		if (placeholderHook != null) {
			placeholderHook.unregister();
			placeholderHook = null;
		}
		if (citizensHook != null) {
			citizensHook.disable();
			citizensHook = null;
		}
	}

	public Set<String> getActiveIntegrations() {
		Set<String> activeIntegrations = new TreeSet<>();
		if (hasVault()) {
			activeIntegrations.add(VAULT_NAME);
		}
		if (hasTNE()) {
			activeIntegrations.add(TNE_NAME);
		}
		if (hasCitizens()) {
			activeIntegrations.add(CITIZENS_NAME);
		}
		if (hasPAPI()) {
			activeIntegrations.add(PAPI_NAME);
		}
		if (hasBetonQuest()) {
			activeIntegrations.add(BETONQUEST_NAME);
		}
		if (hasHeliumBalloon()) {
			activeIntegrations.add(HELIUMBALLOON_NAME);
		}
		return activeIntegrations;
	}

}
