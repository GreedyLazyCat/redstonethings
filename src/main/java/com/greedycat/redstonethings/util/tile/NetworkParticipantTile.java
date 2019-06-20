package com.greedycat.redstonethings.util.tile;

import com.greedycat.redstonethings.util.EnergyNetworkUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class NetworkParticipantTile extends TileEntity{
	
	private int networkId = -1;
	protected EnumFacing[] facing;
	
	
	/**
	 * 
	 * @param capability
	 * @param facing
	 * @param mode если true то facing учитываеться, false - не учитываеться 
	 * @return
	 */
	public boolean hasCapabilityAdvanced(Capability<?> capability, EnumFacing facing, boolean mode) {
		return false;
	}
	
	public boolean canConnectTo(EnumFacing face) {
		if(facing != null) {
			for (int i = 0; i < facing.length; i++) {
				if (facing[i] == face) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return this.hasCapabilityAdvanced(capability, facing, true);
	}
	
	public void setFacingForConnection(EnumFacing[] facing) {
		this.facing = facing;
	}
	
	public EnumFacing[] getFacingForConnection() {
		return facing;
	}
	
	@Override
	public void onLoad() {
		facing = EnumFacing.VALUES;
		EnergyNetworkUtil.checkAround(getWorld(), getPos());
		super.onLoad();
	}
	
	public boolean hasNetworkId() {
		if(networkId == -1) {
			return false;
		}
		return true;
	}
	
	public void setNetworkId(int networkId) {
		this.networkId = networkId;
		markDirty();
	}
	
	public int getNetworkId() {
		return networkId;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.networkId = compound.getInteger("networkId");
		super.readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("networkId", networkId);
		return super.writeToNBT(compound);
	}
	
}
