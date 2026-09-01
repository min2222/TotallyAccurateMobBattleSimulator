package com.min01.tambs.gui.components;

import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.min01.tambs.client.TAMBSReloadListener.Preset;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class PresetCell extends MobCell
{
	protected TextOnlyButton deleteButton;
	protected boolean isDelete;
    
	public PresetCell(int pX, int pY, int pWidth, int pHeight, Preset preset) 
	{
		this(pX, pY, pWidth, pHeight, ResourceLocation.parse(preset.name), preset.tag);
		if(preset.bookmark)
		{
			this.isBookmark = true;
    		this.bookmarkButton.setMessage(Component.literal("★").withStyle(ChatFormatting.GOLD));
		}
	}
	
	public PresetCell(int pX, int pY, int pWidth, int pHeight, ResourceLocation name, CompoundTag tag) 
	{
		super(pX, pY, pWidth, pHeight, name, tag);
        this.deleteButton = new TextOnlyButton(Button.builder(Component.literal("x"), pButton -> 
        {
        	if(pButton.isActive())
        	{
            	boolean delete = this.isDelete;
            	if(delete)
            	{
            		this.deleteButton.setMessage(Component.literal("x"));
            	}
            	else
            	{
            		this.deleteButton.setMessage(Component.literal("x").withStyle(ChatFormatting.RED));
            	}
    			this.isDelete = !delete;
        	}
        }).bounds(this.getX() + 35, this.getY() - 1, 13, 13));
	}
	
    @Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
    	if(this.isActive())
    	{
            this.deleteButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    	}
    	super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
    	if(this.isActive())
    	{
     		if(this.deleteButton.mouseClicked(pMouseX, pMouseY, pButton))
    		{
     			return true;
    		}
    	}
    	return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    
    @Override
    public void setX(int pX) 
    {
    	super.setX(pX);
    	this.deleteButton.setX(pX + 35);
    }
    
    @Override
    public void setY(int pY) 
    {
    	super.setY(pY);
    	this.deleteButton.setY(pY - 1);
    }
    
	@Override
	public void load(Options options)
	{

	}
    
    @Override
    public void save(Options options)
    {
    	if(this.isDelete)
    	{
    		options.presets.removeIf(t -> t.tag.equals(this.tag));
    	}
    	else
    	{
        	options.presets.add(new Preset(ForgeRegistries.ENTITY_TYPES.getKey(this.entity.getType()).toString(), this.isBookmark, this.tag));
    	}
    }
}
