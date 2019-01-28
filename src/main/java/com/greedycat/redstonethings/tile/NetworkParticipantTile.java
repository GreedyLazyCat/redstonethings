package com.greedycat.redstonethings.tile;

import com.greedycat.redstonethings.util.EnergyNetworkUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class NetworkParticipantTile extends TileEntity{
	
	private int networkId = -1;
	
	@Override
	public void onLoad() {
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
