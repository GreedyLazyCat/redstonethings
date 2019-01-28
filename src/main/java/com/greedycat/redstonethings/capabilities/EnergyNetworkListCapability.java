package com.greedycat.redstonethings.capabilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Multiset.Entry;
import com.greedycat.redstonethings.api.IEnergyStorage;

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

public class EnergyNetworkListCapability {
	@CapabilityInject(EnergyNetworkList.class)
	public static Capability<EnergyNetworkList> NETWORK_LIST = null;
	
	public static void register() {
		CapabilityManager.INSTANCE.register(EnergyNetworkList.class, new Capability.IStorage<EnergyNetworkList>()
        {

			@Override
			public NBTBase writeNBT(Capability<EnergyNetworkList> capability, EnergyNetworkList instance, EnumFacing side) {
				
				NBTTagList mainList = new NBTTagList();
				
				Iterator<Map.Entry<Integer, EnergyNetwork>> iterator = instance.getNetworks().entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<Integer, EnergyNetwork> network = iterator.next();
					
					NBTTagList list = new NBTTagList();
					NBTTagCompound compound = new NBTTagCompound();
					
					compound.setInteger("id", network.getKey());
					for(BlockPos pos : network.getValue().getParticipants()) {
						list.appendTag(NBTUtil.createPosTag(pos));
					}
					compound.setTag("participants", list);
					
					mainList.appendTag(compound);
				}
				
				return mainList;
			}

			@Override
			public void readNBT(Capability<EnergyNetworkList> capability, EnergyNetworkList instance, EnumFacing side,
					NBTBase nbt) {
				NBTTagList mainList = (NBTTagList) nbt;
				HashMap<Integer, EnergyNetwork> networks = new HashMap<>();
				
				for (int i = 0; i < mainList.tagCount(); i++) {
					NBTTagCompound compound = mainList.getCompoundTagAt(i);
					EnergyNetwork network = new EnergyNetwork();
					HashSet<BlockPos> participants = new HashSet<>();
					
					NBTTagList list = compound.getTagList("participants", Constants.NBT.TAG_COMPOUND);
					for (int j = 0; j < list.tagCount(); j++) {
						NBTTagCompound tag = list.getCompoundTagAt(j);
						participants.add(NBTUtil.getPosFromTag(tag));
					}
					network.setParticipants(participants);
					
					networks.put(compound.getInteger("id"), network);
					
				}
				instance.setNetworks(networks);
			}
            
        }, EnergyNetworkList::new);
	}
}
