package com.min01.tambs.gui.components;

import java.util.List;
import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
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
	
	public void disable(Consumer<Entity> consumer)
	{
		if(this.enabled)
		{
     		List<Entity> list = TAMBSClientUtil.getEntities(this.minX, this.minY, this.maxX, this.maxY, Double.valueOf(TAMBSClientData.INSTANCE.mouse_distance));
    		list.forEach(consumer);
    		if(list.size() > 0)
    		{
            	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
    		}
    		this.enabled = false;
    		this.minX = 0;
    		this.minY = 0;
    		this.maxX = 0;
    		this.maxY = 0;
		}
	}
	
	public void setColor(int color)
	{
	    int r = (color >> 16) & 0xFF;
	    int g = (color >> 8) & 0xFF;
	    int b = color & 0xFF;
	    this.borderColor = FastColor.ABGR32.color(100, r, g, b);
	    this.baseColor = FastColor.ABGR32.color(200, r, g, b);
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
