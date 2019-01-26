package com.greedycat.redstonethings.tile;

import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorage;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class TestStorageTile extends TileEntity{
	protected EnergyStorage storage = new EnergyStorage(0, 10000);
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		if(capability == EnergyStorageCapability.ENERGY_STORAGE) return true;
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		if(capability == EnergyStorageCapability.ENERGY_STORAGE) return (T) this.storage;
		return super.getCapability(capability, facing);
	}
}
