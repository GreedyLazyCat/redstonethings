package com.greedycat.redstonethings.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class InventoryBase implements IInventory{
	private ItemStack[] items;
	private int stack_limit;
	private TileEntity tile;
	
	public InventoryBase(TileEntity tile, int size,int stack_limit) {
		this.items = new ItemStack[size];
		this.stack_limit = stack_limit;
		this.tile = tile;
	}
	
    public void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        items = new ItemStack[getSizeInventory()];
        System.out.println("called read");
        for (int i = 0; i < list.tagCount(); ++i) 
		{ 
        		NBTTagCompound comp = list.getCompoundTagAt(i); 
		        int j = comp.getByte("Slot") & 255;
		        if (j >= 0 && j < items.length)
		            {
		                items[j] = new ItemStack(comp);
		            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList list = new NBTTagList();
        
        for (int i = 0; i < items.length; ++i)
        {
            if (items[i] != null)
            {
            	System.out.println("called");
                NBTTagCompound comp = new NBTTagCompound();
                comp.setByte("Slot", (byte)i);
                items[i].writeToNBT(comp);
                list.appendTag(comp);
            }
            }
        nbt.setTag("Items", list);
        
     }
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return items.length;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		// TODO Auto-generated method stub
		return items[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		// TODO Auto-generated method stub
		if (items[slot] != null)
        {
            ItemStack itemstack;
            if (items[slot].getCount() == amount)
            {
                itemstack = items[slot];
                items[slot] = null;
                markDirty();
                return itemstack;
            }
            else
            {
                itemstack = items[slot].splitStack(amount);
                if (items[slot].getCount() == 0) items[slot] = null;
                markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		ItemStack stack = items[index];
		items[index] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		items[slot] = stack;
        if (stack != null && stack.getCount() > getInventoryStackLimit())
        {
            stack.setCount(getInventoryStackLimit());
        }

        markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return stack_limit;
	}

	@Override
	public void markDirty() {
		// TODO Auto-generated method stub
		tile.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
