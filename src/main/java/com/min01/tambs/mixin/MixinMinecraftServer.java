package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer 
{
	@ModifyExpressionValue(method = "runServer", at = @At(value = "CONSTANT", args = "longValue=50"))
	private long tambs$runServer(long original)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			long tickrate = (long) (Float.valueOf(TAMBSClientData.INSTANCE.play_speed) * 20L);
			return 1000L / tickrate;
		}
		return original;
	}
}
