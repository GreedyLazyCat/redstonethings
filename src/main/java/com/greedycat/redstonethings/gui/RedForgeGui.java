package com.greedycat.redstonethings.gui;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.container.RedForgeContainer;
import com.greedycat.redstonethings.tile.RedForgeTile;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class RedForgeGui extends GuiContainer{
	
	public static final ResourceLocation TEXTURE = new ResourceLocation(BaseClass.MODID, "textures/gui/redforge.png");
	public RedForgeTile tile;
	
	public RedForgeGui(RedForgeTile tile, EntityPlayer player) {
		super(new RedForgeContainer(tile, player));
		this.tile = tile;
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		fontRenderer.drawString(this.tile.getStoredEnergy()+"/"+this.tile.getMaxEnergyStored(), 60, 60, 4210752);
		
		System.out.println("MouseX: " + mouseX);
		System.out.println("MouseY: " + mouseY);
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(TEXTURE);
		int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        System.out.println("X: " + x);
        int energyPercent = this.tile.energyPercent();
        //System.out.println(this.te.storedEnergy);
        double widthToDraw = 160D/100D*energyPercent;
        

        drawTexturedModalRect(x, y, 0, 0, xSize, 166);
        drawTexturedModalRect(x+8, y+71, 0, 167, (int)widthToDraw, 8);
	}

}
