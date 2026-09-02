package com.min01.tambs.client;

import java.util.UUID;

import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.min01.tambs.gui.components.MobCell;

import net.minecraft.core.BlockPos;

public class TAMBSClientData 
{
	public static Options INSTANCE = new Options();

	public static String SPEED = "1.0";
	public static boolean PAUSED = true;
	public static boolean MOBBATTLE_MODE;

	public static BlockPos LAST_PLACED;
	public static BlockPos LAST_HOVERED;
	public static MobCell SELECTED_CELL;
	public static UUID SELECTED_UUID;
	public static UUID HOVERED_UUID;
	
	public static void selectCell(MobCell cell)
	{
		SELECTED_CELL = cell;
	}
	
	public static void selectUUID(UUID uuid)
	{
		SELECTED_UUID = uuid;
	}
	
	public static void release()
	{
		LAST_PLACED = null;
		LAST_HOVERED = null;
	}
	
	public static void clear()
	{
		LAST_PLACED = null;
		LAST_HOVERED = null;
		SELECTED_CELL = null;
		SELECTED_UUID = null;
		HOVERED_UUID = null;
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
