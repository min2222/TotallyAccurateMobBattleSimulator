package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.player.KeyboardInput;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput
{
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
	private boolean tambs$tick(KeyMapping instance, Operation<Boolean> original)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.screen instanceof TAMBSScreen screen && !(screen.getFocused() instanceof EditBox))
		{
			return TAMBSClientUtil.isKeyDown(instance);
		}
		return original.call(instance);
	}
}
