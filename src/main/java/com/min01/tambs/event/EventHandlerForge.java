package com.min01.tambs.event;

import com.min01.tambs.TAMBS;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = TAMBS.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandlerForge 
{
	@SubscribeEvent
	public static void onMobEffectApplicable(MobEffectEvent.Applicable event)
	{
		MobEffectInstance instance = event.getEffectInstance();
		MobEffect effect = instance.getEffect();
		if(TAMBSClientUtil.isMobBattleMode())
		{
			if(effect != null && TAMBSClientData.INSTANCE.mob_effect.contains(ForgeRegistries.MOB_EFFECTS.getKey(effect).toString()))
			{
				event.setResult(Result.DENY);
			}
		}
	}
}