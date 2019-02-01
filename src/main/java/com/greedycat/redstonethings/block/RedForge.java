package com.greedycat.redstonethings.block;

import java.util.ArrayList;
import java.util.Random;

import com.greedycat.redstonethings.BaseClass;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;
import com.greedycat.redstonethings.inventory.InventoryBase;
import com.greedycat.redstonethings.proxy.CommonProxy;
import com.greedycat.redstonethings.tile.BlockTileEntity;
import com.greedycat.redstonethings.tile.NetworkParticipantTile;
import com.greedycat.redstonethings.tile.RedForgeTile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedForge extends BlockTileEntity<RedForgeTile>{
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing",EnumFacing.Plane.HORIZONTAL);

	private final Random rand = new Random();
	
	public RedForge() {
		super(Material.ANVIL);
		this.setRegistryName("redforge");
		this.setUnlocalizedName("redforge");
		this.setCreativeTab(CommonProxy.redstone_things_tab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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
		else {
			playerIn.openGui(BaseClass.INSTANCE, 0, worldIn, pos.getX(),pos.getY(),pos.getZ());
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		// TODO Auto-generated method stub
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(FACING, placer.getHorizontalFacing());
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub
		super.breakBlock(worldIn, pos, state);/*
		if (worldIn.isRemote) return;

        ArrayList drops = new ArrayList();

        TileEntity teRaw = worldIn.getTileEntity(pos);

        if (teRaw != null && teRaw instanceof RedForgeTile)
        {
        	RedForgeTile tile = (RedForgeTile) teRaw;

            for (int i = 0; i < te.getSizeInventory(); i++)
            {
                ItemStack stack = te.getStackInSlot(i);

                if (stack != null) drops.add(stack.copy());
            }
        }

        for (int i = 0;i < drops.size();i++)
        {
            EntityItem item = new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (ItemStack) drops.get(i));
            item.setVelocity((rand.nextDouble() - 0.5) * 0.25, rand.nextDouble() * 0.5 * 0.25, (rand.nextDouble() - 0.5) * 0.25);
            worldIn.spawnEntity(item);
        }*/
	}

	
	
	@Override
	public boolean isFullCube(IBlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
			
	@Override
	protected BlockStateContainer createBlockState() {
		 return new BlockStateContainer(this, new IProperty[] { FACING});
	}
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}
	@Override
	public int getMetaFromState(IBlockState state) {
		
		EnumFacing facing = state.getValue(FACING);
		int meta = ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		
		return meta;
	}



	@Override
	public Class<RedForgeTile> getTileEntityClass() {
		// TODO Auto-generated method stub
		return RedForgeTile.class;
	}



	@Override
	public RedForgeTile createTileEntity(World world, IBlockState blockState) {
		// TODO Auto-generated method stub
		return new RedForgeTile();
	}
}
