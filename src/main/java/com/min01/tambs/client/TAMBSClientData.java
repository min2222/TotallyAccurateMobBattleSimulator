package com.min01.tambs.client;

import java.util.UUID;

import com.min01.tambs.client.TAMBSReloadListener.Options;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;

public class TAMBSClientData 
{
	public static Options INSTANCE = new Options();
	public static boolean PAUSED = true;

	public static BlockPos LAST_PLACED;
	public static EntityType<?> SELECTED_TYPE;
	public static UUID SELECTED_UUID;
	
	public static void selectType(EntityType<?> type)
	{
		SELECTED_TYPE = type;
	}
	
	public static void selectUUID(UUID uuid)
	{
		SELECTED_UUID = uuid;
	}
	
	public static void release()
	{
		LAST_PLACED = null;
	}
	
	public static void clear()
	{
		PAUSED = true;
		LAST_PLACED = null;
		SELECTED_TYPE = null;
		SELECTED_UUID = null;
	}
	
	public static void pause(boolean paused)
	{
		PAUSED = paused;
	}
	
	public static boolean isPaused()
	{
		return PAUSED;
	}
}
