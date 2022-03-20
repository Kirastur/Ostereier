package de.quadrathelden.ostereier.events;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Objective;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.bunny.Bunny;
import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.config.design.ConfigSpawnpoint;
import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.config.shop.ConfigShopOffer;
import de.quadrathelden.ostereier.displays.DisplayEgg;
import de.quadrathelden.ostereier.economy.EconomyProvider;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.game.egg.GameEgg;
import de.quadrathelden.ostereier.game.world.PlayerScore;
import de.quadrathelden.ostereier.statistics.AggregatedEntry;
import de.quadrathelden.ostereier.statistics.CollectDetailEntry;
import de.quadrathelden.ostereier.tools.Coordinate;

public class EventManager {

	protected final Plugin plugin;
	protected final ConfigManager configManager;

	public EventManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.configManager = orchestrator.getConfigManager();
	}

	public DisplayEgg sendCustomDrawEggEvent(World world, Coordinate coordinate, ConfigEgg configEgg) {
		OstereierCustomDrawEggEvent event = new OstereierCustomDrawEggEvent(world, coordinate, configEgg);
		plugin.getServer().getPluginManager().callEvent(event);
		return event.getDisplayEgg();
	}

	public EditorChangeResult sendEditorChangeEvent(World world, ConfigTemplate template) {
		OstereierEditorChangeEvent event = new OstereierEditorChangeEvent(world, template,
				configManager.getConfigEditor());
		plugin.getServer().getPluginManager().callEvent(event);
		return event.getResult();
	}

	public EconomyProvider sendGetCustomEconomyProvider() throws OstereierException {
		OstereierGetCustomEconomyProviderEvent event = new OstereierGetCustomEconomyProviderEvent(
				configManager.getConfigEconomy());
		plugin.getServer().getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			return event.getEconomyProvider();
		}
		if (event.getCancelReason() != null) {
			throw event.getCancelReason();
		}
		return null;
	}

	public Bunny sendGameStartEvent(World world) {
		OstereierGameStartEvent event = new OstereierGameStartEvent(world, configManager.getConfigBunny(),
				configManager.getConfigGame());
		plugin.getServer().getPluginManager().callEvent(event);
		return event.getCustomBunny();
	}

	public void sendGameStopEvent(World world, List<PlayerScore> playerScores) {
		OstereierGameStopEvent event = new OstereierGameStopEvent(world, configManager.getConfigGame(), playerScores);
		plugin.getServer().getPluginManager().callEvent(event);
	}

	public GameEgg sendEggSpawnEvent(World world, ConfigEgg configEgg, ConfigSpawnpoint configSpawnpoint) {
		OstereierEggSpawnEvent event = new OstereierEggSpawnEvent(world, configEgg, configSpawnpoint);
		plugin.getServer().getPluginManager().callEvent(event);
		return event.getCustomGameEgg();
	}

	public boolean sendEggPickupEvent(World world, Coordinate coordinate, ConfigEgg configEgg, Player player) {
		OstereierEggPickupEvent event = new OstereierEggPickupEvent(world, coordinate, configEgg, player);
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}

	public void sendEggCancelEvent(World world, Coordinate coordinate, ConfigEgg configEgg) {
		OstereierEggCancelEvent event = new OstereierEggCancelEvent(world, coordinate, configEgg);
		plugin.getServer().getPluginManager().callEvent(event);
	}

	public boolean sendScoreboardUpdateEvent(Player player, Objective objective) {
		OstereierScoreboardUpdateEvent event = new OstereierScoreboardUpdateEvent(player,
				configManager.getConfigScoreboard(), objective);
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}

	public boolean sendPlayerBuyItemEvent(Player player, ItemStack itemStack) {
		OstereierPlayerBuyItemEvent event = new OstereierPlayerBuyItemEvent(player, itemStack);
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}

	public boolean sendPlayerSellItemEvent(Player player, ItemStack itemStack) {
		OstereierPlayerSellItemEvent event = new OstereierPlayerSellItemEvent(player, itemStack);
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}

	public Inventory sendPlayerOpenShopEvent(Player player, Inventory inventory) {
		OstereierPlayerOpenShopEvent event = new OstereierPlayerOpenShopEvent(player, inventory);
		plugin.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return null;
		} else {
			return event.getInventory();
		}
	}

	public ConfigShopOffer sendShopFindOfferEvent(String offerName) {
		OstereierShopFindOfferEvent event = new OstereierShopFindOfferEvent(offerName);
		plugin.getServer().getPluginManager().callEvent(event);
		return event.getOffer();
	}

	public void sendStatisticEvent(boolean isAsync, LocalDateTime intervalStart, Collection<CollectDetailEntry> collectDetails,
			Map<World, Integer> worldSegmentSizes, Collection<AggregatedEntry> aggregateds) {
		OstereierStatisticEvent event = new OstereierStatisticEvent(isAsync, intervalStart, collectDetails, worldSegmentSizes,
				aggregateds);
		plugin.getServer().getPluginManager().callEvent(event);
	}

}
