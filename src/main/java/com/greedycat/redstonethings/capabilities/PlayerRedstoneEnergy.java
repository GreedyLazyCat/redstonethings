package com.greedycat.redstonethings.capabilities;

public class PlayerRedstoneEnergy {
	protected int energy;
	protected int max_energy_stored;
	protected int level = 1;
	
	public PlayerRedstoneEnergy() {
		this(500, 1000);
	}
	
	public int energyPercent() {
		return energy / (max_energy_stored / 100);
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
		
	public void increaseLevel() {
		level++;
		if(level > 10) {
			level = 10;
		}
		setMaxEnergyStored(1000 * level);
	}
	
	public void decreaseLevel() {
		level--;
		if(level < 1) {
			level = 1;
		}
		setMaxEnergyStored(1000 * level);
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
	
	public void setLevel(int level) {
		this.level = level;
	}
	public int getLevel() {
		return level;
	}
	
	public void onChages(){
		
	}
}
