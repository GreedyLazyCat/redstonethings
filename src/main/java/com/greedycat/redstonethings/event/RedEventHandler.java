package com.greedycat.redstonethings.event;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;
import com.greedycat.redstonethings.capabilities.PlayerRedstoneEnergy;
import com.greedycat.redstonethings.capabilities.PlayerRedstoneEnergyCapability;
import com.greedycat.redstonethings.gui.PlayerEnergyGui;
import com.jcraft.jorbis.Block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BaseClass.MODID)
public class RedEventHandler {
	
	private static final ResourceLocation WORLD_CAP = new ResourceLocation(BaseClass.MODID, "EnergyNetworkList");
	private static final ResourceLocation PLAYER_CAP = new ResourceLocation(BaseClass.MODID, "PlayerRedstoneEnergy");
	private static ResourceLocation GUI = new ResourceLocation(BaseClass.MODID, "textures/gui/energy_bar_test.png");
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<World> event) {
		event.addCapability(WORLD_CAP, new ICapabilitySerializable<NBTTagList>() {
			
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

			@Override
			public NBTTagList serializeNBT() {
				return (NBTTagList) EnergyNetworkListCapability.NETWORK_LIST.getStorage()
				.writeNBT(EnergyNetworkListCapability.NETWORK_LIST, energyNetworkList, null);
			}

			@Override
			public void deserializeNBT(NBTTagList nbt) {
				EnergyNetworkListCapability.NETWORK_LIST.getStorage()
				.readNBT(EnergyNetworkListCapability.NETWORK_LIST, energyNetworkList, null, nbt);
			}
		});
	}
	@SubscribeEvent
	public static void attachCapabilityToPlayer(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof EntityPlayer)) return; 
		event.addCapability(PLAYER_CAP, new ICapabilitySerializable<NBTTagCompound>() {
			
			private final PlayerRedstoneEnergy playerRedstoneEnergy = new PlayerRedstoneEnergy();
			
			@Override
			public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
				// TODO Auto-generated method stub
				return capability == PlayerRedstoneEnergyCapability.PLAYER_ENERGY;
			}

			@Override
			public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
				if (capability == PlayerRedstoneEnergyCapability.PLAYER_ENERGY) {
					return (T) this.playerRedstoneEnergy;
				}
				return null;
			}

			@Override
			public NBTTagCompound serializeNBT() {
				return (NBTTagCompound) PlayerRedstoneEnergyCapability.PLAYER_ENERGY.getStorage()
				.writeNBT(PlayerRedstoneEnergyCapability.PLAYER_ENERGY, playerRedstoneEnergy, null);
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt) {
				PlayerRedstoneEnergyCapability.PLAYER_ENERGY.getStorage()
				.readNBT(PlayerRedstoneEnergyCapability.PLAYER_ENERGY, playerRedstoneEnergy, null, nbt);
				
				
			}
		});
	}
	@SubscribeEvent
	public static void renderGui(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.ALL) {
			if(Minecraft.getMinecraft().player.hasCapability(PlayerRedstoneEnergyCapability.PLAYER_ENERGY, null)) {
				
				PlayerRedstoneEnergy energy = Minecraft.getMinecraft().player.getCapability(PlayerRedstoneEnergyCapability.PLAYER_ENERGY, null);
				int energyPercent = energy.energyPercent();
				float percent = (132f / 100f) * energyPercent;
				Minecraft.getMinecraft().getTextureManager().bindTexture(GUI);
				Minecraft.getMinecraft().ingameGUI
				.drawTexturedModalRect(event.getResolution().getScaledWidth() / 2 - 66, 0, 0, 0, 132, 12);
				Minecraft.getMinecraft().ingameGUI
				.drawTexturedModalRect(event.getResolution().getScaledWidth() / 2 - 66, 0, 0, 13, Math.round(percent), 12);
				if(Minecraft.getMinecraft().player.isSneaking())
					Minecraft.getMinecraft().ingameGUI
					.drawCenteredString(Minecraft.getMinecraft().fontRenderer, energy.getEnergyStored() + "/" + energy.getMaxEnergyStored(), event.getResolution().getScaledWidth() / 2, 0, Integer.parseInt("FFAA00", 16));
			}
				//Minecraft.getMinecraft().ingameGUI.drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Test", event.getResolution().getScaledWidth() / 2, (event.getResolution().getScaledHeight() / 2) - 4, Integer.parseInt("FFAA00", 16));
		}
	}
}
