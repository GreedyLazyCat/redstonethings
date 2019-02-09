package com.greedycat.redstonethings.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyNetworkUtil {
	public static void buildNetwork(World world, BlockPos start) {
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
			//Здесь мы просто провереям, что полученная позиция - это генератор или хранилище и записываем в переменные
			//Это нам понадобиться позже
			if(tile != null) {
				generator = tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
				storage = tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				if(generator) {
					participants.add(nPos);
				}
				if(storage) {
					participants.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//Циклом прогоняемся по всем возможным "направлением"(не знаю как лучше перевести
				BlockPos child = nPos.offset(face);//эта функция возвращает позицию со сдвигом в данном направлении
				TileEntity tileEntity = world.getTileEntity(child);//получаем тайл
				boolean generator_child = false;
				boolean storage_child = false;
				if(!checked.contains(child)) {
					NetworkParticipantTile participant = null;
					//Здесь мы проверяем, что соседние блоки - это генератор или хранилище
					if(tileEntity != null) {
						if(tileEntity instanceof NetworkParticipantTile) {
							participant = (NetworkParticipantTile) tileEntity;
						}
						generator_child = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
						storage_child = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
					}
					//если провод - добавляем в очередь
					if(world.getBlockState(child).getBlock() instanceof Wire) {
						checked.add(child);
						queue.addLast(child);
					}
					//Если соседний блок генератор и родительский(тот от которого мы "шагаем" в сторону) не генератор
					if (generator_child && !generator) {
						checked.add(child);
						queue.addLast(child);
					}
					//Соседний блок - хранилище и родительский не хранилище
					if(storage_child && !storage) {
						checked.add(child);
						queue.addLast(child);
					}
					//Если родительский блок генератор и соседний блок - хранилище
					if(generator && storage_child) {
						checked.add(child);
						queue.addLast(child);
					}
				}
			}
		}
		if(!participants.isEmpty()) { // Участники не пусты
			//Создаем экзэмпляр сети и устанавливаем найденные генераторы и хранилища
			EnergyNetwork network = new EnergyNetwork();
			network.setParticipants(participants);
			//Получаем список сетей 
			EnergyNetworkList list = getEnergyNetworkList(world);
			if(list != null) {
				//И добавляем туда сеть
				int id = list.addNetwork(network);
				//Потом запускаем метод который выставит id всей сети
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
		while (!queue.isEmpty()) {
			/* <Так же как ив buildNetwork> */
			//Только вместо того, чтобы записывать в участники мы устанавливаем участникам id(проводам тоже)
			BlockPos nPos = queue.poll();
			TileEntity tile = world.getTileEntity(nPos);
			boolean generator = false;
			boolean storage = false;
			if(tile != null) {
				if(tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tile;
					participant.setNetworkId(id);
				}
				storage = tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				generator = tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
			}
			for (EnumFacing face : EnumFacing.VALUES) {
				BlockPos child = nPos.offset(face);
				TileEntity tileEntity = world.getTileEntity(child);
				boolean generator_child = false;
				boolean storage_child = false;
				if(!checked.contains(child)) {
					NetworkParticipantTile participant = null;
					if(tileEntity != null) {
						if(tileEntity instanceof NetworkParticipantTile) {
							participant = (NetworkParticipantTile) tileEntity;
						}
						generator_child = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
						storage_child = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
					}
					
					if(world.getBlockState(child).getBlock() instanceof Wire) {
						checked.add(child);
						queue.addLast(child);
					}
					if (generator_child && !generator) {
						if(participant != null) participant.setNetworkId(id);//Ставим id
						checked.add(child);
						queue.addLast(child);
					}
					if(storage_child && !storage) {
						if(participant != null) participant.setNetworkId(id);
						checked.add(child);
						queue.addLast(child);
					}
					if(generator && storage_child) {
						if(participant != null) participant.setNetworkId(id);
						checked.add(child);
						queue.addLast(child);
					}
				}
			}
			/* </Так же как ив buildNetwork> */
		}
	}
	
	public static void checkAround(World world, BlockPos pos) {
		HashSet<Integer> ids = new HashSet(1);// Список найденных id вокруг
		HashSet<NetworkParticipantTile> no_ids = new HashSet(1);// Список позиций у которых отсутствует id
		//Получаем tile и записываем в переменную генератор ли это или хранилище
		TileEntity start = world.getTileEntity(pos);
		boolean generator = start.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
		boolean storage = start.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
		//Здесь будет храниться id сети 
		int this_id = -1;// -1 значит, что сети нет
		EnergyNetworkList list = getEnergyNetworkList(world);
		//Если генератор то строим сеть т.к. генератор главный в сети. 
		if(generator) {
			buildNetwork(world, pos);
		}
		// Проверяем все соседние блоки
		for(EnumFacing facing : EnumFacing.VALUES) {
			BlockPos child = pos.offset(facing);
			TileEntity tile = world.getTileEntity(child);
			
			if(tile != null && tile instanceof NetworkParticipantTile) {
				boolean tile_generator = tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
				boolean tile_storage = tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				/* Так же, как и в buildNetwork выполняем проверки и в зависимости от того 
				 * есть ли у блока id сети или нет, добавляем в нужный список */
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
		//Конвертируем в массивы для удобства
		Integer[] ids_arr = ids.toArray(new Integer[ids.size()]);
		NetworkParticipantTile[] no_ids_arr = no_ids.toArray(new NetworkParticipantTile[no_ids.size()]);
		
		//Если мы нашли больше двух айдишников, значит у нас две сети и их нудно соеденить
		//HashSet поидее не позволит записать два одинаковых id
		if(ids_arr.length>1) {
			//Сюда мы соберем вместе все найденные сети
			HashSet<BlockPos> result = new HashSet<>();
			if(list != null) {
				for (int i = 0; i < ids_arr.length; i++) {
					if(list.getNetwork(ids_arr[i]) != null) {
						//Добавляем всех участников сети из списка
						result.addAll(list.getNetwork(ids_arr[i]).getParticipants());
						//И удаляем сеть т.к. мы будем объеденять и нам она будет ненужна
						list.removeNetwork(ids_arr[i]);
					}
				}
			}
			//Создаем сеть из собранных
			int new_id = list.addNetwork(new EnergyNetwork(result));
			//И выставляем id новой сети
			setNetworkId(world, pos, new_id);
		}
		//Если id только 1
		else if (!ids.isEmpty() && ids_arr.length == 1) {
			TileEntity tile = world.getTileEntity(pos);
			this_id = ids_arr[0];
			if(tile != null) {
				//Если это участник сети просто ставим ему id
				if(tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tile;
					participant.setNetworkId(this_id);
				}
				//+Если это генератор или хранилище добавляем в найденную сеть
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null) || 
						tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
					if(list != null && list.getNetwork(this_id) != null){
						list.getNetwork(this_id).getParticipants().add(pos);
					}
				}
			}
		}
		//Если мы нашли блоки без id и this_id(id сети, в которой состоит блок выщвавший checkAround)
		if(!no_ids.isEmpty() && this_id != -1) {
			for (int i = 0; i < no_ids_arr.length; i++) {
				NetworkParticipantTile participant = no_ids_arr[i];
				boolean generator_child = participant.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
				boolean storage_child = participant.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				
				//Добавляем в сеть если генератор/хранилище
				if(generator_child || storage_child && list != null) {
					list.getNetwork(this_id).add(participant.getPos());
				}
				//Запускаем установку id т.к. за этим блоком могут быть еще блоки с отсутствующим id
				setNetworkId(world, participant.getPos(), this_id);
				participant.setNetworkId(this_id);
			}
		}
	}
	/**
	 * 
	 * @param world Мир
	 * @param start Стартовая позиция
	 * @return Возвращает найденных участников
	 */
	public static HashSet<BlockPos> checkNetwork(World world, BlockPos start) {
		
		/* <Так же как и в buildNetwork>
		 * (за исключением того, что мы просто возвращаем найденных участников, а не создаем ииз них сеть)  */
		HashSet<BlockPos> checked = new HashSet<>(); 
		HashSet<BlockPos> participants = new HashSet<>();
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);
		
		queue.offer(start);
		checked.add(start);
		while (!queue.isEmpty()) {
			BlockPos nPos = queue.poll();
			
			TileEntity tile = world.getTileEntity(nPos);
			boolean generator = false;
			boolean storage = false;
			if(tile != null) {
				generator = tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
				storage = tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				if(generator) {
					participants.add(nPos);
				}
				if(storage) {
					participants.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//Циклом прогоняемся по всем возможным "направлением"(не знаю как лучше перевести
				BlockPos child = nPos.offset(face);//эта функция возвращает позицию со сдвигом в данном направлении
				TileEntity tileEntity = world.getTileEntity(child);//получаем тайл
				boolean generator_child = false;
				boolean storage_child = false;
				if(!checked.contains(child)) {
					if(tileEntity != null) {
						generator_child = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
						storage_child = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
					}
					if(world.getBlockState(child).getBlock() instanceof Wire) {
						checked.add(child);
						queue.addLast(child);
					}
					if (generator_child && !generator) {
						checked.add(child);
						queue.addLast(child);
					}
					if(storage_child && !storage) {
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
		/* </Так же как и в buildNetwork> */
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
	/**
	 * Этот метод проверяет есть ли в where элементы what
	 * @param where HashSet, в котором будет проверяться наличие элементов what
	 * @param what HashSet, в элементы которого будут проверяться на наличие в what 
	 * @return true/false
	 */
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
	//В метод breakBlock у блока
	public static void breakBlock(World worldIn,BlockPos pos) {
		EnergyNetworkList list = EnergyNetworkUtil.getEnergyNetworkList(worldIn);
		
		if(list != null) {
			TileEntity tile = worldIn.getTileEntity(pos);
			
			if(tile != null && tile instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				
				if(participant.hasNetworkId()) {
					EnergyNetwork network = list.getNetwork(participant.getNetworkId());
					
					if(network != null) {// не забыть
						network.remove(participant.getPos());
						if(!network.hasGenerators(worldIn)){
							list.removeNetwork(participant.getNetworkId());
							EnergyNetworkUtil.setNetworkId(worldIn, pos, -1);
						}
						
					}
				}
			
			}
		}
	}
	// в breakBlock
	public static void breakWire(World worldIn,BlockPos pos) {
		//Сюда мы сохраним подсети, получившиеся в результате того, что мы с ломали провод
		HashMap<BlockPos,EnergyNetwork> posses = new HashMap();
		int networkId = -1;
		EnergyNetwork network = null;
		EnergyNetworkList list = EnergyNetworkUtil.getEnergyNetworkList(worldIn);
		TileEntity tile = worldIn.getTileEntity(pos);
		
		if(tile != null && tile instanceof NetworkParticipantTile) {
			NetworkParticipantTile participantTile = (NetworkParticipantTile) tile;
			networkId = participantTile.getNetworkId();
			network = list.getNetwork(networkId);
		}
		
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockPos p = pos.offset(face);
			TileEntity tileEntity = worldIn.getTileEntity(p);
			
			if(tileEntity != null && tileEntity instanceof NetworkParticipantTile) {
				
				NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
				
				//Получив tile мы проверяем, что у него есть id
				if(participant.hasNetworkId()) {
					boolean check = false;
					if(network != null && network.getParticipants() != null) {
						
						//С помощью метода checkNetwork проверяем какие есть участники сети(генераторы/хранилища)
						HashSet<BlockPos> sub_network = checkNetwork(worldIn, p);
						//С помощью метода contains проверяем есть ли в этой подчети все участники родительской
						check = contains(sub_network, network.getParticipants());
						
						//Если нет, то мы должны поместить эту сеть в список подсетей
						//Перед этим проверив, есть ли подсети с такими же участниками
						//Так мы избавимся от дубликатов
						if(!check) {
							boolean equal_check = false;
							Iterator<Map.Entry<BlockPos, EnergyNetwork>> iterator = posses.entrySet().iterator();
							while (iterator.hasNext()) {
								Map.Entry<BlockPos, EnergyNetwork> entry = iterator.next();
								EnergyNetwork nEnergyNetwork = entry.getValue();
								
								equal_check = Arrays.equals(nEnergyNetwork.getParticipants().toArray(new BlockPos[nEnergyNetwork.getParticipants().size()]), 
										sub_network.toArray(new BlockPos[sub_network.size()]));
								
								if(equal_check) {
									break;
								}
							}
							//Если сеть не совпадает ни с одной из ранее найденных, то добавляем ее
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
			//Удаляем главную сеть
			list.removeNetwork(networkId);
			Iterator<Map.Entry<BlockPos, EnergyNetwork>> iterator = posses.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<BlockPos, EnergyNetwork> entry = iterator.next();
				EnergyNetwork nEnergyNetwork = entry.getValue();
				
				//Если подсеть имеет генераторы, значит мы можем ее добавить в список сетей
				if(nEnergyNetwork.hasGenerators(worldIn)) {
					int id = list.addNetwork(nEnergyNetwork);
					//выставляем id от ранее найденной стартовой позиции
					BlockPos start = entry.getKey();
					EnergyNetworkUtil.setNetworkId(worldIn, start, id);
				}
				else {//Если сеть не имеет генераторов - мы просто ставим id -1
					BlockPos start = entry.getKey();
					EnergyNetworkUtil.setNetworkId(worldIn, start, -1);
				}
			}
		}
	}
}
