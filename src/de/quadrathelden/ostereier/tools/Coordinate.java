package de.quadrathelden.ostereier.tools;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public record Coordinate(int x, int y, int z) {

	public Vector toVector() {
		return new Vector(x, y, z);
	}

	public Location toLocation(World world) {
		return toVector().toLocation(world);
	}

	public Coordinate up() {
		return new Coordinate(x, y + 1, z);
	}

	public Coordinate down() {
		return new Coordinate(x, y - 1, z);
	}

	public Coordinate west() {
		return new Coordinate(x - 1, y, z);
	}

	public Coordinate east() {
		return new Coordinate(x + 1, y, z);
	}

	public Coordinate north() {
		return new Coordinate(x, y, z - 1);
	}

	public Coordinate south() {
		return new Coordinate(x, y, z + 1);
	}

	public Coordinate neighbor(BlockFace blockFace) {
		switch (blockFace) {
		case NORTH:
			return north();
		case SOUTH:
			return south();
		case WEST:
			return west();
		case EAST:
			return east();
		case UP:
			return up();
		case DOWN:
			return down();
		default:
			return this;
		}
	}

	public static Coordinate of(Vector vector) {
		return new Coordinate(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}

	public static Coordinate of(Location location) {
		return new Coordinate(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

}
