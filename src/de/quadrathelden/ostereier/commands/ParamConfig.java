package de.quadrathelden.ostereier.commands;

import org.bukkit.entity.Player;

public enum ParamConfig {

	EGGS("eggs", "Eier"),
	TEMPLATES("templates", "Templates"),
	WORLDS("worlds", "Worlds");

	private final String paramEN;
	private final String paramDE;

	private ParamConfig(String paramEN, String paramDE) {
		this.paramEN = paramEN;
		this.paramDE = paramDE;
	}

	public boolean hasText(String paramText) {
		return (paramText.equalsIgnoreCase(paramEN) || paramText.equalsIgnoreCase(paramDE));
	}

	public String getParamName(Player player) {
		if ((player != null) && (player.getLocale().startsWith("de"))) {
			return paramDE;
		} else {
			return paramEN;
		}
	}

}
