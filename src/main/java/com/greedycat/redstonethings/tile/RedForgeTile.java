package com.greedycat.redstonethings.tile;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.api.IEnergyStorage;
import com.greedycat.redstonethings.capabilities.EnergyGenerator;
import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorage;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.inventory.InventoryBase;
import com.greedycat.redstonethings.network.RedForgeMessage;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import scala.inline;

public class RedForgeTile extends GeneratorTile implements ITickable{
	
	public InventoryBase inventory;
	public int SIZE = 2;
	private ItemStackHandler handler = new ItemStackHandler(SIZE);
	private EnergyGenerator generator = new EnergyGenerator(5000,10000) {
		@Override
		public void onChages() {
			markDirty();
		};
	};
	
	private int timer;
	
	public RedForgeTile() {
		// TODO Auto-generated constructor stub
		inventory = new InventoryBase(this, 3, 64);
	}
	
	public void setStoredEnergy(int storedEnergy) {
		generator.setEnergyStored(storedEnergy);
	}
	
	public int getStoredEnergy() {
		return generator.getEnergyStored();
	}
	public int getMaxEnergyStored() {
		return generator.getMaxEnergyStored();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		if(capability == EnergyGeneratorCapability.ENERGY_GENERATOR) return true;
		else return false;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) this.handler;
		if(capability == EnergyGeneratorCapability.ENERGY_GENERATOR) return (T) this.generator;
		return super.getCapability(capability, facing);
	}
	
	@SideOnly(Side.CLIENT)
	public int energyPercent() {
		return generator.getEnergyStored() /(generator.getMaxEnergyStored()/100);
	}
	
	/* Inventory */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", handler.serializeNBT());
        return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		handler.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
	}
	
	/* /Inventory */
	
	@Override
	public void onLoad() {
		generator.setOutput(500);
		super.onLoad();
	}
	
	@Override
	public void update() {
		
		timer++;
		
		if(timer == 20 * 5) {
			for (int i = 0; i < storages.size(); i++) {
				BlockPos sPos = storages.get(i);
				TileEntity tileEntity = this.getWorld().getTileEntity(sPos);
				if(tileEntity != null) {
					if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)){
						EnergyStorage storage = (EnergyStorage)tileEntity.getCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
						storage.receiveEnergy(generator.getOutput(), false);
					}
				}
			}
			timer = 0;
		}
		
		ItemStack redstone_stack = handler.getStackInSlot(1);
		if(redstone_stack != null && redstone_stack.getItem() == Items.REDSTONE) {
			System.out.println("Redstone");
		}
		this.sendChanges();
	}
	
	public void sendChanges() {
		if(!this.getWorld().isRemote)
			BaseClass.NETWORK.sendToAll(new RedForgeMessage(generator.getEnergyStored(), this.getPos()));
	}
	
}
