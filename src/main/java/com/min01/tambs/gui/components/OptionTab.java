package com.min01.tambs.gui.components;

import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.min01.tambs.gui.screen.TAMBSScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class OptionTab extends TAMBSTab
{
	private EditBox distanceBox;
	private EditBox playSpeedBox;
	private EditBox flySpeedBox;
	
	//TODO mob griefing, mob kill, overlay;
	
	public OptionTab(TAMBSScreen screen)
	{
		super(screen.width / 4, screen.height - (TAMBSScreen.TAB_HEIGHT + 45), screen.width, screen.height, Component.translatable("tambs.tab.options"));
		this.distanceBox = new EditBox(Minecraft.getInstance().font, 5, screen.height - TAMBSScreen.TAB_HEIGHT, 100, 20, Component.translatable("tambs.option.mouse_distance"));
		this.playSpeedBox = new EditBox(Minecraft.getInstance().font, 5, screen.height - (TAMBSScreen.TAB_HEIGHT - 40), 100, 20, Component.translatable("tambs.option.play_speed"));
		this.flySpeedBox = new EditBox(Minecraft.getInstance().font, 5, screen.height - (TAMBSScreen.TAB_HEIGHT - 80), 100, 20, Component.translatable("tambs.option.fly_speed"));
		this.distanceBox.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.playSpeedBox.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.flySpeedBox.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.load(TAMBSClientData.INSTANCE);
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer) 
	{
    	pConsumer.accept(this.distanceBox);
    	pConsumer.accept(this.playSpeedBox);
    	pConsumer.accept(this.flySpeedBox);
		super.visitChildren(pConsumer);
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		pGuiGraphics.drawString(font, this.distanceBox.getMessage(), this.distanceBox.getX(), this.distanceBox.getY() - 13, 0xFFFFFF);
		pGuiGraphics.drawString(font, this.playSpeedBox.getMessage(), this.playSpeedBox.getX(), this.playSpeedBox.getY() - 13, 0xFFFFFF);
		pGuiGraphics.drawString(font, this.flySpeedBox.getMessage(), this.flySpeedBox.getX(), this.flySpeedBox.getY() - 13, 0xFFFFFF);
	}
	
	@Override
	public void load(Options options)
	{
		this.distanceBox.setValue(options.mouse_distance);
		this.playSpeedBox.setValue(options.play_speed);
		this.flySpeedBox.setValue(options.fly_speed);
	}
	
	@Override
	public void save(Options options) 
	{
		if(!this.distanceBox.getValue().isEmpty())
		{
			options.mouse_distance = this.distanceBox.getValue();
		}
		if(!this.playSpeedBox.getValue().isEmpty())
		{
			options.play_speed = this.playSpeedBox.getValue();
		}
		if(!this.flySpeedBox.getValue().isEmpty())
		{
			options.fly_speed = this.flySpeedBox.getValue();
		}
	}
}
