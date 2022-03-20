package de.quadrathelden.ostereier.statistics;

import java.time.LocalDateTime;

import org.bukkit.World;

import de.quadrathelden.ostereier.config.design.ConfigEgg;

public record AggregatedDimensions(LocalDateTime intervalStart, World world, ConfigEgg egg, String currency) {

}
