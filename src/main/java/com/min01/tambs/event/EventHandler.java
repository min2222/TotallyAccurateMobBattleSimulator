package com.min01.tambs.event;

import com.min01.tambs.TAMBS;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = TAMBS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler 
{
	@SubscribeEvent
	public static void onFMLCommonSetup(FMLCommonSetupEvent event)
	{
		event.enqueueWork(() -> 
		{
			
		});
	}
}
