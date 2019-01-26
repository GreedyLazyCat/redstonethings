package com.greedycat.redstonethings.tile;

import com.greedycat.redstonethings.api.ConnectedWith;
import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.util.WireInf;

import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class WireTile extends TileEntity implements ITickable{
	
	private BlockPos[] coords = 
						{new BlockPos(0, 1, 0),
						 new BlockPos(0, -1, 0),
						 new BlockPos(-1, 0, 0),
						 new BlockPos(1, 0, 0),
						 new BlockPos(0, 0, 1),
						 new BlockPos(0, 0, -1)};
	private int counter;
	private ConnectedWith connection;
	private int distance_to_generator;
	
	@Override
	public void update() {
	}
	
	
	public int getDistanceToGenerator() {
		return distance_to_generator;
	}
	
}
