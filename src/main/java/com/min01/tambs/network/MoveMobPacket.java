package com.min01.tambs.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class MoveMobPacket 
{
    private final UUID entityUUID;
    private final BlockPos blockPos;
	
	public MoveMobPacket(UUID entityUUID, BlockPos blockPos) 
	{
		this.entityUUID = entityUUID;
		this.blockPos = blockPos;
	}

	public static MoveMobPacket read(FriendlyByteBuf buf)
	{
		return new MoveMobPacket(buf.readUUID(), buf.readBlockPos());
	}

	public void write(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.entityUUID);
		buf.writeBlockPos(this.blockPos);
	}
	
	public static boolean handle(MoveMobPacket message, Supplier<NetworkEvent.Context> supplier) 
	{
		TAMBSUtil.handlePacket(supplier, LogicalSide.SERVER, ctx ->
		{
			ServerPlayer sender = ctx.getSender();
			Entity entity = TAMBSUtil.getEntityByUUID(sender.level, message.entityUUID);
			if(entity != null)
			{
				entity.setPos(Vec3.atBottomCenterOf(message.blockPos));
				entity.setOldPosAndRot();
			}
		});
		return true;
	}
}
