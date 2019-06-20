package com.greedycat.redstonethings.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;

import com.greedycat.redstonethings.block.Wire;
import com.greedycat.redstonethings.capabilities.EnergyGeneratorCapability;
import com.greedycat.redstonethings.capabilities.EnergyNetwork;
import com.greedycat.redstonethings.capabilities.EnergyNetworkList;
import com.greedycat.redstonethings.capabilities.EnergyNetworkListCapability;
import com.greedycat.redstonethings.capabilities.EnergyStorageCapability;
import com.greedycat.redstonethings.util.tile.NetworkParticipantTile;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyNetworkUtil {
	
	/*
	 * TO DO: ������� ������ �������� ������ ������� �� ������. 
	 */
	
	public static void buildNetwork(World world, BlockPos start) {
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
			boolean storage = false;
			//����� �� ������ ���������, ��� ���������� ������� - ��� ��������� ��� ��������� � ���������� � ����������
			//��� ��� ������������ �����
			if(tile != null && tile instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				
				generator = participant.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, null, false);
				storage = participant.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, null, false);
				
				if(generator || storage) {
					participants.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//������ ����������� �� ���� ��������� "������������"(�� ���� ��� ����� ���������
				BlockPos child = nPos.offset(face);//��� ������� ���������� ������� �� ������� � ������ �����������
				TileEntity tileEntity = world.getTileEntity(child);//�������� ����
				boolean generator_child = false;
				boolean storage_child = false;
				if(!checked.contains(child)) {
					NetworkParticipantTile participant = null;
					//����� �� ���������, ��� �������� ����� - ��� ��������� ��� ���������
					if(tileEntity != null && tileEntity instanceof NetworkParticipantTile) {
						participant = (NetworkParticipantTile) tileEntity;
						generator_child = participant.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, face, true);
						storage_child = participant.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, face, true);
					}
					//���� ������ - ��������� � �������
					if(world.getBlockState(child).getBlock() instanceof Wire) {
						checked.add(child);
						queue.addLast(child);
					}
					//���� �������� ���� ��������� � ������������(��� �� �������� �� "������" � �������) �� ���������
					if (generator_child && !generator) {
						checked.add(child);
						queue.addLast(child);
					}
					//�������� ���� - ��������� � ������������ �� ���������
					if(storage_child && !storage) {
						checked.add(child);
						queue.addLast(child);
					}
					//���� ������������ ���� ��������� � �������� ���� - ���������
					if(generator && storage_child) {
						checked.add(child);
						queue.addLast(child);
					}
				}
			}
		}
		if(!participants.isEmpty()) { // ��������� �� �����
			//������� ��������� ���� � ������������� ��������� ���������� � ���������
			EnergyNetwork network = new EnergyNetwork();
			network.setParticipants(participants);
			//�������� ������ ����� 
			EnergyNetworkList list = getEnergyNetworkList(world);
			if(list != null) {
				//� ��������� ���� ����
				int id = list.addNetwork(network);
				//����� ��������� ����� ������� �������� id ���� ����
				setNetworkId(world, start, id, null);
			}
		}else {
			setNetworkId(world, start, -1, null);
		}
	}
	
	public static void setNetworkId(World world, BlockPos start, int id, EnumFacing startFacing) {
		HashSet<BlockPos> checked = new HashSet<>(); //������ ����������� ������
		ArrayDeque<BlockPos> queue = new ArrayDeque<>(100);//�������, ��� ����������� ���������� ��������� ������ � ������.
		EnergyNetworkList list = getEnergyNetworkList(world);
		
		queue.offer(start);//��������� ��������� ������� � �������
		checked.add(start);//� ����� ��������� � �����������
		while (!queue.isEmpty()) {
			/* <��� �� ��� �� buildNetwork> */
			//������ ������ ����, ����� ���������� � ��������� �� ������������� ���������� id(�������� ����)
			BlockPos nPos = queue.poll();
			TileEntity tile = world.getTileEntity(nPos);
			boolean generator = false;
			boolean storage = false;
			if(tile != null && tile instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				
				if(startFacing == null) {
					generator = participant.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, null, false);
					storage = participant.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, null, false);
					if(generator) System.out.println("Added generator");
					if(storage) System.out.println("Added storage");
				}
				else {
					generator = participant.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, startFacing, true);
					storage = participant.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, startFacing, true);
					startFacing = null;
				}
				
				participant.setNetworkId(id);
				
				if((generator || storage) && list != null && list.getNetwork(id) != null) {
					list.getNetwork(id).getParticipants().add(participant.getPos());
				}
				
			}
			for (EnumFacing face : EnumFacing.VALUES) {
				BlockPos child = nPos.offset(face);
				TileEntity tileEntity = world.getTileEntity(child);
				boolean generator_child = false;
				boolean storage_child = false;
				if(!checked.contains(child)) {
					if(tileEntity != null && tileEntity instanceof NetworkParticipantTile) {
						NetworkParticipantTile participantTile = (NetworkParticipantTile) tileEntity;
						generator_child = participantTile.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, face, true);
						storage_child = participantTile.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, face, true);
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
			/* </��� �� ��� �� buildNetwork> */
		}
	}
	
	public static void checkAround(World world, BlockPos pos) {
		TileEntity start = world.getTileEntity(pos);
		
		if(start != null && start instanceof NetworkParticipantTile) {
			NetworkParticipantTile participant = (NetworkParticipantTile) start;
			if(participant.hasNetworkId()) return;
			
			HashSet<Integer> ids = new HashSet(1);// ������ ��������� id ������
			HashSet<EnumFacing> facings = new HashSet<>(1);
			HashMap<NetworkParticipantTile, EnumFacing> no_ids = new HashMap(1);// ������ ������� � ������� ����������� id
			//�������� tile � ���������� � ���������� ��������� �� ��� ��� ���������
			
			boolean generator = participant.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, null, false);
			boolean storage = participant.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, null, false);
			//����� ����� ��������� id ���� 
			int this_id = -1;// -1 ������, ��� ���� ���
			EnergyNetworkList list = getEnergyNetworkList(world);
			//���� ��������� �� ������ ���� �.�. ��������� ������� � ����. 
			if(generator) {
				buildNetwork(world, pos);
			}
			// ��������� ��� �������� �����
			for(EnumFacing face : EnumFacing.VALUES) {
				BlockPos child = pos.offset(face);
				TileEntity tile = world.getTileEntity(child);
				
				if(tile != null && tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participantTile = (NetworkParticipantTile) tile;
					boolean tile_generator = participantTile.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, face, true);
					boolean tile_storage = participantTile.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, face, true);
					
					/* ��� ��, ��� � � buildNetwork ��������� �������� � � ����������� �� ���� 
					 * ���� �� � ����� id ���� ��� ���, ��������� � ������ ������ */
					if(generator && !tile_generator) {
						if(participantTile.hasNetworkId()) {
							ids.add(participantTile.getNetworkId());
							facings.add(face);
						}
						else {
							no_ids.put(participantTile, face);
						}
					}
					if(storage && !tile_storage) {
						if(participantTile.hasNetworkId()) {
							ids.add(participantTile.getNetworkId());
							facings.add(face);
						}
						else {
							no_ids.put(participantTile, face);
						}
					}
					if(world.getBlockState(pos).getBlock() instanceof Wire) {
						if(participantTile.hasNetworkId()) {
							ids.add(participantTile.getNetworkId());
							facings.add(face);
						}
						else {
							no_ids.put(participantTile, face);
						}
					}
					
				}
				
			}
			//������������ � ������� ��� ��������
			Integer[] ids_arr = ids.toArray(new Integer[ids.size()]);
			NetworkParticipantTile[] no_ids_arr = no_ids.keySet().toArray(new NetworkParticipantTile[no_ids.size()]);
			EnumFacing[] facing_arr = facings.toArray(new EnumFacing[facings.size()]);
			
			//���� �� ����� ������ ���� ����������, ������ � ��� ��� ���� � �� ����� ���������
			//HashSet ������ �� �������� �������� ��� ���������� id
			if(ids_arr.length>1) {
				//���� �� ������� ������ ��� ��������� ����
				HashSet<BlockPos> result = new HashSet<>();
				if(list != null) {
					for (int i = 0; i < ids_arr.length; i++) {
						if(list.getNetwork(ids_arr[i]) != null) {
							//��������� ���� ���������� ���� �� ������
							result.addAll(list.getNetwork(ids_arr[i]).getParticipants());
							//� ������� ���� �.�. �� ����� ���������� � ��� ��� ����� �������
							list.removeNetwork(ids_arr[i]);
						}
					}
				}
				//������� ���� �� ���������
				int new_id = list.addNetwork(new EnergyNetwork(result));
				//� ���������� id ����� ����
				setNetworkId(world, pos, new_id, null);
			}
			//���� id ������ 1
			//TODO ������ ���������� �� facing_arr � ��������� ��� facing  � ����� id
			else if (!ids.isEmpty() && ids_arr.length == 1) {
				TileEntity tile = world.getTileEntity(pos);
				this_id = ids_arr[0];
				EnumFacing f = facing_arr[0];
				if(tile != null && tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participantTile = (NetworkParticipantTile) tile;
					participantTile.setNetworkId(this_id);
					
					//+���� ��� ��������� ��� ��������� ��������� � ��������� ����
					if(participantTile.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, f, true)
							&& participantTile.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, f, true)) {
						if(list != null && list.getNetwork(this_id) != null){
							list.getNetwork(this_id).getParticipants().add(pos);
						}
					}
				}
			}
			//���� �� ����� ����� ��� id � this_id(id ����, � ������� ������� ���� ��������� checkAround)
			if(!no_ids.isEmpty() && this_id != -1) {
				for (int i = 0; i < no_ids_arr.length; i++) {
					NetworkParticipantTile participantTile = no_ids_arr[i];
					EnumFacing facing = no_ids.get(participantTile);
					boolean generator_child = participantTile.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, facing, true);
					boolean storage_child = participantTile.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, facing, true);
					
					//��������� � ���� ���� ���������/���������
					if(generator_child || storage_child && list != null) {
						
						list.getNetwork(this_id).add(participantTile.getPos());
						setNetworkId(world, participantTile.getPos(), this_id, facing);
					}
					if(world.getBlockState(participantTile.getPos()).getBlock() instanceof Wire) {
						setNetworkId(world, participantTile.getPos(), this_id, facing);
					}
					//��������� ��������� id �.�. �� ���� ������ ����� ���� ��� ����� � ������������� id
					//setNetworkId(world, participantTile.getPos(), this_id, facing);
					//participantTile.setNetworkId(this_id);
				}
			}
		}
	}
	/**
	 * 
	 * @param world ���
	 * @param start ��������� �������
	 * @return ���������� ��������� ����������
	 */
	public static HashSet<BlockPos> checkNetwork(World world, BlockPos start) {
		
		/* <��� �� ��� � � buildNetwork>
		 * (�� ����������� ����, ��� �� ������ ���������� ��������� ����������, � �� ������� ��� ��� ����)  */
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
			if(tile != null && tile instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				
				generator = participant.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, null, false);
				storage = participant.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, null, false);
				
				if(generator || storage) {
					participants.add(nPos);
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {//������ ����������� �� ���� ��������� "������������"(�� ���� ��� ����� ���������
				BlockPos child = nPos.offset(face);//��� ������� ���������� ������� �� ������� � ������ �����������
				TileEntity tileEntity = world.getTileEntity(child);//�������� ����
				boolean generator_child = false;
				boolean storage_child = false;
				if(!checked.contains(child)) {
					if(tileEntity != null && tileEntity instanceof NetworkParticipantTile) {
						NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
						generator_child = participant.hasCapabilityAdvanced(EnergyGeneratorCapability.ENERGY_GENERATOR, face, true);
						storage_child = participant.hasCapabilityAdvanced(EnergyStorageCapability.ENERGY_STORAGE, face, true);
					}
					if(world.getBlockState(child).getBlock() instanceof Wire) {
						checked.add(child);
						queue.addLast(child);
					}
					if((storage || generator) && !storage_child && !generator_child) {
						if(tile != null && tile instanceof NetworkParticipantTile) {
							NetworkParticipantTile participant = (NetworkParticipantTile) tile;
							if(participant.canConnectTo(face.getOpposite())) {
								checked.add(child);
								queue.addLast(child);
							}
						}
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
		/* </��� �� ��� � � buildNetwork> */
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
	 * ���� ����� ��������� ���� �� � where �������� what
	 * @param where HashSet, � ������� ����� ����������� ������� ��������� what
	 * @param what HashSet, � �������� �������� ����� ����������� �� ������� � what 
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
	//� ����� breakBlock � �����
	public static void breakBlock(World worldIn,BlockPos pos) {
		EnergyNetworkList list = EnergyNetworkUtil.getEnergyNetworkList(worldIn);
		
		if(list != null) {
			TileEntity tile = worldIn.getTileEntity(pos);
			
			if(tile != null && tile instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				
				if(participant.hasNetworkId()) {
					EnergyNetwork network = list.getNetwork(participant.getNetworkId());
					
					if(network != null) {// �� ������
						network.remove(participant.getPos());
						if(!network.hasGenerators(worldIn)){
							list.removeNetwork(participant.getNetworkId());
							EnergyNetworkUtil.setNetworkId(worldIn, pos, -1, null);
						}
						
					}
				}
			
			}
		}
	}
	// � breakBlock
	public static void breakWire(World worldIn,BlockPos pos) {
		//���� �� �������� �������, ������������ � ���������� ����, ��� �� � ������ ������
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
				
				//������� tile �� ���������, ��� � ���� ���� id
				if(participant.hasNetworkId()) {
					boolean check = false;
					if(network != null && network.getParticipants() != null) {
						
						//� ������� ������ checkNetwork ��������� ����� ���� ��������� ����(����������/���������)
						HashSet<BlockPos> sub_network = checkNetwork(worldIn, p);
						//� ������� ������ contains ��������� ���� �� � ���� ������� ��� ��������� ������������
						check = contains(sub_network, network.getParticipants());
						
						//���� ���, �� �� ������ ��������� ��� ���� � ������ ��������
						//����� ���� ��������, ���� �� ������� � ������ �� �����������
						//��� �� ��������� �� ����������
						if(!check) {
							boolean equal_check = false;
							Iterator<Map.Entry<BlockPos, EnergyNetwork>> iterator = posses.entrySet().iterator();
							while (iterator.hasNext()) {
								Map.Entry<BlockPos, EnergyNetwork> entry = iterator.next();
								EnergyNetwork nEnergyNetwork = entry.getValue();
								
								BlockPos[] net = nEnergyNetwork.getParticipants().toArray(new BlockPos[nEnergyNetwork.getParticipants().size()]);
								BlockPos[] sub = sub_network.toArray(new BlockPos[sub_network.size()]);
								
								equal_check = Arrays.equals(net, sub);
								
								if(equal_check) {
									break;
								}
							}
							//���� ���� �� ��������� �� � ����� �� ����� ���������, �� ��������� ��
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
			//������� ������� ����
			list.removeNetwork(networkId);
			Iterator<Map.Entry<BlockPos, EnergyNetwork>> iterator = posses.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<BlockPos, EnergyNetwork> entry = iterator.next();
				EnergyNetwork nEnergyNetwork = entry.getValue();
				
				//���� ������� ����� ����������, ������ �� ����� �� �������� � ������ �����
				if(nEnergyNetwork.hasGenerators(worldIn)) {
					int id = list.addNetwork(nEnergyNetwork);
					//���������� id �� ����� ��������� ��������� �������
					BlockPos start = entry.getKey();
					EnergyNetworkUtil.setNetworkId(worldIn, start, id, null);
				}
				else {//���� ���� �� ����� ����������� - �� ������ ������ id -1
					BlockPos start = entry.getKey();
					EnergyNetworkUtil.setNetworkId(worldIn, start, -1, null);
				}
			}
		}
	}
}
