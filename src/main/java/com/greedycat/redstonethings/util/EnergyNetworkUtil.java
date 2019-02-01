package com.greedycat.redstonethings.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.greedycat.redstonethings.block.Wire;
import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyNetwork;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.tile.NetworkParticipantTile;

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
			boolean generator = false;
			if(tile != null) {
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {// У меня свои капы на генератор и хранилище
					generator = true;
					participants.add(nPos);
				}
				if(tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tile;
					if(participant.hasNetworkId()) {
						if(world.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
							EnergyNetworkList list = world.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
							list.removeNetwork(participant.getNetworkId());
						}
					}
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//Циклом прогоняемся по всем возможным "направлением"(не знаю как лучше перевести
				BlockPos child = nPos.offset(face);//эта функция возвращает позицию со сдвигом в данном направлении
				TileEntity tileEntity = world.getTileEntity(child);//получаем тайл
				boolean generator_child = false;
				boolean storage_child = false;
				if(tileEntity != null) {
					generator_child = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
					storage_child = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
					//проверяем, что это, генератор или хранилище
					if(generator_child) {
						participants.add(child); // и добовлеям их в соответственные списки
					}
					if(storage_child) {
						participants.add(child);// и добовлеям их в соответственные списки
					}
				}
				//Если в списке проверенных нету этой позиции и блок на этой позиции - это провод, добавляем в список
				//проверенных + в очередь, чтобы с этой позиции проверить уже другие блоки
				if(!checked.contains(child)) {
					if(world.getBlockState(child).getBlock() instanceof Wire) {
						checked.add(child);
						queue.addLast(child);
					}
					if (generator_child && !generator) {
						System.out.println("GCH G");
						checked.add(child);
						queue.addLast(child);
					}
					if(generator && storage_child) {
						checked.add(child);
						queue.addLast(child);
					}
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
		}else {
			setNetworkId(world, start, -1);
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
			boolean generator = false;
			if(tile != null) {
				if(tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tile;
					participant.setNetworkId(id);
				}
				generator = tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
			}
			for (EnumFacing face : EnumFacing.VALUES) {//Циклом прогоняемся по всем возможным "направлением"(не знаю как лучше перевести
				BlockPos child = nPos.offset(face);//эта функция возвращает позицию со сдвигом в данном направлении
				TileEntity tileEntity = world.getTileEntity(child);//получаем тайл
				boolean generator_child = false;
				boolean storage_child = false;
				if(tileEntity != null) {
					generator_child = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
					storage_child = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
					if(tileEntity instanceof NetworkParticipantTile) {
						if(world.getBlockState(child).getBlock() instanceof Wire) {
							NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
							participant.setNetworkId(id);
						}
						if (generator_child && !generator) {
							NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
							participant.setNetworkId(id);
						}
						if(generator && storage_child) {
							NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
							participant.setNetworkId(id);
						}
						
					}
				}
				if(!checked.contains(child)) {
					if(world.getBlockState(child).getBlock() instanceof Wire) {
						checked.add(child);
						queue.addLast(child);
					}
					if (generator_child && !generator) {
						System.out.println("GCH G");
						checked.add(child);
						queue.addLast(child);
					}
					if(generator && storage_child) {
						checked.add(child);
						queue.addLast(child);
					}
				}
			}
		}
	}
	
	public static void checkAround(World world, BlockPos pos) {
		HashSet<Integer> ids = new HashSet(1);
		HashSet<NetworkParticipantTile> no_ids = new HashSet(1);
		TileEntity start = world.getTileEntity(pos);
		boolean generator = start.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
		boolean storage = start.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
		int this_id = -1;
		EnergyNetworkList list = getEnergyNetworkList(world);
		
		if(generator) {
			buildNetworkNew(world, pos);
		}
		
		for(EnumFacing facing : EnumFacing.VALUES) {
			BlockPos child = pos.offset(facing);
			TileEntity tile = world.getTileEntity(child);
			if(tile != null && tile instanceof NetworkParticipantTile) {
				boolean tile_generator = tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
				boolean tile_storage = tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				if(generator && !tile_generator) {
					if(participant.hasNetworkId()) {
						ids.add(participant.getNetworkId());
					}
					else {
						no_ids.add(participant);
					}
				}
				if(storage && !tile_storage) {
					if(participant.hasNetworkId()) {
						ids.add(participant.getNetworkId());
					}
					else {
						no_ids.add(participant);
					}
				}
				if(world.getBlockState(pos).getBlock() instanceof Wire) {
					if(participant.hasNetworkId()) {
						ids.add(participant.getNetworkId());
					}
					else {
						no_ids.add(participant);
					}
				}
				
			}
			
		}
		
		Integer[] ids_arr = ids.toArray(new Integer[ids.size()]);
		NetworkParticipantTile[] no_ids_arr = no_ids.toArray(new NetworkParticipantTile[no_ids.size()]);
		
		if(ids_arr.length>1) {
			System.out.println("IDs have more than one element");
			HashSet<BlockPos> result = new HashSet<>();
			if(list != null) {
				for (int i = 0; i < ids_arr.length; i++) {
					if(list.getNetwork(ids_arr[i]) != null) {
						result.addAll(list.getNetwork(ids_arr[i]).getParticipants());
						list.removeNetwork(ids_arr[i]);
					}
				}
			}
			int new_id = list.addNetwork(new EnergyNetwork(result));
			setNetworkId(world, pos, new_id);
		}
		else if (!ids.isEmpty() && ids_arr.length == 1) {
			System.out.println("IDs have only one element");
			TileEntity tile = world.getTileEntity(pos);
			this_id = ids_arr[0];
			if(tile != null) {
				if(tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tile;
					participant.setNetworkId(this_id);
				}
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null) || 
						tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
					if(list != null && list.getNetwork(this_id) != null){
						list.getNetwork(this_id).getParticipants().add(pos);
					}
				}
			}
		}
		if(!no_ids.isEmpty() && this_id != -1) {
			System.out.println("No ids is not empty");
			for (int i = 0; i < no_ids_arr.length; i++) {
				NetworkParticipantTile participant = no_ids_arr[i];
				boolean generator_child = participant.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
				boolean storage_child = participant.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				if(generator_child || storage_child && list != null) {
					list.getNetwork(this_id).add(participant.getPos());
				}
				participant.setNetworkId(this_id);
			}
		}
	}
	/**
	 * 
	 * @param world Мир
	 * @param start Стартовая позиция
	 * @param networkId ID сети, которое должно быть установлено
	 * @return Возвращает найденных участников из сети
	 */
	public static HashSet<BlockPos> checkNetwork(World world, BlockPos start) {
		
		HashSet<BlockPos> checked = new HashSet<>(); //список проверенных блоков
		HashSet<BlockPos> participants = new HashSet<>();//список генераторов, которые будут найдены в сети
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//Очередь, это особенность реализации алгоритма поиска в ширину.
		
		queue.offer(start);//Добавляем стартовую позицию в очередь
		checked.add(start);//И сразу добавляем в проверенные
		while (!queue.isEmpty()) {//Выполняем пока очередь не пуста
			BlockPos nPos = queue.poll();//Этот метод возвращает объект из головы очереди и сразу его удаляет.
			//Проверяем не генератор ли стартовая позиция.
			TileEntity tile = world.getTileEntity(nPos);
			boolean generator = false;
			boolean storage = false;
			if(tile != null) {
				generator = tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
				storage = tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {// У меня свои капы на генератор и хранилище
					participants.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//Циклом прогоняемся по всем возможным "направлением"(не знаю как лучше перевести
				BlockPos child = nPos.offset(face);//эта функция возвращает позицию со сдвигом в данном направлении
				TileEntity tileEntity = world.getTileEntity(child);//получаем тайл
				boolean generator_child = false;
				boolean storage_child = false;
				if(tileEntity != null) {
					generator_child = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
					storage_child = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
					if(generator_child) {
						participants.add(child); 
					}
					if(storage_child) {
						participants.add(child);
					}
				}
				if(!checked.contains(child) && (world.getBlockState(child).getBlock() instanceof Wire || generator || storage)) {
					if(world.getBlockState(child).getBlock() instanceof Wire) {
						checked.add(child);
						queue.addLast(child);
					}
					if (generator_child && !generator) {
						checked.add(child);
						queue.addLast(child);
					}
					if(generator && storage_child) {
						checked.add(child);
						queue.addLast(child);
					}
				}
			}
		}
		return participants;
	}
	
	public static EnergyNetworkList getEnergyNetworkList(World world) {
		if(world.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
			EnergyNetworkList list = world.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
			return list;
		}
		return null;
	}
	
	public static int getNetworkIdFromBlock(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && tile instanceof NetworkParticipantTile) {
			NetworkParticipantTile participant = (NetworkParticipantTile) tile;
			if(participant.hasNetworkId()) {
				return participant.getNetworkId();
			}
		}
		return -1;
	}
	
	public static  boolean contains(HashSet<BlockPos> where, HashSet<BlockPos> what) {
		BlockPos[] we = where.toArray(new BlockPos[where.size()]);
		BlockPos[] wt = what.toArray(new BlockPos[where.size()]);
		if(we.length < wt.length) {
			return false;
		}
		for (int i = 0; i < wt.length; i++) {
			for (int j = 0; j < we.length; j++) {
				if (wt[i] == we[j]) { 
	                 continue;
	            }
				if(j == we.length - 1) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void breakBlock(World worldIn,BlockPos pos) {
		EnergyNetworkList list = EnergyNetworkUtil.getEnergyNetworkList(worldIn);
		
		if(list != null) {
			TileEntity tile = worldIn.getTileEntity(pos);
			
			if(tile != null && tile instanceof NetworkParticipantTile) {
				System.out.println("Tile");
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				
				if(participant.hasNetworkId()) {
					EnergyNetwork network = list.getNetwork(participant.getNetworkId());
					
					if(network != null) {
						network.remove(participant.getPos());
						boolean check = false;
						
						for (BlockPos nPos : network.getParticipants()) {
							TileEntity tileEntity = worldIn.getTileEntity(nPos);
							
							if(tileEntity != null && tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {
								check = true;
								break;
							}
						}
						
						if(!check) {
							System.out.println("Network have no generator");
							list.removeNetwork(participant.getNetworkId());
							EnergyNetworkUtil.setNetworkId(worldIn, pos, -1);
						}
						
					}
				}
			
			}
		}
	}
	
	public static void breakWire(World worldIn,BlockPos pos) {
		HashMap<BlockPos,EnergyNetwork> posses = new HashMap();
		int networkId = -1;
		EnergyNetworkList list = EnergyNetworkUtil.getEnergyNetworkList(worldIn);
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockPos p = pos.offset(face);
			TileEntity tileEntity = worldIn.getTileEntity(p);
			
			if(tileEntity != null && tileEntity instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
				EnergyNetwork network = null;
				if(participant.hasNetworkId()) {
					network = list.getNetwork(participant.getNetworkId());
					networkId = participant.getNetworkId();
					boolean check = false;
					if(network != null && network.getParticipants() != null) {
						
						HashSet<BlockPos> sub_network = EnergyNetworkUtil.checkNetwork(worldIn, p);
						check = EnergyNetworkUtil.contains(sub_network, network.getParticipants());
						
						if(!check) {//!! опасность говнокода
							boolean equal_check = false;
							Iterator<Map.Entry<BlockPos, EnergyNetwork>> iterator = posses.entrySet().iterator();
							while (iterator.hasNext()) {
								Map.Entry<BlockPos, EnergyNetwork> entry = iterator.next();
								EnergyNetwork nEnergyNetwork = entry.getValue();
								equal_check = EnergyNetworkUtil.contains(nEnergyNetwork.getParticipants(), sub_network);
							}
							if(!equal_check) {
								EnergyNetwork energyNetwork = new EnergyNetwork();
								energyNetwork.setParticipants(sub_network);
								posses.put(p, energyNetwork);
							}
						}else {
							continue;
						}
					}
				}
			}
		}
		
		if(!posses.isEmpty() && networkId != -1) {
			System.out.println("Size of posses:" + posses.size());
			list.removeNetwork(networkId);
			Iterator<Map.Entry<BlockPos, EnergyNetwork>> iterator = posses.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<BlockPos, EnergyNetwork> network = iterator.next();
				EnergyNetwork nEnergyNetwork = network.getValue();
				
				if(nEnergyNetwork.hasGenerators(worldIn)) {
					System.out.println("Has generators");
					int id = list.addNetwork(nEnergyNetwork);
					BlockPos start = network.getKey();
					EnergyNetworkUtil.setNetworkId(worldIn, start, id);
				}
				else {
					BlockPos start = network.getKey();
					EnergyNetworkUtil.setNetworkId(worldIn, start, -1);
				}
			}
		}
	}
}
