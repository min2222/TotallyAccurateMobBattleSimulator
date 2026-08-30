package com.min01.tambs.gui.components;

import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.network.MoveMobPacket;
import com.min01.tambs.network.TAMBSNetwork;
import com.min01.tambs.util.TAMBSClientUtil;
import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ToolTab extends TAMBSTab
{
	private Checkbox teleportBox;
	//TODO team setting tool;
	
	public ToolTab(TAMBSScreen screen)
	{
		super(screen.width / 4, screen.height - (TAMBSScreen.TAB_HEIGHT + 45), screen.width, screen.height, Component.translatable("tambs.tab.tools"));
		this.teleportBox = new Checkbox(5, screen.height - TAMBSScreen.TAB_HEIGHT, 20, 20, Component.translatable("tambs.button.mob_teleport"), false);
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer) 
	{
    	pConsumer.accept(this.teleportBox);
		super.visitChildren(pConsumer);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(!this.isActive() && this.teleportBox.selected())
		{
			TAMBSClientUtil.setHovered();
		}
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) 
	{
		if(!this.isActive() && TAMBSClientData.isPaused())
		{
			if(this.teleportBox.selected())
			{
		        HitResult hitResult = TAMBSClientUtil.raycastFromMouse(Double.valueOf(TAMBSClientData.INSTANCE.mouse_distance), true);
				if(pButton == 0)
				{
		            if(hitResult instanceof EntityHitResult entityHit)
		            {
		            	if(TAMBSClientData.LAST_PLACED == null || !entityHit.getEntity().blockPosition().equals(TAMBSClientData.LAST_PLACED))
		            	{
			            	TAMBSClientData.selectUUID(entityHit.getEntity().getUUID());
			            	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
		            	}
		            }
		            else if(TAMBSClientData.SELECTED_UUID != null && hitResult instanceof BlockHitResult blockHit)
		            {
		            	BlockPos blockPos = blockHit.getBlockPos();
		                Direction direction = blockHit.getDirection();
		                blockPos = blockPos.relative(direction);
		            	TAMBSNetwork.sendToServer(new MoveMobPacket(TAMBSClientData.SELECTED_UUID, blockPos));
		    			Entity entity = TAMBSUtil.getEntityByUUID(Minecraft.getInstance().level, TAMBSClientData.SELECTED_UUID);
		    			if(entity != null)
		    			{
		    				entity.setPos(Vec3.atBottomCenterOf(blockPos));
		    				entity.setOldPosAndRot();
		    			}
		            	TAMBSClientData.selectUUID(null);
		            	TAMBSClientData.LAST_PLACED = entity.blockPosition();
		            }
				}
				else if(pButton == 1)
				{
					TAMBSClientData.selectUUID(null);
	            	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
				}
			}
			return false;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(this.isActive())
		{
			
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	
	@Override
	public boolean renderBlockHighlight() 
	{
		return this.teleportBox.selected() && TAMBSClientData.SELECTED_UUID != null;
	}
}
