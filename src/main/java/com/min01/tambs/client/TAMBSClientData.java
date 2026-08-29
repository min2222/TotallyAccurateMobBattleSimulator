package com.min01.tambs.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;

public class TAMBSClientData 
{
	public static EntityType<?> SELECTED;
	public static BlockPos LAST_PLACED = null;
	public static boolean PAUSED = true;
	
	public static void select(EntityType<?> type)
	{
		SELECTED = type;
	}
	
	public static void release()
	{
		LAST_PLACED = null;
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
