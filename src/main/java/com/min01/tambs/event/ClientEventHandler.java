package com.min01.tambs.event;

import com.min01.tambs.TAMBS;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TAMBS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler 
{
    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event)
    {
    	event.enqueueWork(() -> 
    	{
    		
    	});
    }
}
