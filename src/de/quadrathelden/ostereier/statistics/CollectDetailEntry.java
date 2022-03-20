package de.quadrathelden.ostereier.statistics;

import java.time.LocalDateTime;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.tools.Coordinate;

public record CollectDetailEntry(LocalDateTime timestamp, World world, Coordinate coordinate, ConfigEgg egg,
		int rewardAmount, String rewardCurrency, OfflinePlayer player) {

}
