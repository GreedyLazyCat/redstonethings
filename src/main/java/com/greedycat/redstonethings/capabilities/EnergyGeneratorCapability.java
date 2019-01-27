package com.greedycat.redstonethings.capabilities;

import com.greedycat.redstonethings.api.IEnergyGenerator;
import com.greedycat.redstonethings.api.IEnergyStorage;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class EnergyGeneratorCapability {
	public static Capability<IEnergyGenerator> ENERGY_GENERATOR = null;
	public static Capability.IStorage<IEnergyGenerator> storage = new Capability.IStorage<IEnergyGenerator>()
    {

		@Override
		public NBTBase writeNBT(Capability<IEnergyGenerator> capability, IEnergyGenerator instance, EnumFacing side) {
			
			NBTTagCompound nbtTagList = new NBTTagCompound();
			
			nbtTagList.setInteger("energy", instance.getEnergyStored());
			nbtTagList.setInteger("max_energy_stored", instance.getMaxEnergyStored());
			
			return nbtTagList;
		}

		@Override
		public void readNBT(Capability<IEnergyGenerator> capability, IEnergyGenerator instance, EnumFacing side,
				NBTBase nbt) {
			NBTTagCompound nbtTagCompound = (NBTTagCompound) nbt;
			instance.setEnergyStored(nbtTagCompound.getInteger("energy"));
			instance.setMaxEnergyStored(nbtTagCompound.getInteger("max_energy_stored"));
			
		}
        
    };
	public static void register() {
		CapabilityManager.INSTANCE.register(IEnergyGenerator.class, storage, EnergyGenerator::new);
	}
}
