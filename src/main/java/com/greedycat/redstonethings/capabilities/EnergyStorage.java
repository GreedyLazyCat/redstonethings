package com.greedycat.redstonethings.capabilities;

import com.greedycat.redstonethings.api.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage{
	
	protected int energy;
	protected int max_energy_stored;
	
	public EnergyStorage() {
		this(0, 10000);
	}
	
	public EnergyStorage(int energy, int max) {
		this.energy = energy;
		this.max_energy_stored = max;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		// TODO Auto-generated method stub
		int energyReceived = Math.min(max_energy_stored - energy,maxReceive);

		if (!simulate) {
			energy += energyReceived;
		}
		onChages();
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		// TODO Auto-generated method stub
		int energyExtracted = Math.min(energy,  maxExtract);

		if (!simulate) {
			energy -= energyExtracted;
		}
		onChages();
		return energyExtracted;
	}

	@Override
	public int getEnergyStored() {
		// TODO Auto-generated method stub
		return energy;
	}

	@Override
	public int getMaxEnergyStored() {
		// TODO Auto-generated method stub
		return max_energy_stored;
	}
	
	@Override
	public void setMaxEnergyStored(int energy) {
		// TODO Auto-generated method stub
		max_energy_stored = energy;
	}
	
	@Override
	public void setEnergyStored(int energy) {
		this.energy = energy;
		
	}

	public void onChages(){
		
	}

}
