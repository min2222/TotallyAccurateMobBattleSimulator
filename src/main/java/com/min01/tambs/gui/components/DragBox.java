package com.min01.tambs.gui.components;

import java.util.List;
import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;

public class DragBox
{
	public boolean enabled;
	
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;
	
	public int borderColor = FastColor.ABGR32.color(100, 0, 0, 150);
	public int baseColor = FastColor.ABGR32.color(200, 0, 0, 150);
	
	public Consumer<Entity> consumer;
	
	public void setCallback(Consumer<Entity> consumer)
	{
		this.consumer = consumer;
	}
	
	public void enable(double mouseX, double mouseY)
	{
		if(!this.enabled)
		{
    		this.enabled = true;
    		this.minX = (int) mouseX;
    		this.minY = (int) mouseY;
		}
		this.maxX = (int) mouseX;
		this.maxY = (int) mouseY;
	}
	
	public void disable()
	{
		if(this.enabled)
		{
     		List<Entity> list = TAMBSClientUtil.getEntities(this.minX, this.minY, this.maxX, this.maxY, TAMBSClientData.MAX_DISTANCE);
     		if(this.consumer != null)
     		{
        		list.forEach(this.consumer::accept);
     		}
    		this.enabled = false;
    		this.minX = 0;
    		this.minY = 0;
    		this.maxX = 0;
    		this.maxY = 0;
		}
	}
	
	public void render(GuiGraphics guiGraphics, int width, int height)
	{
		if(this.enabled)
		{
			int halfWidth = width / 2;
			int halfHeight = height / 2;
			if(this.minX <= halfWidth)
			{
				if(this.minY >= halfHeight)
				{
					guiGraphics.fill(this.minX, this.minY, this.maxX, this.maxY, this.borderColor);
					guiGraphics.fill(this.minX + 1, this.minY - 1, this.maxX - 1, this.maxY + 1, this.baseColor);
				}
				else
				{
					guiGraphics.fill(this.minX, this.minY, this.maxX, this.maxY, this.borderColor);
					guiGraphics.fill(this.minX + 1, this.minY + 1, this.maxX - 1, this.maxY - 1, this.baseColor);
				}
			}
			else if(this.minX >= halfWidth)
			{
				if(this.minY <= halfHeight)
				{
					guiGraphics.fill(this.minX, this.minY, this.maxX, this.maxY, this.borderColor);
					guiGraphics.fill(this.minX - 1, this.minY + 1, this.maxX + 1, this.maxY - 1, this.baseColor);
				}
				else
				{
					guiGraphics.fill(this.minX, this.minY, this.maxX, this.maxY, this.borderColor);
					guiGraphics.fill(this.minX - 1, this.minY - 1, this.maxX + 1, this.maxY + 1, this.baseColor);
				}
			}
		}
	}
}
