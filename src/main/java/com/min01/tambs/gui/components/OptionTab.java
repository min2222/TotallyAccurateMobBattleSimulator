package com.min01.tambs.gui.components;

import java.math.BigDecimal;
import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.min01.tambs.gui.screen.TAMBSScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class OptionTab extends TAMBSTab
{
	private EditBox playSpeedBox;
	private EditBox slowMotionSpeed;
	private EditBox fastMotionSpeed;
	private EditBox flySpeedBox;
	private EditBox distanceBox;
	private Checkbox hideOnlyTambsUI;
	private Checkbox clearArrows;
	
	public OptionTab(TAMBSScreen screen)
	{
		super(screen.width / 4, screen.height - (TAMBSScreen.TAB_HEIGHT + 45), screen.width, screen.height, Component.translatable("tambs.tab.options"));
		this.playSpeedBox = new EditBox(Minecraft.getInstance().font, 5, screen.height - (TAMBSScreen.TAB_HEIGHT + 10), 100, 20, Component.translatable("tambs.option.play_speed"));
		this.slowMotionSpeed = new EditBox(Minecraft.getInstance().font, 5, screen.height - (TAMBSScreen.TAB_HEIGHT - 30), 100, 20, Component.translatable("tambs.option.slow_motion_speed"));
		this.fastMotionSpeed = new EditBox(Minecraft.getInstance().font, 5, screen.height - (TAMBSScreen.TAB_HEIGHT - 70), 100, 20, Component.translatable("tambs.option.fast_motion_speed"));
		this.flySpeedBox = new EditBox(Minecraft.getInstance().font, this.playSpeedBox.getX() + 140, screen.height - (TAMBSScreen.TAB_HEIGHT + 10), 100, 20, Component.translatable("tambs.option.fly_speed"));
		this.distanceBox = new EditBox(Minecraft.getInstance().font, this.playSpeedBox.getX() + 140, screen.height - (TAMBSScreen.TAB_HEIGHT - 30), 100, 20, Component.translatable("tambs.option.mouse_distance"));

		this.playSpeedBox.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.slowMotionSpeed.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.fastMotionSpeed.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.flySpeedBox.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.distanceBox.setResponder(t -> this.save(TAMBSClientData.INSTANCE));

		this.playSpeedBox.setFilter(this::isNumber);
		this.slowMotionSpeed.setFilter(this::isNumber);
		this.fastMotionSpeed.setFilter(this::isNumber);
		this.flySpeedBox.setFilter(this::isNumber);
		this.distanceBox.setFilter(this::isNumber);
		
		this.hideOnlyTambsUI = new Checkbox(this.flySpeedBox.getX() + 140, screen.height - (TAMBSScreen.TAB_HEIGHT + 10), 20, 20, Component.translatable("tambs.option.hide_only_tambs_ui"), TAMBSClientData.INSTANCE.hideOnlyTambsUI);
		this.clearArrows = new Checkbox(this.flySpeedBox.getX() + 140, this.hideOnlyTambsUI.getY() + 30, 20, 20, Component.translatable("tambs.option.clear_arrows"), TAMBSClientData.INSTANCE.clear_arrows);
		this.load(TAMBSClientData.INSTANCE);
	}

	public boolean isNumber(String text) 
	{
	    if(text.isBlank()) 
	    {
	        return true;
	    }
	    try
	    {
	        new BigDecimal(text.trim());
	        return true;
	    } 
	    catch(NumberFormatException e) 
	    {
	        return false;
	    }
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer) 
	{
    	pConsumer.accept(this.playSpeedBox);
    	pConsumer.accept(this.slowMotionSpeed);
    	pConsumer.accept(this.fastMotionSpeed);
    	pConsumer.accept(this.flySpeedBox);
    	pConsumer.accept(this.distanceBox);
    	pConsumer.accept(this.hideOnlyTambsUI);
    	pConsumer.accept(this.clearArrows);
		super.visitChildren(pConsumer);
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		pGuiGraphics.drawString(font, this.playSpeedBox.getMessage(), this.playSpeedBox.getX(), this.playSpeedBox.getY() - 13, 0xFFFFFF);
		pGuiGraphics.drawString(font, this.slowMotionSpeed.getMessage(), this.slowMotionSpeed.getX(), this.slowMotionSpeed.getY() - 13, 0xFFFFFF);
		pGuiGraphics.drawString(font, this.fastMotionSpeed.getMessage(), this.fastMotionSpeed.getX(), this.fastMotionSpeed.getY() - 13, 0xFFFFFF);
		pGuiGraphics.drawString(font, this.flySpeedBox.getMessage(), this.flySpeedBox.getX(), this.flySpeedBox.getY() - 13, 0xFFFFFF);
		pGuiGraphics.drawString(font, this.distanceBox.getMessage(), this.distanceBox.getX(), this.distanceBox.getY() - 13, 0xFFFFFF);
	}
	
	@Override
	public void load(Options options)
	{
		this.playSpeedBox.setValue(options.play_speed);
		this.slowMotionSpeed.setValue(options.slow_motion_speed);
		this.fastMotionSpeed.setValue(options.fast_motion_speed);
		this.flySpeedBox.setValue(options.fly_speed);
		this.distanceBox.setValue(options.mouse_distance);
	}
	
	@Override
	public void save(Options options) 
	{
		if(!this.playSpeedBox.getValue().isEmpty())
		{
			options.play_speed = this.playSpeedBox.getValue();
		}
		if(!this.slowMotionSpeed.getValue().isEmpty())
		{
			options.slow_motion_speed = this.slowMotionSpeed.getValue();
		}
		if(!this.fastMotionSpeed.getValue().isEmpty())
		{
			options.fast_motion_speed = this.fastMotionSpeed.getValue();
		}
		if(!this.flySpeedBox.getValue().isEmpty())
		{
			options.fly_speed = this.flySpeedBox.getValue();
		}
		if(!this.distanceBox.getValue().isEmpty())
		{
			options.mouse_distance = this.distanceBox.getValue();
		}
		options.hideOnlyTambsUI = this.hideOnlyTambsUI.selected();
		options.clear_arrows = this.clearArrows.selected();
	}
}
