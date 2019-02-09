package com.greedycat.redstonethings.items;

import com.greedycat.redstonethings.block.BlackHoleBlock;
import com.greedycat.redstonethings.proxy.CommonProxy;
import com.greedycat.redstonethings.tile.BlackHoleTile;
import com.greedycat.redstonethings.util.RegBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlackHole extends Item{
	public BlackHole() {
		this.setRegistryName("blackhole");
		this.setUnlocalizedName("blackhole");
		this.setCreativeTab(CommonProxy.redstone_things_tab);
	}
	
	public void setBlackHole(IBlockState state, EnumFacing facing, BlockPos pos, World world) {
		if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
			for(int ix= -1; ix<2; ix++) {
				for (int iz = -1; iz < 2; iz++) {
					
					BlockPos nPos = pos.add(ix, 0, iz);
					IBlockState nState = world.getBlockState(nPos);
					if(!nState.getBlock().hasTileEntity(nState) && world.setBlockState(nPos, RegBlocks.blackHoleBlock.getDefaultState())){
						TileEntity tile = world.getTileEntity(nPos);
						
						if(tile != null && tile instanceof BlackHoleTile) {
							BlackHoleTile hole = (BlackHoleTile) tile;
							hole.setBlock(nState);
						}
					}
					
				}
			}
		}
		else if(facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
			for(int ix= -1; ix<2; ix++) {
				for (int iy = -1; iy < 2; iy++) {
					BlockPos nPos = pos.add(0, ix, iy);
					IBlockState nState = world.getBlockState(nPos);
					TileEntity tileEntity = world.getTileEntity(nPos);
					
					if(!nState.getBlock().hasTileEntity(nState) && world.setBlockState(nPos, RegBlocks.blackHoleBlock.getDefaultState())){
						TileEntity tile = world.getTileEntity(nPos);
						
						if(tile != null && tile instanceof BlackHoleTile) {
							BlackHoleTile hole = (BlackHoleTile) tile;
							hole.setBlock(nState);
						}
					}
					
				}
			}
		}
		else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			for(int ix= -1; ix<2; ix++) {
				for (int iy = -1; iy < 2; iy++) {
					
					BlockPos nPos = pos.add(ix, iy, 0);
					IBlockState nState = world.getBlockState(nPos);
					if(!nState.getBlock().hasTileEntity(nState) && world.setBlockState(nPos, RegBlocks.blackHoleBlock.getDefaultState())){
						TileEntity tile = world.getTileEntity(nPos);
						
						if(tile != null && tile instanceof BlackHoleTile) {
							BlackHoleTile hole = (BlackHoleTile) tile;
							hole.setBlock(nState);
						}
					}
					
				}
			}
		}
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		IBlockState state = worldIn.getBlockState(pos).getActualState(worldIn, pos);
		
		setBlackHole(state, facing, pos, worldIn);
		
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
