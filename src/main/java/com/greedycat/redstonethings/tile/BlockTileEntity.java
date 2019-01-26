package com.greedycat.redstonethings.tile;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockTileEntity<T extends TileEntity> extends Block {

    

    public BlockTileEntity(Material materialIn) {
		super(materialIn);
		// TODO Auto-generated constructor stub
	}

	public abstract Class<T> getTileEntityClass();

    public T getTileEntity(IBlockAccess world, BlockPos position) {

        return (T) world.getTileEntity(position);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState blockState) {

        return true;
    }

    @Nullable
    @Override
    public abstract T createTileEntity(World world, IBlockState blockState);
}
