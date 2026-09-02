package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.multiplayer.ClientLevel;

@Mixin(ClientLevel.class)
public class MixinClientLevel 
{
	@WrapMethod(method = "setDayTime")
	private void tambs$setDayTime(long pTime, Operation<Void> original)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			pTime = Long.valueOf(TAMBSClientData.INSTANCE.time);
		}
		original.call(pTime);
	}
}
