package com.min01.tambs.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;

public class TeamCell extends TAMBSCell
{
	private final PlayerTeam team;
	
    public TeamCell(int pX, int pY, int pWidth, int pHeight, PlayerTeam team) 
    {
        super(pX, pY, pWidth, pHeight, Component.empty());
        this.team = team;
    }

    @Override
    public void renderCell(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
    	Integer color = this.team.getColor().getColor();
    	if(color == null)
    	{
    		color = 16777216;
    	}
        pGuiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color);
    }
    
    @Override
    public void select()
    {
    	
    }
}
