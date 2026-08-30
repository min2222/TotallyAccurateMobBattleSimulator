package com.min01.tambs.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

@Mixin(Minecraft.class)
public class MixinMinecraft 
{
	@Nullable
	@Shadow
	public LocalPlayer player;
	
	@Nullable
	@Shadow
	public ClientLevel level;
	
	@Shadow
	private volatile boolean pause;
	
	@Unique
	private final Timer tambs$timer = new Timer(20.0F, 0L);

	@ModifyReturnValue(method = "shouldEntityAppearGlowing", at = @At("RETURN"))
	private boolean tambs$shouldEntityAppearGlowing(boolean original, Entity pEntity)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
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
