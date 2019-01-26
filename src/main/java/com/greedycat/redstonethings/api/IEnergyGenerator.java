package com.greedycat.redstonethings.api;

public interface IEnergyGenerator {
	
	void setOutput(int output);
	
	int getOutput();
	
	/**
	 * Returns the maximum amount of energy that can be stored.
	 */
	int getMaxEnergyStored();
	
	int getEnergyStored();
	
	void setEnergyStored(int energy);

	void setMaxEnergyStored(int energy);
}
