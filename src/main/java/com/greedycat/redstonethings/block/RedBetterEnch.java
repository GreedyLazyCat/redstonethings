package com.greedycat.redstonethings.block;

import com.greedycat.redstonethings.proxy.CommonProxy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;

public class RedBetterEnch extends Block{

	public RedBetterEnch() {
		super(Material.ANVIL);
		this.setRegistryName("redbetterench");
		this.setUnlocalizedName("redbetterench");
		this.setCreativeTab(CommonProxy.redstone_things_tab);
	}

}
