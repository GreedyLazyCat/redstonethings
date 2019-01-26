package com.greedycat.redstonethings.container;

import org.jline.terminal.impl.jna.freebsd.CLibrary.termios;

import com.greedycat.redstonethings.capabilities.EnergyStorage;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.inventory.InventoryBase;
import com.greedycat.redstonethings.slots.CrystalSlot;
import com.greedycat.redstonethings.slots.RedstoneSlot;
import com.greedycat.redstonethings.tile.RedForgeTile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RedForgeContainer extends Container{
	
	private InventoryBase inv;
	private RedForgeTile tile;
	private int last_energy;
    private int slotID = 0;
	
	public RedForgeContainer(RedForgeTile tile, EntityPlayer player) {
		this.tile = tile;
		IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		this.addSlotToContainer(new SlotItemHandler(handler, slotID++, 44 + 1 * 18, 17 + 1 * 18) {
			@Override
            public void onSlotChanged() {
                tile.markDirty();
            }
		});
		this.addSlotToContainer(new SlotItemHandler(handler, slotID++, 44 + 3 * 18, 17 + 1 * 18) {
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
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < tile.SIZE) {
                if (!this.mergeItemStack(itemstack1, tile.SIZE, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, tile.SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
	}

	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return true;
	}

}
