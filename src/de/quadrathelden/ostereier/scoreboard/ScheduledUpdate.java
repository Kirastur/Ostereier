package de.quadrathelden.ostereier.scoreboard;

import org.bukkit.entity.Player;

public class ScheduledUpdate {

	protected final Player player;
	protected int lifeTime = 5;

	public ScheduledUpdate(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isEOL() {
		return lifeTime <= 0;
	}

	protected void handleScheduler() {
		if (lifeTime > 0) {
			lifeTime = lifeTime - 1;
		}
	}

}
