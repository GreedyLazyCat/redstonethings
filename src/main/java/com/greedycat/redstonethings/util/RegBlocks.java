package com.greedycat.redstonethings.util;

import com.greedycat.redstonethings.block.BasicBlock;
import com.greedycat.redstonethings.block.RedForge;
import com.greedycat.redstonethings.block.TestStorage;
import com.greedycat.redstonethings.block.Wire;
import com.greedycat.redstonethings.tile.RedForgeTile;
import com.greedycat.redstonethings.tile.TestStorageTile;

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
	
	public static BasicBlock block;
	public static RedForge redForge;
	public static Wire wire;
	public static TestStorage storage;
	
	public static void register() {
		block = new BasicBlock(Material.CLOTH, "testblock", CreativeTabs.COMBAT);
		redForge = new RedForge();
		wire = new Wire();
		storage = new TestStorage();
		
		registerBlock(block);
		registerBlock(redForge);
		registerBlock(wire);
		registerBlock(storage);
		
		registerTile(storage, TestStorageTile.class);
		registerTile(redForge, RedForgeTile.class);
	}
	
	public static void registerTile(Block block,Class tile ) {
		GameRegistry.registerTileEntity(tile, block.getRegistryName().toString());
	}
	
	public static void registerRender() {

		registerBlockRender(block);
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