package com.greedycat.redstonethings.capabilities;

import java.util.HashSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyNetwork {
	public HashSet<BlockPos> participants;
	
	public EnergyNetwork() {}
	
	public EnergyNetwork(HashSet<BlockPos> participants) {
		this.participants = participants;
	}
	public void add(BlockPos pos) {
		participants.add(pos);
	}
	
	public void remove(BlockPos pos) {
		participants.remove(pos);
	}
	
	public boolean hasGenerators(World world) {
		for (BlockPos nPos : this.getParticipants()) {
			TileEntity tileEntity = world.getTileEntity(nPos);
			
			if(tileEntity != null && tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
				return true;
			}
		}
		return false;
	}
	
	public void setParticipants(HashSet<BlockPos> participants) {
		this.participants = participants;
	}
	
	public HashSet<BlockPos> getParticipants() {
		return participants;
	}
	
}
