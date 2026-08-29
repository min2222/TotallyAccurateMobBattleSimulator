package com.min01.tambs.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import com.min01.tambs.client.TAMBSClientData;
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
		return minecraft.screen instanceof TAMBSScreen screen && screen.isCollapsed() && screen.isPauseScreen();
	}
	
	public static boolean isCameraMoving()
	{
		Minecraft minecraft = Minecraft.getInstance();
		return GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(), minecraft.options.keyPickItem.getKey().getValue()) == GLFW.GLFW_PRESS;
	}
	
	public static boolean isKeyDown(KeyMapping key)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Window window = minecraft.getWindow();
		return InputConstants.isKeyDown(window.getWindow(), key.getKey().getValue());
	}
	
	//copied from KeyboardInput;
	public static float calculateImpulse(boolean pInput, boolean pOtherInput)
	{
		if(pInput == pOtherInput)
		{
			return 0.0F;
		} 
		else 
		{
			return pInput ? 1.0F : -1.0F;
		}
	}
	
	public static void placeOrRemoveMob(int button)
	{
        HitResult hitResult = raycastFromMouse(300.0, true);
    	if(button == 0)
    	{
            if(hitResult instanceof BlockHitResult blockHit)
            {
                BlockPos blockPos = blockHit.getBlockPos();
                Direction direction = blockHit.getDirection();
                blockPos = blockPos.relative(direction);

                if(TAMBSClientData.SELECTED != null && !blockPos.equals(TAMBSClientData.LAST_PLACED))
                {
                    TAMBSNetwork.sendToServer(new PlaceMobPacket(ForgeRegistries.ENTITY_TYPES.getKey(TAMBSClientData.SELECTED), blockPos));
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
    
	public static HitResult raycastBlockFromMouse(double maxDistance)
	{
		return raycastFromMouse(maxDistance, false);
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

	    double width = minecraft.getWindow().getWidth();
	    double height = minecraft.getWindow().getHeight();

	    double minX = Math.min(startX, endX);
	    double maxX = Math.max(startX, endX);
	    double minY = Math.min(startY, endY);
	    double maxY = Math.max(startY, endY);

	    double ndcLeft = (2.0 * minX / width) - 1.0;
	    double ndcRight = (2.0 * maxX / width) - 1.0;
	    double ndcTop = 1.0 - (2.0 * minY / height);
	    double ndcBottom = 1.0 - (2.0 * maxY / height);

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
	    
	    BiFunction<Double, Double, Vec3> getRay = (nx, ny) ->
	    {
	        return lookVec.add(rightVec.scale(nx * halfWidth)).add(upVec.scale(ny * halfHeight)).normalize();
	    };

	    Vec3 rayTL = getRay.apply(ndcLeft, ndcTop);
	    Vec3 rayTR = getRay.apply(ndcRight, ndcTop);
	    Vec3 rayBL = getRay.apply(ndcLeft, ndcBottom);
	    Vec3 rayBR = getRay.apply(ndcRight, ndcBottom);

	    Vec3 endTL = cameraPos.add(rayTL.scale(maxDistance));
	    Vec3 endTR = cameraPos.add(rayTR.scale(maxDistance));
	    Vec3 endBL = cameraPos.add(rayBL.scale(maxDistance));
	    Vec3 endBR = cameraPos.add(rayBR.scale(maxDistance));

	    double minW = Math.min(cameraPos.x, Math.min(endTL.x, Math.min(endTR.x, Math.min(endBL.x, endBR.x))));
	    double minH = Math.min(cameraPos.y, Math.min(endTL.y, Math.min(endTR.y, Math.min(endBL.y, endBR.y))));
	    double minD = Math.min(cameraPos.z, Math.min(endTL.z, Math.min(endTR.z, Math.min(endBL.z, endBR.z))));
	    
	    double maxW = Math.max(cameraPos.x, Math.max(endTL.x, Math.max(endTR.x, Math.max(endBL.x, endBR.x))));
	    double maxH = Math.max(cameraPos.y, Math.max(endTL.y, Math.max(endTR.y, Math.max(endBL.y, endBR.y))));
	    double maxD = Math.max(cameraPos.z, Math.max(endTL.z, Math.max(endTR.z, Math.max(endBL.z, endBR.z))));

	    AABB searchBox = new AABB(minW, minH, minD, maxW, maxH, maxD);
	    return minecraft.level.getEntities(minecraft.player, searchBox, t -> true);
	}

	public static HitResult raycastFromMouse(double maxDistance, boolean includeEntity)
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
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(minecraft.level, minecraft.player, cameraPos, rayEnd, minecraft.player.getBoundingBox().expandTowards(rayDir.scale(maxDistance)), t -> true);
        
        return entityHit != null && includeEntity ? entityHit : blockHit;
    }
}
