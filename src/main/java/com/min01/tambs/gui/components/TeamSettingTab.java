package com.min01.tambs.gui.components;

import com.min01.tambs.network.RemoveMobPacket;
import com.min01.tambs.network.TAMBSNetwork;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TeamSettingTab extends TAMBSTab
{
	private final DragBox dragBox = new DragBox();
	
	public TeamSettingTab(int pWidth, int pHeight, int index, Component pMessage)
	{
		super(pWidth, pHeight, index, pMessage);
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(pButton == 0)
		{
			this.dragBox.enable(pMouseX, pMouseY);
			this.dragBox.setCallback(t ->
			{
				TAMBSNetwork.sendToServer(new RemoveMobPacket(t.getUUID(), false));
			});
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	
	@Override
	public void renderCollapse(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		this.dragBox.render(pGuiGraphics, this.width, this.height);
	}
	
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
	{
		this.dragBox.disable();
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
}
