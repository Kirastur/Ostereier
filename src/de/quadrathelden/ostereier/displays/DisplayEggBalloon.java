package de.quadrathelden.ostereier.displays;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.integrations.heliumballoon.HeliumBalloonInterface;
import de.quadrathelden.ostereier.tools.Coordinate;
import de.quadrathelden.ostereier.tools.Message;

public class DisplayEggBalloon extends DisplayEggSimple {

	public static final String HELIUM_BALLOON_SECTION_NAME = "HeliumBalloon";

	protected final HeliumBalloonInterface heliumBalloonInterface;

	public DisplayEggBalloon(World world, Coordinate coordinate, ConfigEgg configEgg,
			HeliumBalloonInterface heliumBalloonInterface) throws OstereierException {
		super(world, coordinate, configEgg, null);
		if (heliumBalloonInterface == null) {
			throw new OstereierException(Message.BALLOON_NOT_AVAIL);
		}
		this.heliumBalloonInterface = heliumBalloonInterface;
	}

	@Override
	public void draw(boolean isEditor, boolean collectable) {
		try {
			heliumBalloonInterface.addWall(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void undraw() {
		heliumBalloonInterface.removeWall(this);
	}

	@Override
	public boolean hasEntity(Entity entity) {
		return this.equals(heliumBalloonInterface.findWall(entity));
	}

	@Override
	public void keepAlive() {
		// Do nothing
	}

}
