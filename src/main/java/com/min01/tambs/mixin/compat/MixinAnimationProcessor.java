package com.min01.tambs.mixin.compat;

import java.io.PrintStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.min01.tambs.util.TAMBSClientUtil;

@Mixin(targets = "software.bernie.geckolib.core.animation.AnimationProcessor", remap = false)
public class MixinAnimationProcessor 
{
	//fix log spam for mcreator mods that use geckolib, which doesn't have animation path data on SynchedEntityDataAccessor while rendered in UI;
	@WrapOperation(method = "buildAnimationQueue", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
	private void tambs$buildAnimationQueue(PrintStream instance, String x, Operation<Void> original)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			return;
		}
		original.call(instance, x);
	}
}
