package de.quadrathelden.ostereier.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import de.quadrathelden.ostereier.config.ConfigManager;
import de.quadrathelden.ostereier.config.subsystem.ConfigEconomy;
import de.quadrathelden.ostereier.config.subsystem.ConfigScoreboard;
import de.quadrathelden.ostereier.economy.EconomyManager;
import de.quadrathelden.ostereier.economy.EconomyProvider;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.text.TextManager;

public class PlayerScoreboard {

	protected final Player player;
	protected final Objective objective;
	protected final TextManager textManager;
	protected final ConfigManager configManager;
	protected final EventManager eventManager;
	protected final EconomyManager economyManager;
	protected int lifeTime = 0;

	public PlayerScoreboard(Player player, Objective objective, TextManager textManager, ConfigManager configManager,
			EventManager eventManager, EconomyManager economyManager) {
		this.player = player;
		this.textManager = textManager;
		this.objective = objective;
		this.configManager = configManager;
		this.eventManager = eventManager;
		this.economyManager = economyManager;
		resetLifetime();
	}

	public Player getPlayer() {
		return player;
	}

	protected void resetLifetime() {
		ConfigScoreboard configScoreboard = configManager.getConfigScoreboard();
		if (configScoreboard.isPermanent()) {
			lifeTime = 1;
		} else {
			lifeTime = configScoreboard.getDisplayTimeout();
		}
	}

	public boolean isEOL() {
		return (lifeTime <= 0);
	}

	public void updateScoreboard() throws OstereierException {
		if (eventManager.sendScoreboardUpdateEvent(player, objective)) {
			EconomyProvider economyProvider = economyManager.getEconomyProvider();
			ConfigScoreboard configScoreboard = configManager.getConfigScoreboard();
			ConfigEconomy configEconomy = configManager.getConfigEconomy();
			for (String myCurrency : configScoreboard.getElements()) {
				int value;
				if (myCurrency.equals(configEconomy.getEggCounterCurrencyName())) {
					value = economyProvider.getEggs(player);
				} else {
					value = economyProvider.getPoints(player, myCurrency);
				}
				if (value > 0) {
					String name = configManager.findCurrency(0, myCurrency, player);
					Score score = objective.getScore(name);
					score.setScore(value);
				}
			}
		}
		resetLifetime();
	}

	void handleScheduler() {
		ConfigScoreboard configScoreboard = configManager.getConfigScoreboard();
		if (!configScoreboard.isPermanent() && (lifeTime > 0)) {
			lifeTime = lifeTime - 1;
		}
	}

}
