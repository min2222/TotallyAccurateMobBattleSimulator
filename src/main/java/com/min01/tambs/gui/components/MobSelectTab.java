package com.min01.tambs.gui.components;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class MobSelectTab extends TAMBSTab
{
	public MobSelectTab(TAMBSScreen screen, int pWidth, int pHeight, int index, Component pMessage)
	{
		super(screen, pWidth, pHeight, index, pMessage);
	}
	
	@Override
	public void buildGrid()
	{
        int width = this.width / COLUMN_COUNT;
        int index = 0;
        
        for(EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues())
        {
            Entity entity = type.create(Minecraft.getInstance().level);
            if(entity instanceof LivingEntity living && type.canSummon())
            {
                int column = index % COLUMN_COUNT;
                int row = index / COLUMN_COUNT;
                MobCell cell = new MobCell(column * width, this.height - TAMBSScreen.COLUMN_HEIGHT + (row * CELL_HEIGHT), width, CELL_HEIGHT, living);
                this.all.add(cell);
                index++;
            }
        }
        this.updateInnerHeight(index);
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) 
	{
		if(this.screen.isCollapsed())
		{
	    	TAMBSClientUtil.placeOrRemoveMob(pButton);
	    	return false;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(this.screen.isCollapsed())
		{
	    	TAMBSClientUtil.placeOrRemoveMob(pButton);
	    	return false;
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
	{
        TAMBSClientData.release();
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
}
