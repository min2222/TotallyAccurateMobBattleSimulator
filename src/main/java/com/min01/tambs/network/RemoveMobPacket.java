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
	
	public RemoveMobPacket(UUID entityUUID) 
	{
		this.entityUUID = entityUUID;
	}

	public static RemoveMobPacket read(FriendlyByteBuf buf)
	{
		return new RemoveMobPacket(buf.readUUID());
	}

	public void write(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.entityUUID);
	}
	
	public static boolean handle(RemoveMobPacket message, Supplier<NetworkEvent.Context> supplier) 
	{
		TAMBSUtil.handlePacket(supplier, LogicalSide.SERVER, ctx ->
		{
			ServerPlayer sender = ctx.getSender();
			Entity entity = TAMBSUtil.getEntityByUUID(sender.level, message.entityUUID);
			if(entity instanceof LivingEntity living)
			{
				living.discard();
			}
		});
		return true;
	}
}
