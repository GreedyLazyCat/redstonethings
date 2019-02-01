package com.greedycat.redstonethings.block;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;
import com.greedycat.redstonethings.proxy.CommonProxy;
import com.greedycat.redstonethings.tile.BlockTileEntity;
import com.greedycat.redstonethings.tile.NetworkParticipantTile;
import com.greedycat.redstonethings.tile.RedBetterEnchTile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedBetterEnch extends BlockTileEntity<RedBetterEnchTile>{
	
	public RedBetterEnch() {
		super(Material.ANVIL);
		this.setRegistryName("redbetterench");
		this.setUnlocalizedName("redbetterench");
		this.setCreativeTab(CommonProxy.redstone_things_tab);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		if(playerIn.getHeldItem(hand) != ItemStack.EMPTY && playerIn.getHeldItem(hand).getItem() == Items.APPLE) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);//получаем тайл
			if(tileEntity != null && tileEntity instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
				System.out.println("Participant id is:" + participant.getNetworkId());
			}
			if(worldIn.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
				EnergyNetworkList list = worldIn.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
				System.out.println("Count of networks:" + list.getNetworks().size());
			}
		}
		playerIn.openGui(BaseClass.INSTANCE, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public Class<RedBetterEnchTile> getTileEntityClass() {
		// TODO Auto-generated method stub
		return RedBetterEnchTile.class;
	}

	@Override
	public RedBetterEnchTile createTileEntity(World world, IBlockState blockState) {
		// TODO Auto-generated method stub
		return new RedBetterEnchTile();
	}

}
