package de.quadrathelden.ostereier.integrations;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.config.subsystem.ConfigNpc;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.shop.ShopManager;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizensImplementation implements Listener, CitizensIntegration {

	protected final Plugin plugin;
	protected final TextManager textManager;
	protected final ConfigNpc configNpc;
	protected final ShopManager shopManager;

	public CitizensImplementation(Plugin plugin, TextManager textManager, ConfigNpc configNpc,
			ShopManager shopManager) {
		this.plugin = plugin;
		this.textManager = textManager;
		this.configNpc = configNpc;
		this.shopManager = shopManager;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@Override
	public void disable() {
		HandlerList.unregisterAll(this);
	}

	protected boolean isMatchingNPC(NPC npc) {
		if (npc.getName().equals(configNpc.getNpcName())) {
			return true;
		}
		try {
			int i = Integer.parseInt(configNpc.getNpcName());
			if (i == npc.getId()) {
				return true;
			}
		} catch (Exception e) {
			// Do nothing
		}
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onNPCLeftClickEvent(NPCLeftClickEvent event) {
		if (!configNpc.isOpenShopUsingLeftMouseclick() || !isMatchingNPC(event.getNPC())) {
			return;
		}
		Player player = event.getClicker();
		try {
			shopManager.openShopGui(player);
		} catch (OstereierException oe) {
			player.sendMessage(oe.getLocalizedFullErrorMessage(textManager, player));
			if (oe.getCause() != null) {
				oe.printStackTrace();
			}
		} catch (Exception e) {
			player.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onNPCRightClickEvent(NPCRightClickEvent event) {
		if (!configNpc.isOpenShopUsingRightMouseclick() || !isMatchingNPC(event.getNPC())) {
			return;
		}
		Player player = event.getClicker();
		try {
			shopManager.openShopGui(player);
		} catch (OstereierException oe) {
			player.sendMessage(oe.getLocalizedFullErrorMessage(textManager, player));
			if (oe.getCause() != null) {
				oe.printStackTrace();
			}
		} catch (Exception e) {
			player.sendMessage(Message.JAVA_EXCEPTION.toString());
			e.printStackTrace();
		}
	}
}
