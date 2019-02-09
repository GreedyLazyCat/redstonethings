package com.greedycat.redstonethings.gui;

import com.greedycat.redstonethings.capabilities.PlayerRedstoneEnergyCapability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerEnergyGui extends Gui{
	String text = "Hello world!";
	 
    public PlayerEnergyGui(Minecraft mc)
    {
        ScaledResolution scaled = new ScaledResolution(mc);
        int width = scaled.getScaledWidth();
        int height = scaled.getScaledHeight();
        EntityPlayer player = mc.player;
		if(player.hasCapability(PlayerRedstoneEnergyCapability.PLAYER_ENERGY, null)) {
			drawCenteredString(mc.fontRenderer, text, width / 2, (height / 2) - 4, Integer.parseInt("FFAA00", 16));
		}
        
    }
}
