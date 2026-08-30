package com.min01.tambs.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.Timer;

@Mixin(Timer.class)
public class MixinTimer
{
	@ModifyExpressionValue(method = "advanceTime", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Timer;msPerTick:F", opcode = Opcodes.GETFIELD))
	private float tambs$advanceTime(float original)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			float tickrate = Float.valueOf(TAMBSClientData.INSTANCE.play_speed) * 20.0F;
			return 1000.0F / tickrate;
		}
		return original;
	}
}
