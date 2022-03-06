package de.quadrathelden.ostereier.chunktickets;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.tools.Coordinate;

public class ChunkTicket {

	private final int chunkX;
	private final int chunkZ;
	private final World world;
	protected final Plugin plugin;
	protected Set<ChunkTicketOwner> owners = new HashSet<>();

	public ChunkTicket(Plugin plugin, World world, Coordinate coordinate) {
		this.chunkX = cor2chunk(coordinate.x());
		this.chunkZ = cor2chunk(coordinate.z());
		this.world = world;
		this.plugin = plugin;
	}

	public static int cor2chunk(int cor) {
		return Math.floorDiv(cor, 16);
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}

	public World getWorld() {
		return world;
	}

	public boolean hasLocation(World testWorld, Coordinate testCoordinate) {
		int clX = cor2chunk(testCoordinate.x());
		int clZ = cor2chunk(testCoordinate.z());
		return ((clX == getChunkX()) && (clZ == getChunkZ()) && world.equals(testWorld));
	}

	public boolean hasOwner(ChunkTicketOwner testOwner) {
		for (ChunkTicketOwner myOwner : owners) {
			if (myOwner == testOwner) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return owners.isEmpty();
	}

	protected void addMinecraftChunkTicket() {
		world.addPluginChunkTicket(chunkX, chunkZ, plugin);
	}

	protected void removeMinecraftChunkTicket() {
		world.removePluginChunkTicket(chunkX, chunkZ, plugin);
	}

	public void addOwner(ChunkTicketOwner newOwner) {
		boolean oldIsEmpty = isEmpty();
		owners.add(newOwner);
		boolean newIsEmpty = isEmpty();
		if (oldIsEmpty && !newIsEmpty) {
			addMinecraftChunkTicket();
		}
	}

	public void removeOwner(ChunkTicketOwner oldOwner) {
		boolean oldIsEmpty = isEmpty();
		owners.remove(oldOwner);
		boolean newIsEmpty = isEmpty();
		if (newIsEmpty && !oldIsEmpty) {
			removeMinecraftChunkTicket();
		}
	}
	
	public void removeAll() {
		for (ChunkTicketOwner myOwner : new HashSet<>(owners)) {
			removeOwner(myOwner);
		}
	}

}
