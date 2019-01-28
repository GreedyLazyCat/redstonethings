package com.greedycat.redstonethings.block;

import java.security.PublicKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyNetwork;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.proxy.CommonProxy;
import com.greedycat.redstonethings.tile.BlockTileEntity;
import com.greedycat.redstonethings.tile.GeneratorTile;
import com.greedycat.redstonethings.tile.WireTile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import scala.reflect.internal.Trees.If;
import scala.reflect.internal.Trees.This;

public class Wire extends Block{
	//������ �������� �������� �� ��, ��������� �� � ���� ������� ������ ��� ������ �������� ����(true/false)
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool EAST = PropertyBool.create("east");
	
	public Wire() {
		super(Material.CIRCUITS);
		this.setRegistryName("wire");
		this.setUnlocalizedName("wire");
		this.setCreativeTab(CommonProxy.redstone_things_tab);
	}
	
	public void buildNetworkNew(World world, BlockPos start) {
		HashSet<BlockPos> checked = new HashSet<>(); //������ ����������� ������
		HashSet<BlockPos> participants = new HashSet<>();//������ �����������, ������� ����� ������� � ����
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//�������, ��� ����������� ���������� ��������� ������ � ������.
		
		queue.offer(start);//��������� ��������� ������� � �������
		checked.add(start);//� ����� ��������� � �����������
		while (!queue.isEmpty()) {//��������� ���� ������� �� �����
			BlockPos nPos = queue.poll();//���� ����� ���������� ������ �� ������ ������� � ����� ��� �������.
			//��������� �� ��������� �� ��������� �������.
			TileEntity tile = world.getTileEntity(nPos);
			if(tile != null) {
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {// � ���� ���� ���� �� ��������� � ���������
					participants.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//������ ����������� �� ���� ��������� "������������"(�� ���� ��� ����� ���������
				BlockPos child = nPos.offset(face);//��� ������� ���������� ������� �� ������� � ������ �����������
				TileEntity tileEntity = world.getTileEntity(child);//�������� ����
				if(tileEntity != null) {
					//���������, ��� ���, ��������� ��� ���������
					if(tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
						participants.add(child); // � ��������� �� � ��������������� ������
					}
					if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
						participants.add(child);// � ��������� �� � ��������������� ������
					}
				}
				//���� � ������ ����������� ���� ���� ������� � ���� �� ���� ������� - ��� ������, ��������� � ������
				//����������� + � �������, ����� � ���� ������� ��������� ��� ������ �����
				if(!checked.contains(child) && world.getBlockState(child).getBlock() instanceof Wire) {
					checked.add(child);
					queue.addLast(child);//��������� � ��� �������
				}
			}
		}
		
		EnergyNetwork network = new EnergyNetwork();
		network.setParticipants(participants);
		if(world.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
			EnergyNetworkList list = world.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
			list.addNetwork(network);
		}
	}
	
