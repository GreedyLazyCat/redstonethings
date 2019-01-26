package com.greedycat.redstonethings.proxy;

import com.greedycat.redstonethings.util.RegBlocks;
import com.greedycat.redstonethings.util.RegEvents;
import com.greedycat.redstonethings.util.RegItems;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
    	RegBlocks.registerRender();
       	RegItems.registerRender();
       	RegEvents.Client();
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
