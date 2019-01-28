package com.greedycat.redstonethings.capabilities;

import java.util.HashSet;

import net.minecraft.util.math.BlockPos;

public class EnergyNetwork {
	public HashSet<BlockPos> participants;
	
	public void add(BlockPos pos) {
		participants.add(pos);
	}
	
	public void setParticipants(HashSet<BlockPos> participants) {
		this.participants = participants;
	}
	
	public HashSet<BlockPos> getParticipants() {
		return participants;
	}
	
}
