package com.min01.tambs.network;

import java.util.function.Supplier;

import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class PlaceMobPacket 
{
    private final ResourceLocation entityName;
    private final CompoundTag tag;
    private final BlockPos blockPos;
	
	public PlaceMobPacket(ResourceLocation entityName, CompoundTag tag, BlockPos blockPos) 
	{
		this.entityName = entityName;
		this.tag = tag;
		this.blockPos = blockPos;
	}

	public static PlaceMobPacket read(FriendlyByteBuf buf)
	{
		return new PlaceMobPacket(buf.readResourceLocation(), buf.readNbt(), buf.readBlockPos());
	}

	public void write(FriendlyByteBuf buf)
	{
		buf.writeResourceLocation(this.entityName);
		buf.writeNbt(this.tag);
		buf.writeBlockPos(this.blockPos);
	}
	
	public static boolean handle(PlaceMobPacket message, Supplier<NetworkEvent.Context> supplier) 
	{
		TAMBSUtil.handlePacket(supplier, LogicalSide.SERVER, ctx ->
		{
			ServerPlayer sender = ctx.getSender();
			EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(message.entityName);
			Entity entity = type.create(sender.level);
			if(entity instanceof Mob mob)
			{
				ForgeEventFactory.onFinalizeSpawn(mob, (ServerLevelAccessor) sender.level, sender.level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWN_EGG, null, null);
			}
			if(message.tag != null)
			{
				entity.load(message.tag);
			}
			entity.setPos(Vec3.atBottomCenterOf(message.blockPos));
			Direction direction = TAMBSUtil.getNearest(sender.position(), entity.position());
			entity.setYRot(direction.toYRot());
			entity.setYHeadRot(direction.toYRot());
			entity.setYBodyRot(direction.toYRot());
			sender.level.addFreshEntity(entity);
		});
		return true;
	}
}
