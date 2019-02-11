package com.greedycat.redstonethings.util.tile;

import com.greedycat.redstonethings.util.EnergyNetworkUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class NetworkParticipantTile extends TileEntity{
	
	private int networkId = -1;
	protected EnumFacing[] facing;
	
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
