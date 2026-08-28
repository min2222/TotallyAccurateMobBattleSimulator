package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;

@Mixin(MouseHandler.class)
public class MixinMouseHandler
{
	@WrapOperation(method = "grabMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"))
	private void tambs$grabMouse(Minecraft instance, Screen pGuiScreen, Operation<Void> original)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			return;
		}
		original.call(instance, pGuiScreen);
	}
}
