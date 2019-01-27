package com.greedycat.redstonethings.capabilities;

import com.greedycat.redstonethings.api.IEnergyGenerator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class EnergyGenerator implements IEnergyGenerator, INBTSerializable<NBTTagCompound>{
	
	protected int energy;
	protected int max_energy_stored;
	protected int output;
	
	public EnergyGenerator() {
		this(0, 10000);
	}
	
	public EnergyGenerator(int energy, int max) {
		this.energy = energy;
		this.max_energy_stored = max;
	}
	
	@Override
	public int getOutput() {
		int energyExtracted = Math.min(energy,  output);
		energy -= energyExtracted;
		onChages();
		return energyExtracted;
	}

	@Override
	public int getMaxEnergyStored() {
		return max_energy_stored;
	}

	@Override
	public int getEnergyStored() {
		return energy;
	}

	@Override
	public void setEnergyStored(int energy) {
		this.energy = energy;
	}

	@Override
	public void setMaxEnergyStored(int energy) {
		this.max_energy_stored = energy;
	}

	@Override
	public void setOutput(int output) {
		this.output = output;
	}
	
	public void onChages(){
		
	}

	@Override
    public NBTTagCompound serializeNBT()
    {

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("energy", getEnergyStored());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        setEnergyStored(nbt.getInteger("energy"));
    }

}
