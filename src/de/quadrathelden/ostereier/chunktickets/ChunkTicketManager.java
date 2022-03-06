package de.quadrathelden.ostereier.chunktickets;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;
import de.quadrathelden.ostereier.tools.Coordinate;

public class ChunkTicketManager {

	protected final Plugin plugin;
	protected List<ChunkTicket> chunkTickets = new ArrayList<>();

	public ChunkTicketManager(OstereierOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
	}

	public ChunkTicket findChunkTicket(World world, Coordinate coordinate) {
		for (ChunkTicket myChunkTicket : chunkTickets) {
			if (myChunkTicket.hasLocation(world, coordinate)) {
				return myChunkTicket;
			}
		}
		return null;
	}

	public List<ChunkTicket> findChunkTicketsForOwner(ChunkTicketOwner owner) {
		List<ChunkTicket> resultList = new ArrayList<>();
		for (ChunkTicket myChunkTicket : chunkTickets) {
			if (myChunkTicket.hasOwner(owner)) {
				resultList.add(myChunkTicket);
			}
		}
		return resultList;
	}

	public void addChunkTicket(ChunkTicketOwner owner, World world, Coordinate coordinate) {
		ChunkTicket myChunkTicket = findChunkTicket(world, coordinate);
		if (myChunkTicket == null) {
			myChunkTicket = new ChunkTicket(plugin, world, coordinate);
			chunkTickets.add(myChunkTicket);
		}
		myChunkTicket.addOwner(owner);
	}

	protected void removeChunkTicket(ChunkTicketOwner owner, ChunkTicket chunkTicket) {
		chunkTicket.removeOwner(owner);
		if (chunkTicket.isEmpty()) {
			chunkTickets.remove(chunkTicket);
		}

	}

	public void removeChunkTicket(ChunkTicketOwner owner, World world, Coordinate coordinate) {
		ChunkTicket myChunkTicket = findChunkTicket(world, coordinate);
		if (myChunkTicket == null) {
			return;
		}
		removeChunkTicket(owner, myChunkTicket);
	}

	public void removeChunkTicket(ChunkTicketOwner owner) {
		List<ChunkTicket> ownerChunkTickets = findChunkTicketsForOwner(owner);
		for (ChunkTicket myChunkTicket : ownerChunkTickets) {
			removeChunkTicket(owner, myChunkTicket);
		}
	}

	public void removeAllFromWorld(World world) {
		for (ChunkTicket myChunkTicket : new ArrayList<>(chunkTickets)) {
			if (myChunkTicket.getWorld().equals(world)) {
				myChunkTicket.removeAll();
				chunkTickets.remove(myChunkTicket);
			}
		}
	}

	public void disable() {
		for (ChunkTicket myChunkTicket : new ArrayList<>(chunkTickets)) {
			myChunkTicket.removeAll();
			chunkTickets.remove(myChunkTicket);
		}
	}

}
