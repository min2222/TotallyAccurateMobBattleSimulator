package com.min01.tambs.gui.components;

import org.apache.commons.lang3.StringUtils;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class MobCell extends AbstractWidget
{
	protected TextOnlyButton bookmarkButton;
    protected boolean isBookmark;
    public final Entity entity;
    public final CompoundTag tag;
    public boolean error;
    public boolean hidden;
    
    public MobCell(int pX, int pY, int pWidth, int pHeight, ResourceLocation name) 
    {
    	this(pX, pY, pWidth, pHeight, name, new CompoundTag());
    }
    
    public MobCell(int pX, int pY, int pWidth, int pHeight, ResourceLocation name, CompoundTag tag) 
    {
        super(pX, pY, pWidth, pHeight, Component.empty());
        this.entity = ForgeRegistries.ENTITY_TYPES.getValue(name).create(Minecraft.getInstance().level);
        this.tag = tag;
    	try
    	{
            if(!tag.isEmpty())
            {
        		this.entity.load(tag);
            }
    	}
    	catch(Throwable e)
    	{
    		
    	}
        this.bookmarkButton = new TextOnlyButton(Button.builder(Component.literal("☆"), pButton -> 
        {
        	if(pButton.isActive())
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
        	}
        }).bounds(this.getX() + 1, this.getY() + 1, 13, 13));
		this.setTooltip(Tooltip.create(this.entity.getDisplayName()));
        this.setTooltipDelay(10);
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
    {
        if(this.hidden)
        	return;
    	super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
    
    @Override
    protected ClientTooltipPositioner createTooltipPositioner() 
    {
    	return new BelowOrAboveWidgetTooltipPositioner(this);
    }

    @Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
    	if(this.error)
    		return;
    	try
    	{
        	if(this.isActive())
        	{
                this.renderBorder(pGuiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
                this.bookmarkButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                
                PoseStack stack = pGuiGraphics.pose();
                stack.pushPose();
                stack.translate(0.0F, 10.0F, 0.0F);
                pGuiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
                if(this.entity instanceof LivingEntity living)
                {
                	InventoryScreen.renderEntityInInventoryFollowsAngle(pGuiGraphics, this.getX() + (this.width / 2), this.getY() + this.height - 15, (int) (30 - this.entity.getBoundingBox().getSize()), 0, 0, living);
                }
                pGuiGraphics.disableScissor();
                stack.popPose();
                
                if(this.isHovered)
                {
                    pGuiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x44FFFFFF);
                }
        	}
    	}
    	catch(Throwable e)
    	{
    		this.error = true;
    	}
    }
    
	public void load(Options options)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(this.entity.getType());
		if(options.contains(options.bookmarks, location))
		{
			this.isBookmark = true;
    		this.bookmarkButton.setMessage(Component.literal("★").withStyle(ChatFormatting.GOLD));
		}
	}
	
	public void save(Options options)
	{
		String location = ForgeRegistries.ENTITY_TYPES.getKey(this.entity.getType()).toString();
	    if(this.isBookmark)
	    {
	        if(!options.bookmarks.contains(location)) 
	        {
	            options.bookmarks.add(location);
	        }
	    }
	    else
	    {
	        options.bookmarks.remove(location);
	    }
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
    	if(this.isActive())
    	{
     		if(!this.bookmarkButton.mouseClicked(pMouseX, pMouseY, pButton))
    		{
     	    	TAMBSClientData.selectCell(this);
    		}
    	}
    	return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
    {
    	
    }
    
    public void renderBorder(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight)
    {
    	int i = this.isFocused() ? -1 : -6250336;
    	pGuiGraphics.fill(pX, pY, pX + pWidth, pY + pHeight, i);
    	
        pGuiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        pGuiGraphics.blit(Screen.BACKGROUND_LOCATION, pX + 1, pY + 1, 0, 0.0F, 0.0F, pWidth - 2, pHeight - 2, 32, 32);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    public boolean isBookmark()
    {
    	return this.isBookmark;
    }

    public boolean match(String query)
    {
    	ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(this.entity.getType());
    	String modId = location.getNamespace();
    	boolean isModId = query.startsWith("@") && StringUtils.containsIgnoreCase(modId, query.replace("@", ""));
    	boolean isTag = query.startsWith("#") && this.entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(query.replaceAll("[^a-zA-Z0-9:]", "").toLowerCase())));
    	return StringUtils.containsIgnoreCase(this.entity.getDisplayName().getString(), query) || StringUtils.containsIgnoreCase(location.getPath(), query) || isModId || isTag;
    }
}