	//����� "��������" ����, �������� � ���� ��� � ��������� �������
	public ArrayList<BlockPos> buildNetwork(IBlockAccess worldIn, BlockPos start) {
		HashSet<BlockPos> checked = new HashSet<>(); //������ ����������� ������
		ArrayList<BlockPos> generators = new ArrayList<>();//������ �����������, ������� ����� ������� � ����
		ArrayList<BlockPos> storages = new ArrayList<>();//������ ��������, ������� ����� ������� � ����
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//�������, ��� ����������� ���������� ��������� ������ � ������.
		
		queue.offer(start);//��������� ��������� ������� � �������
		checked.add(start);//� ����� ��������� � �����������
		while (!queue.isEmpty()) {//��������� ���� ������� �� �����
			BlockPos nPos = queue.poll();//���� ����� ���������� ������ �� ������ ������� � ����� ��� �������.
			//��������� �� ��������� �� ��������� �������.
			TileEntity tile = worldIn.getTileEntity(nPos);
			if(tile != null) {
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {// � ���� ���� ���� �� ��������� � ���������
					generators.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//������ ����������� �� ���� ��������� "������������"(�� ���� ��� ����� ���������
				BlockPos child = nPos.offset(face);//��� ������� ���������� ������� �� ������� � ������ �����������
				TileEntity tileEntity = worldIn.getTileEntity(child);//�������� ����
				if(tileEntity != null) {
					//���������, ��� ���, ��������� ��� ���������
					if(tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
						generators.add(child); // � ��������� �� � ��������������� ������
					}
					if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
						storages.add(child);// � ��������� �� � ��������������� ������
					}
				}
				//���� � ������ ����������� ���� ���� ������� � ���� �� ���� ������� - ��� ������, ��������� � ������
				//����������� + � �������, ����� � ���� ������� ��������� ��� ������ �����
				if(!checked.contains(child) && worldIn.getBlockState(child).getBlock() instanceof Wire) {
					checked.add(child);
					queue.addLast(child);//��������� � ��� �������
				}
			}	
		}
		
		if(!generators.isEmpty()) {//���� ���� � ����  ����������
			for (int i = 0; i < generators.size(); i++) {//����������� �� ��� ������
				BlockPos gPos = generators.get(i);
				TileEntity tileEntity = worldIn.getTileEntity(gPos);
				if(tileEntity != null && tileEntity instanceof GeneratorTile) {
					//���������, ��� ��� ��������� ���� ����� GeneratorTile, ����� ��� ��������� ���� ���� �����
					GeneratorTile generator = (GeneratorTile) tileEntity;
					generator.setStorages(storages);//������������� � ���� ��������� ������ ��������� � ���� ��������
				}
			}
		}
		return generators;
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		// ����� ���� �������, ��� ����� ���� �������� ������ ���� ���� �������� ����������� ����.
		// ���� ������ ������ �� ����� ��������� ����������
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockPos p = pos.offset(face);
			TileEntity tileEntity = worldIn.getTileEntity(p);
			if(tileEntity != null && tileEntity instanceof GeneratorTile) {
				//���� ���� ��� ���������, �� ������������� ���� � ��� �������, ����
				//��������� ����� ���� ��������� ����� ����� �������
				buildNetwork(worldIn, p);
			}
			if(worldIn.getBlockState(p).getBlock() instanceof Wire) {
				//���������� ������� � ������ ����������� ����
				Wire wire = (Wire) worldIn.getBlockState(p).getBlock();
				wire.buildNetwork(worldIn, p);
			}
		}
		super.onBlockDestroyedByPlayer(worldIn, pos, state);
	}

	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		//����� ������ ���������� ��� ����� "��������� ����"
		this.buildNetwork(worldIn, pos);
		super.onBlockAdded(worldIn, pos, state);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// ��� ���� ��� ����������� ��������� ���������/���������� ���� ��� ���� ���������� �����
		this.buildNetwork(worldIn, pos);
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}
	//��� ����� ����� ��� �������� ����� �� �� ���������� "�����������" � ������ ������
	public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing){
		BlockPos child = pos.offset(facing);
		TileEntity tileEntity = world.getTileEntity(child);
		if(tileEntity != null) {
			if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
				//���� ��������� - ��
				return true;
			}
			if(tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
				//���� ��������� - ��
				return true;
			}
		}
		if(world.getBlockState(child).getBlock() instanceof Wire) {
			//���� ������ - ��
			return true;
		}
		return false;
	}
	
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		//� ����������� �� ����, ����� ������� ����������, ���������� ������������ ��������
		if (canConnectTo(world, pos, EnumFacing.UP)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes,
					new AxisAlignedBB(0.377, 0, 0.377, 0.623D, 0.623D, 0.623D));
		}
		if (canConnectTo(world, pos, EnumFacing.UP)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes,
					new AxisAlignedBB(0.377, 0.377, 0.377, 0.623D, 1, 0.623D));
		}
		if (canConnectTo(world, pos, EnumFacing.NORTH)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes,
					new AxisAlignedBB(0.377, 0.377, 0, 0.623D, 0.623D, 0.623D));
		}
		if (canConnectTo(world, pos, EnumFacing.SOUTH)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes,
					new AxisAlignedBB(0.377, 0.377, 0.377, 0.623D, 0.623D, 1));
		}
		if (canConnectTo(world, pos, EnumFacing.EAST)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes,
					new AxisAlignedBB(0.377, 0.377, 0.377, 1, 0.623D, 0.623D));
		}
		if (canConnectTo(world, pos, EnumFacing.WEST)) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes,
					new AxisAlignedBB(0, 0.377, 0.377, 0.623D, 0.623D, 0.623D));
		}
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		// ���� �����, ��� � � ���������
		double[] sidebound = new double[6];
		sidebound[0]=sidebound[1]=sidebound[2]=0.377D;
		sidebound[3]=sidebound[4]=sidebound[5]=0.6231D;
		
		if (canConnectTo(world, pos, EnumFacing.DOWN) == true) {
			sidebound[1]=0;
		}
		if (canConnectTo(world, pos, EnumFacing.UP) == true) {
			sidebound[4]=1;
		}
		if (canConnectTo(world, pos, EnumFacing.NORTH) == true) {
			sidebound[2]=0;
		}
		if (canConnectTo(world, pos, EnumFacing.SOUTH) == true) {
			sidebound[5]=1;
		}
		if (canConnectTo(world, pos, EnumFacing.WEST) == true) {
			sidebound[0]=0;
		}
		if (canConnectTo(world, pos, EnumFacing.EAST) == true) {
			sidebound[3]=1;
		}
		
		return new AxisAlignedBB(sidebound[0], sidebound[1], sidebound[2], sidebound[3], sidebound[4], sidebound[5]);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		//������� ��������� ��� ����� "�������"
		return new BlockStateContainer(this, UP, DOWN, NORTH, SOUTH, WEST, EAST);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		//��������� ������� ������ ������������� true/false ���������
		// � ������� ����� �� ����� ���������(��� ���) �������� ������ � ������������ �������.
		return state
		.withProperty(DOWN, canConnectTo(world, pos, EnumFacing.DOWN))
		.withProperty(UP, canConnectTo(world, pos, EnumFacing.UP))
		.withProperty(NORTH, canConnectTo(world, pos, EnumFacing.NORTH))
		.withProperty(SOUTH, canConnectTo(world, pos, EnumFacing.SOUTH))
		.withProperty(WEST, canConnectTo(world, pos, EnumFacing.WEST))
		.withProperty(EAST, canConnectTo(world, pos, EnumFacing.EAST));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

}
