package com.min01.tambs.gui.components;

import org.apache.commons.lang3.StringUtils;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class MobCell extends AbstractWidget
{
    private final LivingEntity entity;
    private TextOnlyButton bookmarkButton;
    private boolean isBookmark;
    
    public MobCell(int pX, int pY, int pWidth, int pHeight, LivingEntity entity) 
    {
        super(pX, pY, pWidth, pHeight, entity.getDisplayName());
        this.entity = entity;
        
        this.bookmarkButton = new TextOnlyButton(Button.builder(Component.literal("☆"), pButton -> 
        {
        	boolean bookmark = this.isBookmark;
        	if(bookmark)
        	{
        		this.bookmarkButton.setMessage(Component.literal("☆"));
        	}
        	else
        	{
        		this.bookmarkButton.setMessage(Component.literal("★").withStyle(ChatFormatting.GOLD));
        	}
			this.isBookmark = !bookmark;
        }).bounds(this.getX() + 1, this.getY() + 1, 13, 13));
    }

    @Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
        this.renderBorder(pGuiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        
        if(this.isHovered)
        {
            pGuiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x44FFFFFF);
        }
        
        PoseStack stack = pGuiGraphics.pose();
        stack.pushPose();
        stack.translate(0.0F, 10.0F, 0.0F);
        pGuiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
        InventoryScreen.renderEntityInInventoryFollowsAngle(pGuiGraphics, this.getX() + (this.width / 2), this.getY() + this.height - 15, (int) (30 - this.entity.getBoundingBox().getSize()), 0, 0, this.entity);
        pGuiGraphics.disableScissor();
        stack.popPose();
        
        this.bookmarkButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
    
    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) 
    {
		if(pMouseY <= TAMBSScreen.COLUMN_HEIGHT)
		{
			return false;
		}
    	return super.isMouseOver(pMouseX, pMouseY);
    }
    
    @Override
    public void setX(int pX) 
    {
    	super.setX(pX);
    	this.bookmarkButton.setX(pX + 1);
    }
    
    @Override
    public void setY(int pY) 
    {
    	super.setY(pY);
    	this.bookmarkButton.setY(pY + 1);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
    	this.bookmarkButton.mouseClicked(pMouseX, pMouseY, pButton);
 		if(!this.bookmarkButton.isMouseOver(pMouseX, pMouseY))
		{
        	TAMBSClientData.select(this.entity.getType());
		}
    	return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    
    public boolean match(String query)
    {
    	ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(this.entity.getType());
    	String modId = location.getNamespace();
    	boolean isModId = query.startsWith("@") && StringUtils.containsIgnoreCase(modId, query.replace("@", ""));
    	return StringUtils.containsIgnoreCase(this.getMessage().getString(), query) || isModId;
    }
    
    public void renderBorder(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight)
    {
    	int i = this.isFocused() ? -1 : -6250336;
    	pGuiGraphics.fill(pX, pY, pX + pWidth, pY + pHeight, i);
    	pGuiGraphics.fill(pX + 1, pY + 1, pX + pWidth - 1, pY + pHeight - 1, -16777216);
    }
    
    public boolean isBookmark()
    {
    	return this.isBookmark;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
    {
    	
    }
}
