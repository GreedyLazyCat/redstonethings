package com.greedycat.redstonethings.capabilities;

import com.greedycat.redstonethings.api.IEnergyStorage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class EnergyStorageCapability {
	@CapabilityInject(IEnergyStorage.class)
	public static Capability<IEnergyStorage> ENERGY_STORAGE = null;
	public static void register() {
		CapabilityManager.INSTANCE.register(IEnergyStorage.class, new Capability.IStorage<IEnergyStorage>()
        {

			@Override
			public NBTBase writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, EnumFacing side) {
				
				NBTTagCompound nbtTagList = new NBTTagCompound();
				
				nbtTagList.setInteger("energy", instance.getEnergyStored());
				nbtTagList.setInteger("max_energy_stored", instance.getMaxEnergyStored());
				
				return nbtTagList;
			}

			@Override
			public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, EnumFacing side,
					NBTBase nbt) {
				NBTTagCompound nbtTagCompound = (NBTTagCompound) nbt;
				instance.setEnergyStored(nbtTagCompound.getInteger("energy"));
				instance.setMaxEnergyStored(nbtTagCompound.getInteger("max_energy_stored"));
				
			}
            
        }, EnergyStorage::new);
	}
}
