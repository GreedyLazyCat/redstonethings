package com.greedycat.redstonethings;

import com.greedycat.redstonethings.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = BaseClass.MODID, name = "redstonethings",version = BaseClass.VERSION)
public class BaseClass {
	public static final String MODID = "redstonethings";
	public static final String VERSION = "0.1";
	
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	
	@Mod.Instance(MODID)
	public static BaseClass INSTANCE;
    
	//here you must specify the path to the folder where the proxy files are located
   	@SidedProxy(clientSide = "com.greedycat.redstonethings.proxy.ClientProxy", serverSide = "com.greedycat.redstonethings.proxy.CommonProxy")
   	public static CommonProxy proxy;
	
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
