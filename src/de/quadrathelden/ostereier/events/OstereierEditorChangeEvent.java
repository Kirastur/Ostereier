package de.quadrathelden.ostereier.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.quadrathelden.ostereier.config.design.ConfigTemplate;
import de.quadrathelden.ostereier.config.subsystem.ConfigEditor;

public class OstereierEditorChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final World world;
	private final ConfigTemplate template;
	private final ConfigEditor configEditor;
	private boolean displayActive = true;
	private boolean statusbarActive = true;
	private boolean listenerActive = true;

	OstereierEditorChangeEvent(World world, ConfigTemplate template, ConfigEditor configEditor) {
		this.world = world;
		this.template = template;
		this.configEditor = configEditor;
	}

	public boolean isEditorActive() {
		return (world != null);
	}

	public World getWorld() {
		return world;
	}

	public ConfigTemplate getTemplate() {
		return template;
	}

	public ConfigEditor getConfigEditor() {
		return configEditor;
	}

	public boolean isDisplayActive() {
		return displayActive;
	}

	public void setDisplayActive(boolean newDisplayActive) {
		displayActive = newDisplayActive;
	}

	public boolean isStatusbarActive() {
		return statusbarActive;
	}

	public void setStatusbarActive(boolean newStatusbarActive) {
		statusbarActive = newStatusbarActive;
	}

	public boolean isListenerActive() {
		return listenerActive;
	}

	public void setListenerActive(boolean newListenerActive) {
		listenerActive = newListenerActive;
	}

	EditorChangeResult getResult() {
		return new EditorChangeResult(displayActive, statusbarActive, listenerActive);
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}