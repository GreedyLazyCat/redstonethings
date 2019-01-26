package com.greedycat.redstonethings.network;

import com.greedycat.redstonethings.tile.RedBetterEnchTile;
import com.greedycat.redstonethings.tile.RedForgeTile;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RedBetterEnchMessage implements IMessage{
	
	private int energy_to_send;
	private int x;
	private int y;
	private int z;
	
	public RedBetterEnchMessage() {
		// TODO Auto-generated constructor stub
	}
	public RedBetterEnchMessage(int energy,BlockPos pos){
		this.energy_to_send = energy;
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		energy_to_send = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energy_to_send);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	public static class EnchHandler implements IMessageHandler<RedBetterEnchMessage, IMessage> {
		@Override
		public IMessage onMessage(RedBetterEnchMessage message, MessageContext ctx) {
			
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				
				@Override
				public void run() {
					processMessage(message, Minecraft.getMinecraft().world);
				}
			});
			
			return null;
		}
		public void processMessage(RedBetterEnchMessage message, WorldClient world) {
			TileEntity tile = world.getTileEntity(new BlockPos(message.x, message.y, message.z));
			if(tile instanceof RedBetterEnchTile) {
				RedBetterEnchTile redForgeTile = (RedBetterEnchTile) tile;
				redForgeTile.setChanges(message.energy_to_send);
			}
		}
    }

}
