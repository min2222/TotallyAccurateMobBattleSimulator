package com.min01.tambs.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class AddTeamPacket 
{
    private final UUID entityUUID;
    private final String teamName;
    private final ChatFormatting color;
	
	public AddTeamPacket(UUID entityUUID, String teamName, ChatFormatting color) 
	{
		this.entityUUID = entityUUID;
		this.teamName = teamName;
		this.color = color;
	}

	public static AddTeamPacket read(FriendlyByteBuf buf)
	{
		return new AddTeamPacket(buf.readUUID(), buf.readUtf(), ChatFormatting.values()[buf.readInt()]);
	}

	public void write(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.entityUUID);
		buf.writeUtf(this.teamName);
		buf.writeInt(this.color.ordinal());
	}
	
	public static boolean handle(AddTeamPacket message, Supplier<NetworkEvent.Context> supplier) 
	{
		TAMBSUtil.handlePacket(supplier, LogicalSide.SERVER, ctx ->
		{
			ServerPlayer sender = ctx.getSender();
			Entity entity = TAMBSUtil.getEntityByUUID(sender.level, message.entityUUID);
			if(entity != null)
			{
		        Scoreboard scoreboard = sender.level.getScoreboard();
		        PlayerTeam team = scoreboard.getPlayerTeam(message.teamName);
		        if(team == null) 
		        {
		        	team = scoreboard.addPlayerTeam(message.teamName);
		        	team.setAllowFriendlyFire(false);
		        	team.setCollisionRule(CollisionRule.PUSH_OTHER_TEAMS);
		        }
	        	team.setColor(message.color);
		        scoreboard.addPlayerToTeam(entity.getStringUUID(), team);
			}
		});
		return true;
	}
}
