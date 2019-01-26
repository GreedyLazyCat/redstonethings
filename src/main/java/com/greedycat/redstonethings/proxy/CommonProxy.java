package com.greedycat.redstonethings.proxy;

import com.google.common.eventbus.Subscribe;
import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.gui.GuiHandler;
import com.greedycat.redstonethings.network.RedBetterEnchMessage;
import com.greedycat.redstonethings.network.RedForgeMessage;
import com.greedycat.redstonethings.util.RegBlocks;
import com.greedycat.redstonethings.util.RegCrafts;
import com.greedycat.redstonethings.util.RegEvents;
import com.greedycat.redstonethings.util.RegItems;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import scala.xml.dtd.impl.Base;

public class CommonProxy {
	
	private int messageId = 0;
	
	public static CreativeTabs redstone_things_tab = new CreativeTabs(CreativeTabs.getNextID(), "Redstone Things") {
		
		@Override
		public ItemStack getTabIconItem() {
			// TODO Auto-generated method stub
			return new ItemStack(RegBlocks.redForge);
		}
	};
	
    public void preInit(FMLPreInitializationEvent event) {
        RegItems.register();
    	RegBlocks.register();
    	RegEvents.Server();
    	EnergyStorageCapability.register();
    	EnergyGeneratorCapability.register();
    	NetworkRegistry.INSTANCE.registerGuiHandler(BaseClass.INSTANCE, new GuiHandler());
    	
    	BaseClass.NETWORK.registerMessage(RedForgeMessage.Handler.class, RedForgeMessage.class, messageId++, Side.CLIENT);
    	BaseClass.NETWORK.registerMessage(RedBetterEnchMessage.EnchHandler.class, RedBetterEnchMessage.class, messageId++, Side.CLIENT);
    }
    public void init(FMLInitializationEvent event) {
    	RegCrafts.recipesRegister();
    }
    public void postInit(FMLPostInitializationEvent event) {}
	
}
