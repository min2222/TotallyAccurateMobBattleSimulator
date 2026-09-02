package com.min01.tambs.event;

import org.joml.Vector4f;

import com.min01.tambs.TAMBS;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.client.TAMBSReloadListener;
import com.min01.tambs.gui.components.MobCell;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.gui.tab.TAMBSTab;
import com.min01.tambs.misc.TAMBSKeyMappings;
import com.min01.tambs.util.TAMBSClientUtil;
import com.min01.tambs.util.TAMBSUtil;
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
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

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
            minecraft.player.setDeltaMovement(Vec3.ZERO);
			TAMBSClientData.pause(true);
			TAMBSClientData.MOBBATTLE_MODE = true;
		}
	}
	
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event)
    {
		TAMBSClientData.MOBBATTLE_MODE = false;
		TAMBSClientData.pause(true);
    	TAMBSClientData.clear();
    	TAMBSReloadListener.save(FMLPaths.CONFIGDIR.get());
    }
	
	@SubscribeEvent
	public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event)
	{
		NamedGuiOverlay overlay = event.getOverlay();
		if(TAMBSClientUtil.isMobBattleMode() && overlay == VanillaGuiOverlay.HOTBAR.type())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.screen instanceof TAMBSScreen)
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
	    if(stage == Stage.AFTER_TRANSLUCENT_BLOCKS && TAMBSClientUtil.isMobBattleMode() && !TAMBSClientUtil.isCameraMoving() && TAMBSClientData.isPaused()) 
	    {
	    	if(minecraft.screen instanceof TAMBSScreen screen && screen.getCurrentTab() instanceof TAMBSTab tab)
	    	{
		        HitResult hitResult = TAMBSClientUtil.raycastBlock(Double.valueOf(TAMBSClientData.INSTANCE.mouse_distance));
		        if(hitResult instanceof BlockHitResult blockHit) 
		        {
		            BlockPos blockPos = blockHit.getBlockPos();
		            BlockState blockState = minecraft.level.getBlockState(blockPos);
		            if(!blockState.isAir())
		            {
	        	    	if(tab.renderBlockHighlight())
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
	        	    	}
	        	    	
	        	    	if(tab.renderEntityPreview())
	        	    	{
		        	    	stack.pushPose();
		        	    	Vec3 pos = Vec3.atBottomCenterOf(blockPos.above());
		        	    	stack.translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z);
		        	    	MobCell cell = TAMBSClientData.SELECTED_CELL;
		        	    	if(cell != null)
		        	    	{
		        	    		Entity entity = cell.entity.getType().create(minecraft.level);
		        	    		if(cell.tag != null)
		        	    		{
		        	    			entity.load(cell.tag);
		        	    		}
		        	    		EntityRenderer<? super Entity> renderer = entityRenderDispatcher.getRenderer(entity);
		        				Direction direction = TAMBSUtil.getNearest(minecraft.player.position(), pos);
		        	    		
		        				stack.mulPose(Axis.YN.rotationDegrees(direction.toYRot()));
		    	    			renderer.render(entity, 0.0F, 0.0F, stack, bufferSource, LightTexture.FULL_BLOCK);
		        	    	}
		        	    	stack.popPose();
	        	    	}
		            }
		        }
	    	}
	    }
	}
}
