package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(Entity.class)
public class MixinEntity
{
	@WrapMethod(method = "tick")
	private void tambs$tick(Operation<Void> original)
	{
		Entity entity = (Entity) (Object) this;
		original.call();
		if(!(entity instanceof Player))
		{
			if(TAMBSClientUtil.isMobBattleMode() && TAMBSClientData.INSTANCE.mob_kill.filter(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType())))
			{
				entity.kill();
			}
		}
	}
}
