package com.greedycat.redstonethings.event;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BaseClass.MODID)
public class RedEventHandler {
	
	private static final ResourceLocation WORLD_CAP = new ResourceLocation(BaseClass.MODID, "EnergyNetworkList");
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<World> event) {
		event.addCapability(WORLD_CAP, new ICapabilityProvider() {
			
			private final EnergyNetworkList energyNetworkList = new EnergyNetworkList();
			
			@Override
			public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
				return capability == EnergyNetworkListCapability.NETWORK_LIST;
			}

			@Override
			public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
				if (capability == EnergyNetworkListCapability.NETWORK_LIST) {
					return (T) this.energyNetworkList;
				}
				return null;
			}
		});
	}
	
	@SubscribeEvent
	public static void pickUpItem(EntityItemPickupEvent event) {
		System.out.println("Item picked up!");
	}
}
