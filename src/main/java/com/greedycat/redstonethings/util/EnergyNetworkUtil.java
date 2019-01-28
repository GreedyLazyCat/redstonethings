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
			if(tile != null && tile instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				/*
				if(participant.hasNetworkId()) {
					if(world.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
						EnergyNetworkList list = world.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
						list.removeNetwork(participant.getNetworkId());
					}
				}*/
				
				participant.setNetworkId(id);
			}
			for (EnumFacing face : EnumFacing.VALUES) {//������ ����������� �� ���� ��������� "������������"(�� ���� ��� ����� ���������
				BlockPos child = nPos.offset(face);//��� ������� ���������� ������� �� ������� � ������ �����������
				TileEntity tileEntity = world.getTileEntity(child);//�������� ����
				if(tileEntity != null && tileEntity instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tileEntity;
					participant.setNetworkId(id);
				}
				//���� � ������ ����������� ���� ���� ������� � ���� �� ���� ������� - ��� ������, ��������� � ������
				//����������� + � �������, ����� � ���� ������� ��������� ��� ������ �����
				if(!checked.contains(child) && world.getBlockState(child).getBlock() instanceof Wire) {
					checked.add(child);
					queue.addLast(child);//��������� � ��� �������
				}
			}
		}
	}
	
	public static void checkAround(World world, BlockPos pos) {
		ArrayList<Integer> ids = new ArrayList(1);
		for(EnumFacing facing : EnumFacing.VALUES) {
			BlockPos child = pos.offset(facing);
			TileEntity tile = world.getTileEntity(child);
			if(tile != null && tile instanceof NetworkParticipantTile) {
				NetworkParticipantTile participant = (NetworkParticipantTile) tile;
				if(participant.hasNetworkId()) ids.add(participant.getNetworkId());
			}
		}
		if(ids.size()>1) {
			System.out.println("IDs have more than one element");
			buildNetworkNew(world, pos);
		}
		else if (!ids.isEmpty() && ids.size() == 1) {
			System.out.println("IDs have only one element");
			TileEntity tile = world.getTileEntity(pos);
			if(tile != null) {
				if(tile instanceof NetworkParticipantTile) {
					NetworkParticipantTile participant = (NetworkParticipantTile) tile;
					participant.setNetworkId(ids.get(0));
				}
				if(tile.hasCapability(EnergyGeneratorCapability.ENERGY_GENERATOR, null) || 
						tile.hasCapability(EnergyStorageCapability.ENERGY_STORAGE, null)) {
					if(world.hasCapability(EnergyNetworkListCapability.NETWORK_LIST, null)) {
						EnergyNetworkList list = world.getCapability(EnergyNetworkListCapability.NETWORK_LIST, null);
						if(list.getNetwork(ids.get(0)) != null){
							list.getNetwork(ids.get(0)).getParticipants().add(pos);
						}
					}
				}
			}
		}
		else if(ids.isEmpty()){
			System.out.println("IDs is empty");
			buildNetworkNew(world, pos);
		}
	}
}
