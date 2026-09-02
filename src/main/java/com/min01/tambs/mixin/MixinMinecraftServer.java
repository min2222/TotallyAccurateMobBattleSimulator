package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.min01.tambs.client.TAMBSClientData;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer 
{
	@ModifyExpressionValue(method = "runServer", at = @At(value = "CONSTANT", args = "longValue=50"))
	private long tambs$runServer(long original)
	{
		if(TAMBSClientData.MOBBATTLE_MODE && !TAMBSClientData.isPaused())
		{
			long tickrate = (long) (Float.valueOf(TAMBSClientData.SPEED) * 20L);
			return Math.min(1000L / Math.max(tickrate, 0L), 1000L);
		}
		return original;
	}
}
