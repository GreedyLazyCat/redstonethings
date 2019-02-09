package com.greedycat.redstonethings.util;

import com.greedycat.redstonethings.items.BlackHole;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RegItems {
	public static BlackHole blackHole;
	
    public static void register() {
    	blackHole = new BlackHole();
    	
    	registerItems(blackHole);
    }
    public static void registerRender() {
    	registerItemsRender(blackHole);
    }
    private static void registerItems(Item item) {
        ForgeRegistries.ITEMS.register(item);
    }
    private static void registerItemsRender(Item item) {
    	Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
