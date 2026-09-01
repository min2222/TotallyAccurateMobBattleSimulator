package com.min01.tambs.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class RemoveTeamPacket 
{
    private final UUID entityUUID;
	
	public RemoveTeamPacket(UUID entityUUID) 
	{
		this.entityUUID = entityUUID;
	}

	public static RemoveTeamPacket read(FriendlyByteBuf buf)
	{
		return new RemoveTeamPacket(buf.readUUID());
	}

	public void write(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.entityUUID);
	}
	
	public static boolean handle(RemoveTeamPacket message, Supplier<NetworkEvent.Context> supplier) 
	{
		TAMBSUtil.handlePacket(supplier, LogicalSide.SERVER, ctx ->
		{
			ServerPlayer sender = ctx.getSender();
			Entity entity = TAMBSUtil.getEntityByUUID(sender.level, message.entityUUID);
			if(entity != null)
			{
		        Scoreboard scoreboard = sender.level.getScoreboard();
		        scoreboard.removePlayerFromTeam(entity.getStringUUID());
			}
		});
		return true;
	}
}
