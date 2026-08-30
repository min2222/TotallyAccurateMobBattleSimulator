package com.min01.tambs.gui.components;

import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.util.TAMBSClientUtil;
import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class PresetTab extends MobSelectTab
{
	protected TextOnlyButton addButton;
	protected boolean isAdd;
	protected TAMBSScreen screen;
	
	public PresetTab(TAMBSScreen screen)
	{
		super(screen, Component.translatable("tambs.tab.preset"));
		this.screen = screen;
	}
	
	@Override
	public void init(TAMBSScreen screen) 
	{
		super.init(screen);
        this.addButton = new TextOnlyButton(Button.builder(Component.literal("+"), pButton -> 
        {
        	if(this.isActive())
        	{
            	boolean add = this.isAdd;
            	if(add)
            	{
            		this.addButton.setMessage(Component.literal("+"));
            	}
            	else
            	{
            		this.addButton.setMessage(Component.literal("+").withStyle(ChatFormatting.GREEN));
            	}
    			this.isAdd = !add;
        	}
        }).bounds(this.getX() + 24, this.getY() + 16, 15, 15));
	}
	
	@Override
	public void buildGrid()
	{
		
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer) 
	{
		pConsumer.accept(this.addButton);
		super.visitChildren(pConsumer);
	}
	
	@Override
	public boolean renderEntityPreview() 
	{
		return !this.isAdd;
	}
	
	@Override
	public boolean renderBlockHighlight() 
	{
		return !this.isAdd;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(!this.isActive())
		{
			TAMBSClientUtil.setHovered();
		}
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		if(!this.isActive() && this.isAdd)
		{
			if(pButton == 0)
			{
		        HitResult hitResult = TAMBSClientUtil.raycastFromMouse(Double.valueOf(TAMBSClientData.INSTANCE.mouse_distance), true);
	            if(hitResult instanceof EntityHitResult entityHit)
	            {
	            	Entity entity =  entityHit.getEntity();
	            	if(entity instanceof LivingEntity living)
	            	{
		            	if(TAMBSClientData.LAST_PLACED == null || !living.blockPosition().equals(TAMBSClientData.LAST_PLACED))
		            	{
		            		//FIXME unable to click cell until switch tab;
		            		//FIXME bookmark conflict with normal mob select tab;
		            		//TODO save to json with nbt tag;
		                    MobCell cell = new MobCell(0, this.height - TAMBSScreen.TAB_HEIGHT, this.width / COLUMN_COUNT, CELL_HEIGHT, living, TAMBSUtil.saveEntity(living));
		                    this.all.add(cell);
		                    this.scrollAmount = 0;
		                    this.updateCell(t -> true);
			            	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
			            	TAMBSClientData.LAST_PLACED = living.blockPosition();
		            	}
	            	}
	            }
			}
            return false;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(!this.isActive() && this.isAdd)
		{
			return false;
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		if(this.isActive())
		{
			this.addButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		}
	}
}
