package com.min01.tambs.util;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.min01.tambs.mixin.LevelInvoker;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class TAMBSUtil 
{
	public static void getClientLevel(Consumer<Level> consumer)
	{
		LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).filter(ClientLevel.class::isInstance).ifPresent(level -> 
		{
			consumer.accept(level);
		});
	}
	
	public static Direction getNearest(Vec3 start, Vec3 end)
	{
		Vec3 pos = start.subtract(end);
		return Direction.getNearest(pos.x, 0.0F, pos.z);
	}
	
	public static CompoundTag saveEntity(Entity entity)
	{
		CompoundTag tag = entity.saveWithoutId(new CompoundTag());
		tag.remove("Pos");
		tag.remove("Motion");
		tag.remove("Rotation");
		tag.remove("FallDistance");
		tag.remove("Fire");
		tag.remove("Air");
		tag.remove("OnGround");
		tag.remove("Invulnerable");
		tag.remove("PortalCooldown");
		tag.remove("UUID");
		if(tag.contains("ForgeCaps", 10))
		{
			CompoundTag caps = tag.getCompound("ForgeCaps");
			tag.put("ForgeCaps", caps.copy());
		}
		return tag;
	}
	
	public static void handlePacket(Supplier<NetworkEvent.Context> supplier, LogicalSide side, Consumer<NetworkEvent.Context> consumer)
	{
		NetworkEvent.Context ctx = supplier.get();
		ctx.enqueueWork(() ->
		{
			NetworkDirection direction = ctx.getDirection();
			LogicalSide receptionSide = direction.getReceptionSide();
			if(side.isClient() && !receptionSide.isClient())
			{
				return;
			}
			if(side.isServer() && !receptionSide.isServer())
			{
				return;
			}
			consumer.accept(ctx);
		});
		ctx.setPacketHandled(true);
	}
	
	public static LevelEntityGetter<Entity> getEntityGetter(Level level)
	{
		return ((LevelInvoker) level).tambs$invoke_getEntities();
	}
	
	public static Entity getEntityByUUID(Level level, UUID uuid)
	{
		LevelEntityGetter<Entity> getter = getEntityGetter(level);
		return getter.get(uuid);
	}
}
