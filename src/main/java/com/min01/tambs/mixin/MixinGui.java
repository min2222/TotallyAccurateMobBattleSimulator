package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@Mixin(Gui.class)
public class MixinGui 
{
	@WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
	private void tambs$renderCrosshair(GuiGraphics instance, ResourceLocation pAtlasLocation, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight, Operation<Void> original)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			pAtlasLocation = TAMBSClientData.isPaused() ? TAMBSClientUtil.PAUSED_ICON : TAMBSClientUtil.PLAY_ICON;
		}
		original.call(instance, pAtlasLocation, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight);
	}
}
