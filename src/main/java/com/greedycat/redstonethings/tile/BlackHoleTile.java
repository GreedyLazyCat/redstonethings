package com.greedycat.redstonethings.tile;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class BlackHoleTile extends TileEntity implements ITickable{
	
	private int timer;
	private IBlockState block = Blocks.GRASS.getDefaultState();
	
	@Override
	public void onLoad() {
		super.onLoad();
	}
	
	public void setBlock(IBlockState block) {
		this.block = block;
	}
	
	@Override
	public void update() {
		timer++;
		if(timer > 20*3) {
			this.getWorld().setBlockState(this.getPos(), block);
			timer = 0;
		}
	}

}
