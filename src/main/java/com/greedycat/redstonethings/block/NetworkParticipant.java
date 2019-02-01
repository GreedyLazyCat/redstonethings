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
		EnergyNetworkList list = EnergyNetworkUtil.getEnergyNetworkList(worldIn);
		
		if(list != null) {
			TileEntity tile = worldIn.getTileEntity(pos);
			
			if(tile != null && tile instanceof NetworkParticipantTile) {
				System.out.println("Tile");
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				
				if(participant.hasNetworkId()) {
					EnergyNetwork network = list.getNetwork(participant.getNetworkId());
					
					if(network != null) {
						network.remove(participant.getPos());
						boolean check = false;
						
						for (BlockPos nPos : network.getParticipants()) {
							TileEntity tileEntity = worldIn.getTileEntity(nPos);
							
							if(tileEntity != null && tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
								check = true;
								break;
							}
						}
						
						if(!check) {
							System.out.println("Network have no generator");
							list.removeNetwork(participant.getNetworkId());
							EnergyNetworkUtil.setNetworkId(worldIn, pos, -1);
						}
						
					}
				}
			
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	
}
