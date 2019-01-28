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
			if(tile != null && tile instanceof NetworkParticipant) {
				NetworkParticipant participant = (NetworkParticipant) tile;
				participant.setNetworkId(id);
			}
			for (EnumFacing face : EnumFacing.VALUES) {//������ ����������� �� ���� ��������� "������������"(�� ���� ��� ����� ���������
				BlockPos child = nPos.offset(face);//��� ������� ���������� ������� �� ������� � ������ �����������
				TileEntity tileEntity = world.getTileEntity(child);//�������� ����
				if(tileEntity != null && tileEntity instanceof NetworkParticipant) {
					NetworkParticipant participant = (NetworkParticipant) tileEntity;
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
