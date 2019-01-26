package com.greedycat.redstonethings.container;

import com.greedycat.redstonethings.inventory.InventoryBase;
import com.greedycat.redstonethings.tile.RedBetterEnchTile;
import com.greedycat.redstonethings.tile.RedForgeTile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RedBetterEnchContainer extends Container{

	private RedBetterEnchTile tile;
	private int last_energy;
    private int slotID = 0;
	
	public RedBetterEnchContainer(RedBetterEnchTile tile, EntityPlayer player) {
		
		this.tile = tile;
		
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		this.addSlotToContainer(new SlotItemHandler(handler, slotID++, 7, 7) {
			@Override
            public void onSlotChanged() {
                tile.markDirty();
            }
		});
		
		//їнвентарь
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        //Тотбар
        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return true;
	}

}
