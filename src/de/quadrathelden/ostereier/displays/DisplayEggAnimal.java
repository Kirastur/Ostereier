package de.quadrathelden.ostereier.displays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.util.Vector;

import de.quadrathelden.ostereier.config.design.ConfigEgg;
import de.quadrathelden.ostereier.events.EventManager;
import de.quadrathelden.ostereier.tools.Coordinate;

public class DisplayEggAnimal extends DisplayEggSimple {

	protected final EventManager eventManager;
	protected LivingEntity livingEntity = null;

	protected DisplayEggAnimal(World world, Coordinate coordinate, ConfigEgg configEgg, EventManager eventManager) {
		super(world, coordinate, configEgg, null);
		this.eventManager = eventManager;
	}

	@Override
	public void draw(boolean isEditor, boolean collectable) {
		Location location = getCoordinate().toLocation(getWorld());
		location.add(new Vector(0.5, 0.0, 0.5));
		Entity newEntity = getWorld().spawnEntity(location, getConfigEgg().getAnimal());
		if (!(newEntity instanceof LivingEntity)) {
			newEntity.remove();
			return;
		}
		LivingEntity newLivingEntity = (LivingEntity) newEntity;

		newLivingEntity.setPersistent(false);
		newLivingEntity.setCanPickupItems(false);
		newLivingEntity.setCollidable(false);
		newLivingEntity.setInvulnerable(true);
		newLivingEntity.setGravity(false);
		newLivingEntity.setSilent(true);
		newLivingEntity.setRemoveWhenFarAway(false);

		if (newLivingEntity instanceof Monster monster) {
			monster.setAware(false);
		}
		livingEntity = eventManager.sendRefineAnimalEvent(newLivingEntity, getConfigEgg(), isEditor);
		if (!newLivingEntity.equals(livingEntity)) {
			newLivingEntity.remove();
		}
	}

	@Override
	public void undraw() {
		if (livingEntity != null) {
			livingEntity.remove();
			livingEntity = null;
		}
	}

	@Override
	public boolean hasEntity(Entity entity) {
		return ((livingEntity != null) && livingEntity.equals(entity));
	}

	@Override
	public void keepAlive() {
		// Nothing to do
	}

}
