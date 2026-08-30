package com.min01.tambs.gui.components;

import java.util.function.Consumer;

import com.min01.tambs.gui.screen.TAMBSScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.util.Mth;

public class TAMBSTabNavigationBar extends TabNavigationBar
{
	private int height;
	public TAMBSTabNavigationBar(int height, int pWidth, TabManager pTabManager, Iterable<Tab> pTabs) 
	{
		super(pWidth, pTabManager, pTabs);
		this.height = height;
	}
	
	@Override
	public void arrangeElements()
	{
		for(TabButton button : this.tabButtons)
		{
			button.setWidth(Mth.roundToward(this.width / this.tabs.size(), 2));
		}
		this.layout.arrangeElements();
		this.layout.setX(Mth.roundToward(0, 2));
		this.layout.setY(this.height - (TAMBSScreen.TAB_HEIGHT + 50));
	}
	
	public void visitWidgets(Consumer<AbstractWidget> pConsumer) 
	{
		this.layout.visitWidgets(pConsumer);
		for(Tab tab : this.tabs)
		{
			tab.visitChildren(pConsumer);
		}
	}
	
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		for(TabButton button : this.tabButtons)
		{
			button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		}
	}
}
