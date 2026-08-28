package com.min01.tambs.event;

import org.joml.Vector4f;

import com.min01.tambs.TAMBS;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.misc.TAMBSKeyMappings;
import com.min01.tambs.util.TAMBSClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TAMBS.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandlerForge
{
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Phase phase = event.phase;
		if(phase != Phase.END)
			return;
		while(TAMBSKeyMappings.ENTER_MOB_BATTLE_MODE.consumeClick())
		{
			minecraft.setScreen(new TAMBSScreen());
		}
	}
	
	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent event)
	{
		if(TAMBSClientUtil.isMobBattleMode())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onRenderLevelStage(RenderLevelStageEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Stage stage = event.getStage();
		Camera camera = event.getCamera();
		PoseStack stack = event.getPoseStack();
		Vec3 camPos = camera.getPosition();
		MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
	    if(stage == Stage.AFTER_TRANSLUCENT_BLOCKS && TAMBSClientUtil.isMobBattleMode() && !TAMBSClientUtil.isCameraMoving()) 
	    {
	        HitResult hitResult = TAMBSClientUtil.raycastBlockFromMouse(300.0);
	        if(hitResult instanceof BlockHitResult blockHit) 
	        {
	            BlockPos blockPos = blockHit.getBlockPos();
	            BlockState blockState = minecraft.level.getBlockState(blockPos);
	            if(!blockState.isAir())
	            {
	            	ResourceLocation location = ResourceLocation.fromNamespaceAndPath(TAMBS.MODID, "textures/misc/white.png");
	                VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(location));
	                VoxelShape shape = blockState.getShape(minecraft.level, blockPos, CollisionContext.of(camera.getEntity()));
        	    	stack.pushPose();
        	    	stack.translate(blockPos.getX() - camPos.x, blockPos.getY() - camPos.y, blockPos.getZ() - camPos.z);
	        	    for(AABB aabb : shape.toAabbs())
	        	    {
	        	    	TAMBSClientUtil.drawBox(aabb.inflate(0.01F), stack, consumer, new Vector4f(1.0F, 1.0F, 1.0F, 0.5F), LightTexture.FULL_BLOCK);
	        	    }
        	    	stack.popPose();
        	    	
        	    	stack.pushPose();
        	    	Vec3 pos = Vec3.atBottomCenterOf(blockPos.above());
        	    	stack.translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z);
        	    	if(TAMBSClientData.SELECTED != null)
        	    	{
        	    		Entity entity = TAMBSClientData.SELECTED.create(minecraft.level);
        				Vec3 relative = minecraft.player.position().subtract(pos);
        				Direction direction = Direction.getNearest(relative.x, relative.y, relative.z);
        	    		EntityRenderer<? super Entity> renderer = entityRenderDispatcher.getRenderer(entity);
        	    		
        				stack.mulPose(Axis.YN.rotationDegrees(direction.toYRot()));
    	    			renderer.render(entity, 0.0F, 0.0F, stack, bufferSource, LightTexture.FULL_BLOCK);
        	    	}
        	    	stack.popPose();
	            }
	        }
	    }
	}
}
