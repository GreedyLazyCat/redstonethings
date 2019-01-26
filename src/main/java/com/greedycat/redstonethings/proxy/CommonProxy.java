package com.greedycat.redstonethings.proxy;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.gui.GuiHandler;
import com.greedycat.redstonethings.network.RedForgeMessage;
import com.greedycat.redstonethings.util.RegBlocks;
import com.greedycat.redstonethings.util.RegCrafts;
import com.greedycat.redstonethings.util.RegEvents;
import com.greedycat.redstonethings.util.RegItems;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import scala.xml.dtd.impl.Base;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        RegItems.register();
    	RegBlocks.register();
    	RegEvents.Server();
    	EnergyStorageCapability.register();
    	EnergyGeneratorCapability.register();
    	NetworkRegistry.INSTANCE.registerGuiHandler(BaseClass.INSTANCE, new GuiHandler());
    	
    	BaseClass.NETWORK.registerMessage(RedForgeMessage.Handler.class, RedForgeMessage.class, 0, Side.CLIENT);
    }
    public void init(FMLInitializationEvent event) {
    	RegCrafts.recipesRegister();
    }
    public void postInit(FMLPostInitializationEvent event) {}
}
