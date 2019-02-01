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
		HashSet<BlockPos> checked = new HashSet<>(); //������ ����������� ������
		HashSet<BlockPos> participants = new HashSet<>();//������ �����������, ������� ����� ������� � ����
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//�������, ��� ����������� ���������� ��������� ������ � ������.
		
		
		queue.offer(start);//��������� ��������� ������� � �������
		checked.add(start);//� ����� ��������� � �����������
		while (!queue.isEmpty()) {//��������� ���� ������� �� �����
			BlockPos nPos = queue.poll();//���� ����� ���������� ������ �� ������ ������� � ����� ��� �������.
			//��������� �� ��������� �� ��������� �������.
			TileEntity tile = world.getTileEntity(nPos);
			boolean generator = false;
			if(tile != null) {
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null)) {// � ���� ���� ���� �� ��������� � ���������
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
			for (EnumFacing face : EnumFacing.VALUES) {//������ ����������� �� ���� ��������� "������������"(�� ���� ��� ����� ���������
				BlockPos child = nPos.offset(face);//��� ������� ���������� ������� �� ������� � ������ �����������
				TileEntity tileEntity = world.getTileEntity(child);//�������� ����
				boolean generator_child = false;
				boolean storage_child = false;
				if(tileEntity != null) {
					generator_child = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
					storage_child = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
					//���������, ��� ���, ��������� ��� ���������
					if(generator_child) {
						participants.add(child); // � ��������� �� � ��������������� ������
					}
					if(storage_child) {
						participants.add(child);// � ��������� �� � ��������������� ������
					}
				}
				//���� � ������ ����������� ���� ���� ������� � ���� �� ���� ������� - ��� ������, ��������� � ������
				//����������� + � �������, ����� � ���� ������� ��������� ��� ������ �����
				if(!checked.contains(child)) {
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
		HashSet<BlockPos> checked = new HashSet<>(); //������ ����������� ������
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//�������, ��� ����������� ���������� ��������� ������ � ������.
		
		queue.offer(start);//��������� ��������� ������� � �������
		checked.add(start);//� ����� ��������� � �����������
		while (!queue.isEmpty()) {//��������� ���� ������� �� �����
			BlockPos nPos = queue.poll();//���� ����� ���������� ������ �� ������ ������� � ����� ��� �������.
			//��������� �� ��������� �� ��������� �������.
			TileEntity tile = world.getTileEntity(nPos);
			boolean generator = false;
			if(tile != null && tile instanceof NetworkParticipantTile) {
				if(tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tile;
					participant.setNetworkId(id);
				}
				generator = tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
			}
			for (EnumFacing face : EnumFacing.VALUES) {//������ ����������� �� ���� ��������� "������������"(�� ���� ��� ����� ���������
				BlockPos child = nPos.offset(face);//��� ������� ���������� ������� �� ������� � ������ �����������
				TileEntity tileEntity = world.getTileEntity(child);//�������� ����
				boolean generator_child = false;
				boolean storage_child = false;
				if(tileEntity != null) {
					if(tileEntity instanceof NetworkParticipantTile) {
						NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
						participant.setNetworkId(id);
					}
					generator_child = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
					storage_child = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
				}
				if(!checked.contains(child)) {
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
	}
	
	public static void checkAround(World world, BlockPos pos) {
		ArrayList<Integer> ids = new ArrayList(1);
		ArrayList<NetworkParticipantTile> no_ids = new ArrayList(1);
		TileEntity start = world.getTileEntity(pos);
		boolean generator = start.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
		boolean storage = start.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
		int this_id = -1;
		
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
				if(!generator && !storage) {
					if(participant.hasNetworkId()) {
						ids.add(participant.getNetworkId());
					}
					else {
						no_ids.add(participant);
					}
				}
				
			}
			
		}
		if(ids.size()>1) {
			System.out.println("IDs have more than one element");
			EnergyNetworkList list = getEnergyNetworkList(world);
			HashSet<BlockPos> result = new HashSet<>();
			if(list != null) {
				for (int i = 0; i < ids.size(); i++) {
					result.addAll(list.getNetwork(ids.get(i)).getParticipants());
					list.removeNetwork(ids.get(i));
				}
			}
			int new_id = list.addNetwork(new EnergyNetwork(result));
			setNetworkId(world, pos, new_id);
		}
		else if (!ids.isEmpty() && ids.size() == 1) {
			System.out.println("IDs have only one element");
			TileEntity tile = world.getTileEntity(pos);
			this_id = ids.get(0);
			if(tile != null) {
				if(tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tile;
					participant.setNetworkId(this_id);
				}
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null) || 
						tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
					if(world.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
						EnergyNetworkList list = world.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
						if(list.getNetwork(this_id) != null){
							list.getNetwork(this_id).getParticipants().add(pos);
						}
					}
				}
			}
		}
		if(!no_ids.isEmpty() && this_id != -1) {
			for (int i = 0; i < no_ids.size(); i++) {
				NetworkParticipantTile participant = no_ids.get(i);
				participant.setNetworkId(this_id);
			}
		}
	}
	/**
	 * 
	 * @param world ���
	 * @param start ��������� �������
	 * @param networkId ID ����, ������� ������ ���� �����������
	 * @return ���������� ��������� ���������� �� ����
	 */
	public static HashSet<BlockPos> checkNetwork(World world, BlockPos start) {
		
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
				boolean generator = false;
				boolean storage = false;
				if(tileEntity != null) {
					generator = tileEntity.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null);
					storage = tileEntity.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null);
					if(generator) {
						participants.add(child); 
					}
					if(storage) {
						participants.add(child);
					}
				}
				if(!checked.contains(child) && (world.getBlockState(child).getBlock() instanceof Wire || generator || storage)) {
					checked.add(child);
					queue.addLast(child);
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
						
						if(!check) {
							System.out.println("Check");
							EnergyNetwork energyNetwork = new EnergyNetwork();
							energyNetwork.setParticipants(sub_network);
							posses.put(p, energyNetwork);
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
