package com.greedycat.redstonethings.tile;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;

public class GeneratorTile extends NetworkParticipantTile{
	public ArrayList<BlockPos> storages = new ArrayList<>();
	
	public void setStorages(ArrayList<BlockPos> storages) {
		this.storages = storages;
		markDirty();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for(int i = 0; i < storages.size(); i++) {
			NBTTagCompound tag = NBTUtil.createPosTag(storages.get(i));
			list.appendTag(tag);
		}
		
		compound.setTag("storages", list);
		
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList list = compound.getTagList("storages", Constants.NBT.TAG_COMPOUND);
		storages.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			storages.add(NBTUtil.getPosFromTag(list.getCompoundTagAt(i)));
		}
		super.readFromNBT(compound);
	}
}
