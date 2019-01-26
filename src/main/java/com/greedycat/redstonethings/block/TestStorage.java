package com.greedycat.redstonethings.block;

import com.greedycat.redstonethings.capabilities.EnergyStorage;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.tile.BlockTileEntity;
import com.greedycat.redstonethings.tile.TestStorageTile;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestStorage extends BlockTileEntity<TestStorageTile>{

	public TestStorage() {
		super(Material.ANVIL);
		this.setRegistryName("teststorage");
		this.setUnlocalizedName("teststorage");
		this.setCreativeTab(CreativeTabs.COMBAT);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile != null && tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
			EnergyStorage storage = (EnergyStorage)tile.getCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
			System.out.println(storage.getEnergyStored());
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public Class<TestStorageTile> getTileEntityClass() {
		// TODO Auto-generated method stub
		return TestStorageTile.class;
	}

	@Override
	public TestStorageTile createTileEntity(World world, IBlockState blockState) {
		// TODO Auto-generated method stub
		return new TestStorageTile();
	}

}
