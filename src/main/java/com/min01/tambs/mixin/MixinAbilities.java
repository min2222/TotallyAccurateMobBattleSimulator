package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.world.entity.player.Abilities;

@Mixin(Abilities.class)
public class MixinAbilities
{
	@ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
	private float tambs$getFlyingSpeed(float original)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			original *= Float.valueOf(TAMBSClientData.INSTANCE.fly_speed);
		}
		return original;
	}
}
