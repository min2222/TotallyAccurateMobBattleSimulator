package com.min01.tambs.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.components.MobCell;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.network.PlaceMobPacket;
import com.min01.tambs.network.RemoveMobPacket;
import com.min01.tambs.network.TAMBSNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class TAMBSClientUtil 
{
	public static boolean isMobBattleMode()
	{
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.screen instanceof TAMBSScreen;
	}
	
	public static boolean isHidden()
	{
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft.screen instanceof TAMBSScreen screen && screen.isHidden();
	}
	
	public static String getTickrate()
	{
		Minecraft minecraft = Minecraft.getInstance();
		if(isMouseDown(minecraft.options.keyAttack))
		{
			return TAMBSClientData.INSTANCE.slow_motion_speed;
		}
		else if(isMouseDown(minecraft.options.keyUse))
		{
			return TAMBSClientData.INSTANCE.fast_motion_speed;
		}
		return TAMBSClientData.INSTANCE.play_speed;
	}
	
	public static boolean isCameraMoving()
	{
		if(!TAMBSClientData.isPaused())
		{
			return true;
		}
		Minecraft minecraft = Minecraft.getInstance();
		return isMouseDown(minecraft.options.keyPickItem);
	}
	
	public static boolean isMouseDown(KeyMapping key)
	{
		Minecraft minecraft = Minecraft.getInstance();
		return GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(), key.getKey().getValue()) == GLFW.GLFW_PRESS;
	}
	
	public static boolean isKeyDown(KeyMapping key)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Window window = minecraft.getWindow();
		return InputConstants.isKeyDown(window.getWindow(), key.getKey().getValue());
	}
	
	public static void hover()
	{
		if(!TAMBSClientData.isPaused())
		{
			return;
		}
        raycast(TAMBSClientData.LAST_HOVERED, t -> 
        {
        	if(t instanceof LivingEntity living)
        	{
    			TAMBSClientData.HOVERED_UUID = living.getUUID();
            	TAMBSClientData.LAST_HOVERED = living.blockPosition();
        	}
        }, t ->
        {
        	TAMBSClientData.HOVERED_UUID = null;
        });
	}
	
	public static void raycast(Consumer<Entity> entityConsumer, Consumer<BlockHitResult> blockConsumer)
	{
		raycast(TAMBSClientData.LAST_PLACED, entityConsumer, blockConsumer);
	}
	
	public static void raycast(BlockPos last, Consumer<Entity> entityConsumer, Consumer<BlockHitResult> blockConsumer)
	{
        HitResult hitResult = TAMBSClientUtil.raycast(Double.valueOf(TAMBSClientData.INSTANCE.mouse_distance), true);
        if(hitResult instanceof EntityHitResult entityHit)
        {
    		Entity entity = entityHit.getEntity();
        	if(last == null || !entity.blockPosition().equals(last))
        	{
        		entityConsumer.accept(entity);
        	}
        }
        else if(hitResult instanceof BlockHitResult blockHit)
        {
        	blockConsumer.accept(blockHit);
        }
	}
	
	public static void placeOrRemoveMob(int button)
	{
		if(!TAMBSClientData.isPaused())
		{
			return;
		}
        HitResult hitResult = raycast(Double.valueOf(TAMBSClientData.INSTANCE.mouse_distance), true);
    	if(button == 0)
    	{
            if(hitResult instanceof BlockHitResult blockHit)
            {
                BlockPos blockPos = blockHit.getBlockPos();
                Direction direction = blockHit.getDirection();
                blockPos = blockPos.relative(direction);

                MobCell cell = TAMBSClientData.SELECTED_CELL;
                if(cell != null && !blockPos.equals(TAMBSClientData.LAST_PLACED))
                {
                    TAMBSNetwork.sendToServer(new PlaceMobPacket(ForgeRegistries.ENTITY_TYPES.getKey(cell.entity.getType()), cell.tag, blockPos));
                    TAMBSClientData.LAST_PLACED = blockPos;
                }
            }
    	}
    	else if(button == 1)
    	{
            if(hitResult instanceof EntityHitResult entityHit)
            {
                TAMBSNetwork.sendToServer(new RemoveMobPacket(entityHit.getEntity().getUUID(), TAMBSClientUtil.isKeyDown(Minecraft.getInstance().options.keySprint)));
            }
    	}
	}
	
    public static void drawBox(AABB boundingBox, PoseStack stack, VertexConsumer consumer, Vector4f color, int light) 
    {
        Matrix4f matrix4f = stack.last().pose();
        float maxX = (float) boundingBox.maxX * 0.625F;
        float minX = (float) boundingBox.minX * 0.625F;
        float maxY = (float) boundingBox.maxY * 0.625F;
        float minY = (float) boundingBox.minY * 0.625F;
        float maxZ = (float) boundingBox.maxZ * 0.625F;
        float minZ = (float) boundingBox.minZ * 0.625F;

        float maxU = maxZ - minZ;
        float maxV = maxY - minY;
        float minU = minZ - maxZ;
        float minV = minY - maxY;
        
        // X+
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, 0.0F, 0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, 0.0F, 0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, 0.0F, 0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, 0.0F, 0F).endVertex();

        // X-
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, 0.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, 0.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, 0.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, 0.0F, 0.0F).endVertex();

        maxU = maxX - minX;
        maxV = maxY - minY;
        minU = minX - maxX;
        minV = minY - maxY;
        
        // Z-
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, -1.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, -1.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, -1.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, -1.0F).endVertex();

        // Z+
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, 1.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, 1.0F).endVertex();

        maxU = maxZ - minZ;
        maxV = maxX - minX;
        minU = minZ - maxZ;
        minV = minX - maxX;
        
        // Y+
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 1.0F, 0.0F).endVertex();

        // Y-
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, -1.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, -1.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color.x, color.y, color.z, color.w).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, -1.0F, 0.0F).endVertex();
        consumer.vertex(matrix4f, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color.x, color.y, color.z, color.w).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, -1.0F, 0.0F).endVertex();
    }

    public static List<Entity> getEntities(double startX, double startY, double endX, double endY, double maxDistance)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null || minecraft.player == null)
        {
        	return new ArrayList<>();
        }
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        
        double width = minecraft.getWindow().getGuiScaledWidth();
        double height = minecraft.getWindow().getGuiScaledHeight();

        AABB broadBox = new AABB(cameraPos, cameraPos).inflate(maxDistance);
        List<Entity> potentialEntities = minecraft.level.getEntities(minecraft.player, broadBox, Entity::isAlive);

        float fov = minecraft.options.fov().get().floatValue();
        Matrix4f projectionMatrix = minecraft.gameRenderer.getProjectionMatrix(fov);
        
        Matrix4f viewMatrix = new Matrix4f().rotateX(camera.getXRot() * ((float) Math.PI / 180.0F)).rotateY((camera.getYRot() + 180.0F) * ((float) Math.PI / 180.0F));
        Matrix4f viewProj = new Matrix4f(projectionMatrix).mul(viewMatrix);

        double minX = Math.min(startX, endX);
        double maxX = Math.max(startX, endX);
        double minY = Math.min(startY, endY);
        double maxY = Math.max(startY, endY);

        List<Entity> selected = new ArrayList<>();
        for(Entity entity : potentialEntities) 
        {
            AABB bb = entity.getBoundingBox();
            Vec3[] corners = new Vec3[] 
            {
                new Vec3(bb.minX, bb.minY, bb.minZ), new Vec3(bb.minX, bb.minY, bb.maxZ),
                new Vec3(bb.minX, bb.maxY, bb.minZ), new Vec3(bb.minX, bb.maxY, bb.maxZ),
                new Vec3(bb.maxX, bb.minY, bb.minZ), new Vec3(bb.maxX, bb.minY, bb.maxZ),
                new Vec3(bb.maxX, bb.maxY, bb.minZ), new Vec3(bb.maxX, bb.maxY, bb.maxZ)
            };

            double entMinX = Double.MAX_VALUE;
            double entMinY = Double.MAX_VALUE;
            double entMaxX = -Double.MAX_VALUE;
            double entMaxY = -Double.MAX_VALUE;
            boolean inFront = false;

            for(Vec3 corner : corners) 
            {
                Vector4f clipPos = new Vector4f((float) (corner.x - cameraPos.x), (float) (corner.y - cameraPos.y), (float) (corner.z - cameraPos.z), 1.0F);
                viewProj.transform(clipPos);
                if(clipPos.w() > 0.05F) 
                {
                    inFront = true; 
                    float ndcX = clipPos.x() / clipPos.w();
                    float ndcY = clipPos.y() / clipPos.w();
                    
                    double screenX = (ndcX + 1.0) / 2.0 * width;
                    double screenY = (1.0 - ndcY) / 2.0 * height;
                    entMinX = Math.min(entMinX, screenX);
                    entMinY = Math.min(entMinY, screenY);
                    entMaxX = Math.max(entMaxX, screenX);
                    entMaxY = Math.max(entMaxY, screenY);
                }
            }
            if(inFront)
            {
                if(entMinX <= maxX && entMaxX >= minX && entMinY <= maxY && entMaxY >= minY)
                {
                    selected.add(entity);
                }
            }
        }
        return selected;
    }
    
	public static HitResult raycastBlock(double maxDistance)
	{
		return raycast(maxDistance, false);
	}

	public static HitResult raycast(double maxDistance, boolean includeEntity)
	{
        Minecraft minecraft = Minecraft.getInstance();
		MouseHandler mouse = minecraft.mouseHandler;
        if(minecraft.level == null || minecraft.player == null)
        {
        	return null;
        }
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        double width = minecraft.getWindow().getWidth();
        double height = minecraft.getWindow().getHeight();

        double screenX = (2.0 * mouse.xpos() / width) - 1.0;
        double screenY = 1.0 - (2.0 * mouse.ypos() / height);

        float partialTick = minecraft.getFrameTime();
        double fovY = Math.toRadians(minecraft.gameRenderer.getFov(camera, partialTick, true));
        double aspectRatio = width / height;

        double halfHeight = Math.tan(fovY / 2.0);
        double halfWidth = halfHeight * aspectRatio;

        Vector3f look = camera.getLookVector();
        Vector3f up = camera.getUpVector();
        Vector3f left = camera.getLeftVector();

        Vec3 lookVec = new Vec3(look.x(), look.y(), look.z());
        Vec3 upVec = new Vec3(up.x(), up.y(), up.z());
        Vec3 rightVec = new Vec3(-left.x(), -left.y(), -left.z());

        Vec3 rayDir = lookVec.add(rightVec.scale(screenX * halfWidth)).add(upVec.scale(screenY * halfHeight)).normalize();
        Vec3 rayEnd = cameraPos.add(rayDir.scale(maxDistance));

        BlockHitResult blockHit = minecraft.level.clip(new ClipContext(cameraPos, rayEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, minecraft.player));
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(minecraft.level, minecraft.player, cameraPos, rayEnd, minecraft.player.getBoundingBox().expandTowards(rayDir.scale(maxDistance)), Entity::isAlive);
        
        return entityHit != null && includeEntity ? entityHit : blockHit;
    }
}
