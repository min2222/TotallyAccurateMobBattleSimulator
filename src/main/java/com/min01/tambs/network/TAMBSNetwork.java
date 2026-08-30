package com.min01.tambs.network;

import com.min01.tambs.TAMBS;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public class TAMBSNetwork 
{
	public static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(TAMBS.MODID, TAMBS.MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	
	public static void registerMessages()
	{
		int id = 0;
		CHANNEL.registerMessage(id++, PlaceMobPacket.class, PlaceMobPacket::write, PlaceMobPacket::read, PlaceMobPacket::handle);
		CHANNEL.registerMessage(id++, RemoveMobPacket.class, RemoveMobPacket::write, RemoveMobPacket::read, RemoveMobPacket::handle);
		CHANNEL.registerMessage(id++, MoveMobPacket.class, MoveMobPacket::write, MoveMobPacket::read, MoveMobPacket::handle);
	}
	
    public static <MSG> void sendToServer(MSG message) 
    {
    	CHANNEL.sendToServer(message);
    }
    
    public static <MSG> void sendToAll(MSG message)
    {
    	for(ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) 
    	{
    		sendToPlayer(message, player);
    	}
    }
    
    public static <MSG> void sendToPlayer(MSG msg, ServerPlayer player) 
    {
        CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
