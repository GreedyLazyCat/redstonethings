package com.greedycat.redstonethings.tile;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.capabilities.EnergyGenerator;
import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorage;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.network.RedBetterEnchMessage;
import com.greedycat.redstonethings.network.RedForgeMessage;

import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class RedBetterEnchTile extends TileEntity implements net.minecraft.util.ITickable{
	public int SIZE = 1;
	private ItemStackHandler handler = new ItemStackHandler(SIZE);
	private EnergyStorage storage = new EnergyStorage(5000,10000) {
		@Override
		public void onChages() {
			markDirty();
		};
	};
	
	@SideOnly(Side.CLIENT)
	public int energyPercent() {
		return storage.getEnergyStored() /(storage.getMaxEnergyStored()/100);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		if(capability == EnergyStorageCapability.ENERGY_STORAGE) return true;
		else return false;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) this.handler;
		if(capability == EnergyStorageCapability.ENERGY_STORAGE) return (T) this.storage;
		return super.getCapability(capability, facing);
	}
	
	public int getMaxEnrgyStored() {
		return storage.getMaxEnergyStored();
	}
	
	public int getEnergyStored() {
		return storage.getEnergyStored();
	}
	
	public void setChanges(int energy_stored) {
		storage.setEnergyStored(energy_stored);
	}
	
	@Override
	public void update() {
		sendChanges();
	}
	
	public void sendChanges() {
		if(!this.getWorld().isRemote) {
			BaseClass.NETWORK.sendToAll(new RedBetterEnchMessage(storage.getEnergyStored(), this.getPos()));
		}
	}

}
