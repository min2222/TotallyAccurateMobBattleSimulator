package com.min01.tambs.gui.components;

import org.apache.commons.lang3.StringUtils;

import com.min01.tambs.client.TAMBSClientData;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class MobCell extends TAMBSCell
{
    private final LivingEntity entity;
    
    public MobCell(int pX, int pY, int pWidth, int pHeight, LivingEntity entity) 
    {
        super(pX, pY, pWidth, pHeight, entity.getDisplayName());
        this.entity = entity;
    }

    @Override
    public void renderCell(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
        PoseStack stack = pGuiGraphics.pose();
        stack.pushPose();
        stack.translate(0.0F, 10.0F, 0.0F);
        pGuiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
        InventoryScreen.renderEntityInInventoryFollowsAngle(pGuiGraphics, this.getX() + (this.width / 2), this.getY() + this.height - 15, (int) (30 - this.entity.getBoundingBox().getSize()), 0, 0, this.entity);
        pGuiGraphics.disableScissor();
        stack.popPose();
    }
    
    @Override
    public void select()
    {
    	TAMBSClientData.select(this.entity.getType());
    }
    
    @Override
    public boolean match(String query)
    {
    	ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(this.entity.getType());
    	String modId = location.getNamespace();
    	boolean isModId = query.startsWith("@") && StringUtils.containsIgnoreCase(modId, query.replace("@", ""));
    	return super.match(query) || isModId;
    }
}
