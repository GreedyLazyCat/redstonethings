package com.greedycat.redstonethings.block;

import java.security.PublicKey;
import java.util.ArrayDeque;
import java.util.ArrayList;

import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.tile.BlockTileEntity;
import com.greedycat.redstonethings.tile.GeneratorTile;
import com.greedycat.redstonethings.tile.WireTile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import scala.reflect.internal.Trees.If;
import scala.reflect.internal.Trees.This;

public class Wire extends Block{

	public Wire() {
		super(Material.CIRCUITS);

		this.setRegistryName("wire");
		this.setUnlocalizedName("wire");
		this.setCreativeTab(CreativeTabs.COMBAT);
	}
	
	public ArrayList<BlockPos> buildNetwork(IBlockAccess worldIn, BlockPos start) {
		ArrayList<BlockPos> checked = new ArrayList<>();
		ArrayList<BlockPos> generators = new ArrayList<>();
		ArrayList<BlockPos> storages = new ArrayList<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);
		
		queue.offer(start);
		checked.add(start);
		while (!queue.isEmpty()) {
			BlockPos nPos = queue.poll();
			TileEntity tile = worldIn.getTileEntity(nPos);
			if(tile != null) {
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
					generators.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {
				BlockPos child = nPos.offset(face);
				TileEntity tileEntity = worldIn.getTileEntity(child);
				if(tileEntity != null) {
					if(tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
						generators.add(child);
					}
					if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
						storages.add(child);
					}
				}
				if(!checked.contains(child) && worldIn.getBlockState(child).getBlock() == this) {
					checked.add(child);
					queue.addLast(child);
					//placer.sendMessage(new TextComponentString(poss.size().toString()));
				}
			}	
		}
		
		if(!generators.isEmpty()) {
			for (int i = 0; i < generators.size(); i++) {
				BlockPos gPos = generators.get(i);
				TileEntity tileEntity = worldIn.getTileEntity(gPos);
				if(tileEntity != null && tileEntity instanceof GeneratorTile) {
					GeneratorTile generator = (GeneratorTile) tileEntity;
					generator.setStorages(storages);
				}
			}
		}
		
		System.out.println("Cables: " + checked.size());
		return generators;
	}
	
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		if(!worldIn.isRemote) {
			ArrayList<BlockPos> checked = new ArrayList<>();
			ArrayList<BlockPos> generators = new ArrayList<>();
			ArrayList<BlockPos> storages = new ArrayList<>();
			ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);
			
			queue.offer(pos);
			checked.add(pos);
			while (!queue.isEmpty()) {
				BlockPos nPos = queue.poll();
				for (EnumFacing face : EnumFacing.VALUES) {
					BlockPos child = nPos.offset(face);
					TileEntity tileEntity = worldIn.getTileEntity(child);
					if(tileEntity != null) {
						if(tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
							generators.add(child);
						}
						if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
							storages.add(child);
						}
					}
					if(!checked.contains(child) && worldIn.getBlockState(child).getBlock() == this) {
						checked.add(child);
						queue.addLast(child);
						//placer.sendMessage(new TextComponentString(poss.size().toString()));
					}
				}	
			}
			System.out.println(checked.size());
			System.out.println("Generators: " + generators.size());
			System.out.println("Storages: " + storages.size());
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockPos p = pos.offset(face);
			TileEntity tileEntity = worldIn.getTileEntity(p);
			if(tileEntity != null && tileEntity instanceof GeneratorTile) {
				buildNetwork(worldIn, p);
			}
			if(worldIn.getBlockState(p).getBlock() instanceof Wire) {
				Wire wire = (Wire) worldIn.getBlockState(p).getBlock();
				wire.buildNetwork(worldIn, p);
			}
		}
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub

		this.buildNetwork(worldIn, pos);
		super.onBlockAdded(worldIn, pos, state);
	}
	
	
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// TODO Auto-generated method stub
		this.buildNetwork(worldIn, pos);
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}

	/*
	@Override
	public Class<WireTile> getTileEntityClass() {
		return WireTile.class;
	}

	@Override
	public WireTile createTileEntity(World world, IBlockState blockState) {
		return new WireTile();
	}*/

}
