package com.greedycat.redstonethings.block;

import java.security.PublicKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyNetwork;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.proxy.CommonProxy;
import com.greedycat.redstonethings.tile.BlockEnergyTileEntity;
import com.greedycat.redstonethings.tile.GeneratorTile;
import com.greedycat.redstonethings.tile.NetworkParticipantTile;
import com.greedycat.redstonethings.tile.WireTile;
import com.greedycat.redstonethings.util.EnergyNetworkUtil;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
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

public class Wire extends BlockEnergyTileEntity<WireTile>{
	//Каждое свойство отвечает за то, подключен ли к этой стороне провод или другой участник цепи(true/false)
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
	
	//Метод "строящий" сеть, передаем в него мир и стартовую позицию
	@Deprecated
	public ArrayList<BlockPos> buildNetwork(IBlockAccess worldIn, BlockPos start) {
		HashSet<BlockPos> checked = new HashSet<>(); //список проверенных блоков
		ArrayList<BlockPos> generators = new ArrayList<>();//список генераторов, которые будут найдены в сети
		ArrayList<BlockPos> storages = new ArrayList<>();//список проводов, которые будут найдены в сети
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//Очередь, это особенность реализации алгоритма поиска в ширину.
		
		queue.offer(start);//Добавляем стартовую позицию в очередь
		checked.add(start);//И сразу добавляем в проверенные
		while (!queue.isEmpty()) {//Выполняем пока очередь не пуста
			BlockPos nPos = queue.poll();//Этот метод возвращает объект из головы очереди и сразу его удаляет.
			//Проверяем не генератор ли стартовая позиция.
			TileEntity tile = worldIn.getTileEntity(nPos);
			if(tile != null) {
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {// У меня свои капы на генератор и хранилище
					generators.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//Циклом прогоняемся по всем возможным "направлением"(не знаю как лучше перевести
				BlockPos child = nPos.offset(face);//эта функция возвращает позицию со сдвигом в данном направлении
				TileEntity tileEntity = worldIn.getTileEntity(child);//получаем тайл
				if(tileEntity != null) {
					//проверяем, что это, генератор или хранилище
					if(tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
						generators.add(child); // и добовлеям их в соответственные списки
					}
					if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
						storages.add(child);// и добовлеям их в соответственные списки
					}
				}
				//Если в списке проверенных нету этой позиции и блок на этой позиции - это провод, добавляем в список
				//проверенных + в очередь, чтобы с этой позиции проверить уже другие блоки
				if(!checked.contains(child) && worldIn.getBlockState(child).getBlock() instanceof Wire) {
					checked.add(child);
					queue.addLast(child);//Добавляем в низ очереди
				}
			}	
		}
		
		if(!generators.isEmpty()) {//Если есть в сети  генераторы
			for (int i = 0; i < generators.size(); i++) {//прогоняемся по ним циклом
				BlockPos gPos = generators.get(i);
				TileEntity tileEntity = worldIn.getTileEntity(gPos);
				if(tileEntity != null && tileEntity instanceof GeneratorTile) {
					//Проверяем, что они наследают спец класс GeneratorTile, нижке под спойлером есть этот класс
					GeneratorTile generator = (GeneratorTile) tileEntity;
					generator.setStorages(storages);//устонавливаем в этот генератор список найденных в сети хранилищ
				}
			}
		}
		return generators;
	}
	

	public void cleanNetwork(World world) {
		if(world.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
			EnergyNetworkList list = world.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
			list.networks.clear();
		}
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.getHeldItem(hand) != ItemStack.EMPTY && playerIn.getHeldItem(hand).getItem() == Items.APPLE) {
			cleanNetwork(worldIn);
		}
		TileEntity tileEntity = worldIn.getTileEntity(pos);//получаем тайл
		if(tileEntity != null && tileEntity instanceof NetworkParticipantTile) {
			NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
			System.out.println("Participant id is:" + participant.getNetworkId());
		}
		if(worldIn.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
			EnergyNetworkList list = worldIn.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
			System.out.println("Count of networks:" + list.getNetworks().size());
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		// TODO Auto-generated method stub
		EnergyNetworkUtil.breakWire(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		//BlockPos позиция одного из участников, для удобства. От неё запустим алгоритм, который поставит айдишники
		//EnergyNetworkUtil.breakWire(worldIn, pos);
	}

	//Это метод нужен для проверки можем ли мы текстуркой "соедениться" с другим блоком
	public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing){
		BlockPos child = pos.offset(facing);
		TileEntity tileEntity = world.getTileEntity(child);
		if(tileEntity != null) {
			if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
				//если генератор - да
				return true;
			}
			if(tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
				//если хранилище - да
				return true;
			}
		}
		if(world.getBlockState(child).getBlock() instanceof Wire) {
			//если провод - да
			return true;
		}
		return false;
	}
	
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		//В зависимости от того, какие стороны подключены, выставляем определенную коллизию
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
		// Тоже самое, что и с коллизией
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
		//Создаем контейнер для наших "свойств"
		return new BlockStateContainer(this, UP, DOWN, NORTH, SOUTH, WEST, EAST);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		//Пользуясь методом просто устанавливаем true/false свойствам
		// С помощью этого мы будем рендерить(или нет) частичку кабеля с определенной стороны.
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

	@Override
	public Class<WireTile> getTileEntityClass() {
		// TODO Auto-generated method stub
		return WireTile.class;
	}

	@Override
	public WireTile createTileEntity(World world, IBlockState blockState) {
		// TODO Auto-generated method stub
		return new WireTile();
	}

}
