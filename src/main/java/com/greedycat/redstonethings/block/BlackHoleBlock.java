package com.greedycat.redstonethings.block;

import javax.annotation.Nullable;

import com.greedycat.redstonethings.tile.BlackHoleTile;
import com.greedycat.redstonethings.util.tile.BlockTileEntity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlackHoleBlock extends BlockTileEntity<BlackHoleTile>{

	public BlackHoleBlock() {
		super(Material.AIR);
		this.setRegistryName("blackholeblock");
		this.setUnlocalizedName("blackholeblock");
	}

	@Override
	public Class<BlackHoleTile> getTileEntityClass() {
		return BlackHoleTile.class;
	}

	@Override
	public BlackHoleTile createTileEntity(World world, IBlockState blockState) {
		return new BlackHoleTile();
	}
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }
	
	@Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
	
	@Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

	@Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

}
