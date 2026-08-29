package com.min01.tambs.gui.components;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TAMBSCell extends AbstractWidget
{
    private TextOnlyButton bookmarkButton;
    private boolean isBookmark;
    
    public TAMBSCell(int pX, int pY, int pWidth, int pHeight, Component pMessage) 
    {
        super(pX, pY, pWidth, pHeight, pMessage);
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
        this.renderCell(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.bookmarkButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if(this.isHovered)
        {
            pGuiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x44FFFFFF);
        }
    }
    
	public void renderCell(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
		
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
 			this.select();
		}
    	return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
    {
    	
    }
    
    public void select()
    {
    	
    }
    
    public boolean match(String query)
    {
    	return StringUtils.containsIgnoreCase(this.getMessage().getString(), query);
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
}
