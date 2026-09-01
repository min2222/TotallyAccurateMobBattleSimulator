package com.min01.tambs.gui.tab;

import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSReloadListener.Options;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public class TAMBSTab extends AbstractWidget implements Tab
{
	protected final GridLayout layout = new GridLayout();
	
	public TAMBSTab(int pX, int pY, int pWidth, int pHeight, Component pMessage)
	{
		super(pX, pY, pWidth, pHeight, pMessage);
	}

	@Override
	public Component getTabTitle() 
	{
		return this.getMessage();
	}

	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer)
	{
		pConsumer.accept(this);
		this.layout.visitWidgets(pConsumer);
	}

	@Override
	public void doLayout(ScreenRectangle pRectangle) 
	{
		this.layout.arrangeElements();
		FrameLayout.alignInRectangle(this.layout, pRectangle, 0.5F, 0.16666667F);
	}

	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
	{
		
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) 
	{

	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
	    return false;
	}
	
	public void load(Options options)
	{
		
	}
	
	public void save(Options options)
	{
		
	}

	public boolean renderEntityPreview()
	{
		return false;
	}
	
	public boolean renderBlockHighlight() 
	{
		return false;
	}
}