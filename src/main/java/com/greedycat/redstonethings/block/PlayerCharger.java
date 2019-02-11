package com.greedycat.redstonethings.block;

import com.greedycat.redstonethings.tile.PlayerChargerTile;
import com.greedycat.redstonethings.util.tile.BlockEnergyTileEntity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public class PlayerCharger extends BlockEnergyTileEntity<PlayerChargerTile>{

	public PlayerCharger() {
		super(Material.ANVIL);
	}

	@Override
	public Class<PlayerChargerTile> getTileEntityClass() {
		return PlayerChargerTile.class;
	}

	@Override
	public PlayerChargerTile createTileEntity(World world, IBlockState blockState) {
		return new PlayerChargerTile();
	}

}
