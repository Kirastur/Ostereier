package de.quadrathelden.ostereier.integrations.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import de.quadrathelden.ostereier.events.OstereierEggPickupEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class BetonQuestOstereierObjective extends Objective implements Listener {

	public static final String BETONQUEST_NOTIFY_NAME = "eggs_to_collect";
	public static final String OSTEREIER_NOTIFY_NAME = "BetonQuestEggsToCollect";

	private final int neededAmount;
	private final boolean notify;
	private final int notifyInterval;

	public BetonQuestOstereierObjective(final Instruction instruction) throws InstructionParseException {
		super(instruction);
		this.template = OstereierData.class;
		this.neededAmount = instruction.getInt();
		this.notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
		this.notify = (instruction.hasArgument("notify") || this.notifyInterval > 1);
	}

	public void handleOstereierEggPickupEvent(final OstereierEggPickupEvent event) throws QuestRuntimeException {
		final String playerID = PlayerConverter.getID(event.getPlayer());
		if (!this.containsPlayer(playerID) || !this.checkConditions(playerID)) {
			return;
		}
		final OstereierData playerData = (OstereierData) this.dataMap.get(playerID);
		playerData.add();
		if (playerData.getAmount() == this.neededAmount) {
			this.completeObjective(playerID);
			return;
		}
		if (this.notify && playerData.getAmount() % this.notifyInterval == 0) {
			Config.sendNotify(this.instruction.getPackage().getName(), playerID, BETONQUEST_NOTIFY_NAME,
					new String[] { String.valueOf(this.neededAmount - playerData.getAmount()) },
					BETONQUEST_NOTIFY_NAME + ",info");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onOstereierEggPickupEvent(final OstereierEggPickupEvent event) {
		try {
			handleOstereierEggPickupEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public String getDefaultDataInstruction() {
		return "0";
	}

	@Override
	public String getProperty(final String name, final String playerID) {
		if ("left".equalsIgnoreCase(name)) {
			return Integer.toString(this.neededAmount - ((OstereierData) this.dataMap.get(playerID)).getAmount());
		}
		if ("amount".equalsIgnoreCase(name)) {
			return Integer.toString(((OstereierData) this.dataMap.get(playerID)).getAmount());
		}
		return "";
	}

	public static class OstereierData extends ObjectiveData {
		private int amount;

		public OstereierData(final String instruction, final String playerID, final String objID) {
			super(instruction, playerID, objID);
			this.amount = Integer.parseInt(instruction);
		}

		private void add() {
			++this.amount;
			this.update();
		}

		private int getAmount() {
			return this.amount;
		}

		@Override
		public String toString() {
			return String.valueOf(this.amount);
		}
	}

}