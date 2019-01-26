package com.greedycat.redstonethings.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class RegEvents {
	public static void Client() {
		
	}
	public static void Server() {
		
	}
	
	public void register(Object event) {
		MinecraftForge.EVENT_BUS.register(event);
	}
	public void registerFML(Object event) {
		FMLCommonHandler.instance().bus().register(event);
	}
}
