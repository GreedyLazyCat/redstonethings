package com.greedycat.redstonethings.gui;

import com.greedycat.redstonethings.container.RedBetterEnchContainer;
import com.greedycat.redstonethings.container.RedForgeContainer;
import com.greedycat.redstonethings.tile.RedBetterEnchTile;
import com.greedycat.redstonethings.tile.RedForgeTile;
import com.greedycat.redstonethings.util.References;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import scala.reflect.internal.Trees.New;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		
		if(ID == References.RedForgeGui) return new RedForgeContainer((RedForgeTile)world.getTileEntity(new BlockPos(x,y,z)), player);
		if(ID == 1) return new RedBetterEnchContainer((RedBetterEnchTile)world.getTileEntity(new BlockPos(x,y,z)), player);
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		
		if(ID == References.RedForgeGui) return new RedForgeGui((RedForgeTile)world.getTileEntity(new BlockPos(x,y,z)), player);
		if(ID == 1) return new RedBetterEnchGui((RedBetterEnchTile)world.getTileEntity(new BlockPos(x,y,z)), player);
		return null;
	}

	


}
