package de.quadrathelden.ostereier.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.config.design.ConfigEgg;

public class OstereierRefineAnimalEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	protected final ConfigEgg configEgg;
	protected final boolean isEditor;
	protected LivingEntity livingEntity;

	protected OstereierRefineAnimalEvent(LivingEntity livingEntity, ConfigEgg configEgg, boolean isEditor) {
		this.livingEntity = livingEntity;
		this.configEgg = configEgg;
		this.isEditor = isEditor;
	}

	public LivingEntity getLivingEntity() {
		return livingEntity;
	}

	public void setLivingEntity(LivingEntity livingEntity) {
		this.livingEntity = livingEntity;
	}

	public ConfigEgg getConfigEgg() {
		return configEgg;
	}

	public boolean isEditor() {
		return isEditor;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
