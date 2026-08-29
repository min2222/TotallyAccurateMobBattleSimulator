package com.min01.tambs.gui.components;

import com.min01.tambs.gui.screen.TAMBSScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TAMBSTab extends AbstractWidget
{
	protected static final int MAX_INDEX = 3;
	private final int index;
	
	public TAMBSTab(int pWidth, int pHeight, int index, Component pMessage)
	{
		super((pWidth / MAX_INDEX) * index, pHeight - (TAMBSScreen.COLUMN_HEIGHT + 45), pWidth, pHeight, pMessage);
		this.index = index;
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
	{
		
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
	{
		
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		if(pMouseY <= this.height - TAMBSScreen.COLUMN_HEIGHT && pMouseX >= (this.width / MAX_INDEX) * (this.index + 1))
		{
			return false;
		}
		return super.isMouseOver(pMouseX, pMouseY);
	}
	
	public void renderCollapse(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		
	}
	
	public boolean mouseClickedCollapse(double pMouseX, double pMouseY, int pButton)
	{
		return false;
	}
	
	public boolean mouseDraggedCollapse(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		return false;
	}
	
	public void onCollapse()
	{
		
	}

	public void renderTab(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
    	int i = this.isFocused() ? -1 : -6250336;

    	pGuiGraphics.fill(this.getX(), this.getY(), this.width, this.height, i);
    	pGuiGraphics.fill(this.getX() + 1, this.getY() + 1, this.width - 1, this.height, -16777216);
    	pGuiGraphics.fill(this.getX(), this.height, this.width, this.height - TAMBSScreen.COLUMN_HEIGHT, -16777216);
    	
    	pGuiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 5, this.getY() + 5, 0xFFFFFF);
	}
}
