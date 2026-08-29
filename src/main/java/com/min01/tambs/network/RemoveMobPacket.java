package com.min01.tambs.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class RemoveMobPacket 
{
    private final UUID entityUUID;
    private final boolean isCtrlDown;
	
	public RemoveMobPacket(UUID entityUUID, boolean isCtrlDown) 
	{
		this.entityUUID = entityUUID;
		this.isCtrlDown = isCtrlDown;
	}

	public static RemoveMobPacket read(FriendlyByteBuf buf)
	{
		return new RemoveMobPacket(buf.readUUID(), buf.readBoolean());
	}

	public void write(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.entityUUID);
		buf.writeBoolean(this.isCtrlDown);
	}
	
	public static boolean handle(RemoveMobPacket message, Supplier<NetworkEvent.Context> supplier) 
	{
		TAMBSUtil.handlePacket(supplier, LogicalSide.SERVER, ctx ->
		{
			ServerPlayer sender = ctx.getSender();
			Entity entity = TAMBSUtil.getEntityByUUID(sender.level, message.entityUUID);
			if(message.isCtrlDown)
			{
				entity.discard();
			}
			else if(entity instanceof LivingEntity living)
			{
				living.discard();
			}
		});
		return true;
	}
}
