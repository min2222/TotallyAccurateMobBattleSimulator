package com.min01.tambs.gui.components;

import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.min01.tambs.gui.screen.TAMBSScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class OptionTab extends TAMBSTab
{
	private EditBox distanceBox;
	private EditBox playSpeedBox;
	
	public OptionTab(TAMBSScreen screen)
	{
		super(screen.width / 4, screen.height - (TAMBSScreen.TAB_HEIGHT + 45), screen.width, screen.height, Component.translatable("tambs.tab.options"));
		this.distanceBox = new EditBox(Minecraft.getInstance().font, 5, screen.height - TAMBSScreen.TAB_HEIGHT, 100, 20, Component.translatable("tambs.option.mouse_distance"));
		this.playSpeedBox = new EditBox(Minecraft.getInstance().font, 5, screen.height - (TAMBSScreen.TAB_HEIGHT - 30), 100, 20, Component.translatable("tambs.option.play_speed"));
		this.distanceBox.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.playSpeedBox.setResponder(t -> this.save(TAMBSClientData.INSTANCE));
		this.load(TAMBSClientData.INSTANCE);
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer) 
	{
    	pConsumer.accept(this.distanceBox);
    	pConsumer.accept(this.playSpeedBox);
		super.visitChildren(pConsumer);
	}
	
	@Override
	public void load(Options options)
	{
		this.distanceBox.setValue(options.mouse_distance);
		this.playSpeedBox.setValue(options.play_speed);
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
	}
}
