package com.greedycat.redstonethings.tile;

import java.util.Arrays;

import com.greedycat.redstonethings.capabilities.EnergyStorage;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.util.tile.NetworkParticipantTile;
import com.sun.jna.platform.win32.Winioctl.STORAGE_DEVICE_NUMBER;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class PlayerChargerTile extends NetworkParticipantTile{
	
	private EnergyStorage storage = new EnergyStorage(0, 10000);
	
	@Override
	public void onLoad() {
		super.onLoad();
		EnumFacing[] faces = {EnumFacing.UP};
		setFacingForConnection(faces);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(facing != null && Arrays.asList(this.facing).contains(facing) && capability == EnergyStorageCapability.ENERGY_STORAGE) return true;
		else return false;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == EnergyStorageCapability.ENERGY_STORAGE) return (T) this.storage;
		return super.getCapability(capability, facing);
	}
	
}
