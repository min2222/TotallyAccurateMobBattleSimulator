package com.min01.tambs.gui.components;

import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.network.RemoveMobPacket;
import com.min01.tambs.network.TAMBSNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class ToolTab extends TAMBSTab
{
	private final DragBox dragBox = new DragBox();
	
	public ToolTab(TAMBSScreen screen, int pWidth, int pHeight, int index, Component pMessage)
	{
		super(screen, pWidth, pHeight, index, pMessage);
	}
	
	@Override
	public void buildGrid() 
	{
        int width = this.width / COLUMN_COUNT;
        int index = 0;
        
        Minecraft minecraft =  Minecraft.getInstance();
        Scoreboard scoreboard = minecraft.level.getScoreboard();
        for(PlayerTeam team : scoreboard.getPlayerTeams())
        {
            int column = index % COLUMN_COUNT;
            int row = index / COLUMN_COUNT;
            TeamCell cell = new TeamCell(column * width, this.height - TAMBSScreen.COLUMN_HEIGHT + (row * CELL_HEIGHT), width, CELL_HEIGHT, team);
            this.all.add(cell);
            index++;
        }
        this.updateInnerHeight(index);
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		if(this.screen.isCollapsed())
		{
			this.dragBox.render(pGuiGraphics, pMouseX, pMouseY);
		}
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(this.screen.isCollapsed())
		{
			if(pButton == 0)
			{
				this.dragBox.enable(pMouseX, pMouseY);
				this.dragBox.setCallback(t ->
				{
					TAMBSNetwork.sendToServer(new RemoveMobPacket(t.getUUID(), false));
				});
			}
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
	{
		this.dragBox.disable();
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
}
