package com.greedycat.redstonethings.util;

import com.greedycat.redstonethings.block.RedForge;
import com.greedycat.redstonethings.block.Wire;
import com.greedycat.redstonethings.tile.RedForgeTile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RegBlocks {
	
	public static RedForge redForge;
	public static Wire wire;
	
	public static void register() {
		redForge = new RedForge();
		wire = new Wire();
		
		
		registerBlock(redForge);
		registerBlock(wire);
		registerTile(redForge, RedForgeTile.class);
	}
	
	public static void registerTile(Block block,Class tile ) {
		GameRegistry.registerTileEntity(tile, block.getRegistryName().toString());
	}
	
	public static void registerRender() {
		registerBlockRender(redForge);
	}
	public static void registerBlock(Block block) {
		ForgeRegistries.BLOCKS.register(block);
		ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}
	public static void registerBlockRender(Block block) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
}