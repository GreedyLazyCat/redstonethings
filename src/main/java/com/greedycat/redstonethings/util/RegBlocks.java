package com.greedycat.redstonethings.util;

import com.greedycat.redstonethings.block.BlackHoleBlock;
import com.greedycat.redstonethings.block.PlayerCharger;
import com.greedycat.redstonethings.block.RedBetterEnch;
import com.greedycat.redstonethings.block.RedForge;
import com.greedycat.redstonethings.block.Wire;
import com.greedycat.redstonethings.tile.BlackHoleTile;
import com.greedycat.redstonethings.tile.RedBetterEnchTile;
import com.greedycat.redstonethings.tile.RedForgeTile;
import com.greedycat.redstonethings.tile.WireTile;

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
	public static RedBetterEnch better_ench;
	public static BlackHoleBlock blackHoleBlock;
	public static PlayerCharger playerCharger;
	
	public static void register() {
		redForge = new RedForge();
		wire = new Wire();
		better_ench = new RedBetterEnch();
		blackHoleBlock = new BlackHoleBlock();
		playerCharger = new PlayerCharger();
		
		registerBlock(redForge);
		registerBlock(wire);
		registerBlock(better_ench);
		ForgeRegistries.BLOCKS.register(blackHoleBlock);
		
		registerTile(blackHoleBlock, BlackHoleTile.class);
		registerTile(wire, WireTile.class);
		registerTile(better_ench, RedBetterEnchTile.class);
		registerTile(redForge, RedForgeTile.class);
	}
	
	public static void registerTile(Block block,Class tile ) {
		GameRegistry.registerTileEntity(tile, block.getRegistryName().toString());
	}
	
	public static void registerRender() {
		registerBlockRender(redForge);
		registerBlockRender(wire);
	}
	public static void registerBlock(Block block) {
		ForgeRegistries.BLOCKS.register(block);
		ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}
	public static void registerBlockRender(Block block) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
}