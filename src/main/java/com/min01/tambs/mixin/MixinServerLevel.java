package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.min01.tambs.client.TAMBSClientData;

import net.minecraft.server.level.ServerLevel;

@Mixin(ServerLevel.class)
public class MixinServerLevel 
{
	@WrapMethod(method = "setDayTime")
	private void tambs$setDayTime(long pTime, Operation<Void> original)
	{
		if(TAMBSClientData.MOBBATTLE_MODE)
		{
			pTime = Long.valueOf(TAMBSClientData.INSTANCE.time);
		}
		original.call(pTime);
	}
}
