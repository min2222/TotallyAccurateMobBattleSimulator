package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

@Mixin(Minecraft.class)
public class MixinMinecraft 
{
	@ModifyReturnValue(method = "shouldEntityAppearGlowing", at = @At("RETURN"))
	private boolean tambs$shouldEntityAppearGlowing(boolean original, Entity pEntity)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			if(pEntity.getTeam() != null)
			{
				return true;
			}
			if(TAMBSClientData.SELECTED_UUID != null && TAMBSClientData.SELECTED_UUID == pEntity.getUUID())
			{
				return true;
			}
			if(TAMBSClientData.HOVERED_UUID != null && TAMBSClientData.HOVERED_UUID == pEntity.getUUID())
			{
				return true;
			}
		}
		return original;
	}
}
