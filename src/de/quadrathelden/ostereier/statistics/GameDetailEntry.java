package de.quadrathelden.ostereier.statistics;

import java.time.LocalDateTime;

import org.bukkit.World;

public record GameDetailEntry(LocalDateTime timestamp, World world, boolean isStarted) {

}
