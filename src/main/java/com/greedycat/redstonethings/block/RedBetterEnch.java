package com.greedycat.redstonethings.block;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.proxy.CommonProxy;
import com.greedycat.redstonethings.tile.BlockTileEntity;
import com.greedycat.redstonethings.tile.RedBetterEnchTile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedBetterEnch extends BlockTileEntity<RedBetterEnchTile>{
	
	public RedBetterEnch() {
		super(Material.ANVIL);
		this.setRegistryName("redbetterench");
		this.setUnlocalizedName("redbetterench");
		this.setCreativeTab(CommonProxy.redstone_things_tab);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		playerIn.openGui(BaseClass.INSTANCE, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public Class<RedBetterEnchTile> getTileEntityClass() {
		// TODO Auto-generated method stub
		return RedBetterEnchTile.class;
	}

	@Override
	public RedBetterEnchTile createTileEntity(World world, IBlockState blockState) {
		// TODO Auto-generated method stub
		return new RedBetterEnchTile();
	}

}
