package de.quadrathelden.ostereier.integrations.heliumballoon;

import org.bukkit.entity.Entity;

import de.quadrathelden.ostereier.displays.DisplayEggBalloon;
import de.quadrathelden.ostereier.exception.OstereierException;

public interface HeliumBalloonInterface {

	public void addWall(DisplayEggBalloon displayEggBalloon) throws OstereierException;

	public void removeWall(DisplayEggBalloon displayEggBalloon);

	public DisplayEggBalloon findWall(Entity entity);

}
