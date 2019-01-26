package com.greedycat.redstonethings.slots;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RedstoneSlot extends Slot{

	public RedstoneSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		// TODO Auto-generated method stub
		if(stack!=null && stack.getItem() == Items.REDSTONE) {
			return true;
		}
		return false;
	}
	
}
