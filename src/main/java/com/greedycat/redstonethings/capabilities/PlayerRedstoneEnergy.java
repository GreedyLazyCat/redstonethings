package com.greedycat.redstonethings.capabilities;

public class PlayerRedstoneEnergy {
	protected int energy;
	protected int max_energy_stored;
	
	public PlayerRedstoneEnergy() {
		this(0, 10000);
	}
	
	public PlayerRedstoneEnergy(int energy, int max) {
		this.energy = energy;
		this.max_energy_stored = max;
	}
	
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energyReceived = Math.min(max_energy_stored - energy,maxReceive);

		if (!simulate) {
			energy += energyReceived;
		}
		onChages();
		return energyReceived;
	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		int energyExtracted = Math.min(energy,  maxExtract);

		if (!simulate) {
			energy -= energyExtracted;
		}
		onChages();
		return energyExtracted;
	}

	public int getEnergyStored() {
		return energy;
	}

	public int getMaxEnergyStored() {
		return max_energy_stored;
	}
	
	public void setMaxEnergyStored(int energy) {
		max_energy_stored = energy;
	}

	public void setEnergyStored(int energy) {
		this.energy = energy;
		
	}

	public void onChages(){
		
	}
}
