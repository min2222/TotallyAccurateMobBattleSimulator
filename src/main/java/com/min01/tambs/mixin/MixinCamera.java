package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;

@Mixin(Camera.class)
public class MixinCamera
{
	@WrapMethod(method = "setup")
	private void tambs$setup(BlockGetter pLevel, Entity pEntity, boolean pDetached, boolean pThirdPersonReverse, float pPartialTick, Operation<Void> original)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			pPartialTick = Minecraft.getInstance().getFrameTime();
		}
		original.call(pLevel, pEntity, pDetached, pThirdPersonReverse, pPartialTick);
	}
}
