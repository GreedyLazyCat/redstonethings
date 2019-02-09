package com.greedycat.redstonethings.capabilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.Constants;

public class PlayerRedstoneEnergyCapability {
	@CapabilityInject(PlayerRedstoneEnergy.class)
	public static Capability<PlayerRedstoneEnergy> PLAYER_ENERGY = null;
	public static void register() {
		CapabilityManager.INSTANCE.register(PlayerRedstoneEnergy.class, new Capability.IStorage<PlayerRedstoneEnergy>()
        {

			@Override
			public NBTBase writeNBT(Capability<PlayerRedstoneEnergy> capability, PlayerRedstoneEnergy instance, EnumFacing side) {
				
				NBTTagCompound compound = new NBTTagCompound();
				compound.setInteger("energy", instance.getEnergyStored());
				compound.setInteger("max_energy", instance.getMaxEnergyStored());
				
				return compound;
			}

			@Override
			public void readNBT(Capability<PlayerRedstoneEnergy> capability, PlayerRedstoneEnergy instance, EnumFacing side,
					NBTBase nbt) {
				NBTTagCompound compound = (NBTTagCompound) nbt;
				instance.setMaxEnergyStored(compound.getInteger("max_energy"));
				instance.setEnergyStored(compound.getInteger("energy"));
			}
            
        }, PlayerRedstoneEnergy::new);
	}
}
