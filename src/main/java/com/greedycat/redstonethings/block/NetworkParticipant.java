package com.greedycat.redstonethings.block;

import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyNetwork;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.tile.NetworkParticipantTile;
import com.greedycat.redstonethings.util.EnergyNetworkUtil;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkParticipant extends Block{

	public NetworkParticipant(Material materialIn) {
		super(materialIn);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		EnergyNetworkUtil.breakBlock(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}

	
}
