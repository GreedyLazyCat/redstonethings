package com.greedycat.redstonethings.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

import com.greedycat.redstonethings.block.Wire;
import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyNetwork;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.tile.NetworkParticipant;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyNetworkUtil {
	public static void buildNetworkNew(World world, BlockPos start) {
		HashSet<BlockPos> checked = new HashSet<>(); //список проверенных блоков
		HashSet<BlockPos> participants = new HashSet<>();//список генераторов, которые будут найдены в сети
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//Очередь, это особенность реализации алгоритма поиска в ширину.
		
		queue.offer(start);//Добавляем стартовую позицию в очередь
		checked.add(start);//И сразу добавляем в проверенные
		while (!queue.isEmpty()) {//Выполняем пока очередь не пуста
			BlockPos nPos = queue.poll();//Этот метод возвращает объект из головы очереди и сразу его удаляет.
			//Проверяем не генератор ли стартовая позиция.
			TileEntity tile = world.getTileEntity(nPos);
			if(tile != null) {
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {// У меня свои капы на генератор и хранилище
					participants.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//Циклом прогоняемся по всем возможным "направлением"(не знаю как лучше перевести
				BlockPos child = nPos.offset(face);//эта функция возвращает позицию со сдвигом в данном направлении
				TileEntity tileEntity = world.getTileEntity(child);//получаем тайл
				if(tileEntity != null) {
					//проверяем, что это, генератор или хранилище
					if(tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
						participants.add(child); // и добовлеям их в соответственные списки
					}
					if(tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
						participants.add(child);// и добовлеям их в соответственные списки
					}
				}
				//Если в списке проверенных нету этой позиции и блок на этой позиции - это провод, добавляем в список
				//проверенных + в очередь, чтобы с этой позиции проверить уже другие блоки
				if(!checked.contains(child) && world.getBlockState(child).getBlock() instanceof Wire) {
					checked.add(child);
					queue.addLast(child);//Добавляем в низ очереди
				}
			}
		}
		if(!participants.isEmpty()) {
			EnergyNetwork network = new EnergyNetwork();
			network.setParticipants(participants);
			if(world.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
				EnergyNetworkList list = world.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
				int id = list.addNetwork(network);
				setNetworkId(world, start, id);
			}
		}
	}
	
	public static void setNetworkId(World world, BlockPos start, int id) {
		HashSet<BlockPos> checked = new HashSet<>(); //список проверенных блоков
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//Очередь, это особенность реализации алгоритма поиска в ширину.
		
		queue.offer(start);//Добавляем стартовую позицию в очередь
		checked.add(start);//И сразу добавляем в проверенные
		while (!queue.isEmpty()) {//Выполняем пока очередь не пуста
			BlockPos nPos = queue.poll();//Этот метод возвращает объект из головы очереди и сразу его удаляет.
			//Проверяем не генератор ли стартовая позиция.
			TileEntity tile = world.getTileEntity(nPos);
			if(tile != null && tile instanceof NetworkParticipant) {
				NetworkParticipant participant = (NetworkParticipant) tile;
				participant.setNetworkId(id);
			}
			for (EnumFacing face : EnumFacing.VALUES) {//Циклом прогоняемся по всем возможным "направлением"(не знаю как лучше перевести
				BlockPos child = nPos.offset(face);//эта функция возвращает позицию со сдвигом в данном направлении
				TileEntity tileEntity = world.getTileEntity(child);//получаем тайл
				if(tileEntity != null && tileEntity instanceof NetworkParticipant) {
					NetworkParticipant participant = (NetworkParticipant) tileEntity;
					participant.setNetworkId(id);
				}
				//Если в списке проверенных нету этой позиции и блок на этой позиции - это провод, добавляем в список
				//проверенных + в очередь, чтобы с этой позиции проверить уже другие блоки
				if(!checked.contains(child) && world.getBlockState(child).getBlock() instanceof Wire) {
					checked.add(child);
					queue.addLast(child);//Добавляем в низ очереди
				}
			}
		}
	}
	
	public static void checkAround(World world, BlockPos pos) {
		ArrayList<Integer> ids = new ArrayList(1);
		for(EnumFacing facing : EnumFacing.VALUES) {
			BlockPos child = pos.offset(facing);
			TileEntity tile = world.getTileEntity(child);
			if(tile != null && tile instanceof NetworkParticipant) {
				NetworkParticipant participant = (NetworkParticipant) tile;
				if(participant.hasNetworkId()) ids.add(participant.getNetworkId());
			}
		}
		if(ids.size()>1) {
			buildNetworkNew(world, pos);
		}
		else if (!ids.isEmpty() && ids.size() == 1) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile != null && tile instanceof NetworkParticipant) {
				NetworkParticipant participant = (NetworkParticipant) tile;
				participant.setNetworkId(ids.get(0));
			}
		}
	}
}
