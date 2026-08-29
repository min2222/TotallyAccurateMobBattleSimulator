package com.min01.tambs.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.min01.tambs.gui.screen.TAMBSScreen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TAMBSTab extends AbstractWidget
{
	protected static final int MAX_INDEX = 4;

	protected static final int COLUMN_COUNT = 10;
	protected static final int CELL_HEIGHT = 70;
	
	protected final List<TAMBSCell> all = new ArrayList<>();
	protected final TAMBSScreen screen;
	protected final int index;
	
	protected EditBox searchBox;
	protected TextOnlyButton bookmarkButton;
	protected boolean isBookmark;

	protected double scrollAmount;
	protected int innerHeight;
	
	public TAMBSTab(TAMBSScreen screen, int pWidth, int pHeight, int index, Component pMessage)
	{
		super((pWidth / MAX_INDEX) * index, pHeight - (TAMBSScreen.COLUMN_HEIGHT + 45), pWidth, pHeight, pMessage);
		this.init();
        this.screen = screen;
		this.index = index;
	}
	
	public void init()
	{
        this.buildGrid();
        this.searchBox = new EditBox(Minecraft.getInstance().font, 3, this.height - (TAMBSScreen.COLUMN_HEIGHT + 14), (this.width / MAX_INDEX) - 5, 12, Component.empty());
        this.searchBox.setResponder(this::updateSearch);
        
        this.bookmarkButton = new TextOnlyButton(Button.builder(Component.literal("☆"), pButton -> 
        {
            this.scrollAmount = 0;
        	boolean bookmark = this.isBookmark;
        	if(bookmark)
        	{
        		this.bookmarkButton.setMessage(Component.literal("☆"));
        		this.updateCell(t -> true);
        	}
        	else
        	{
        		this.bookmarkButton.setMessage(Component.literal("★").withStyle(ChatFormatting.GOLD));
        	    this.updateCell(TAMBSCell::isBookmark);
        	}
			this.isBookmark = !bookmark;
        }).bounds(this.getX() + 13, this.getY() + 16, 15, 15));
	}
	
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
	{
		if(!this.screen.isCollapsed())
		{
			super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		}
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
	{
		this.renderTab(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		if(this.isSelected())
		{
	        for(TAMBSCell cell : this.all)
	        {
	            pGuiGraphics.enableScissor(0, this.height - TAMBSScreen.COLUMN_HEIGHT, this.width, this.height);
	            cell.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	            pGuiGraphics.disableScissor();
	        }
			this.searchBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
			this.bookmarkButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	    	pGuiGraphics.drawString(Minecraft.getInstance().font, Component.literal("🔎"), this.getX() + 5, this.getY() + 20, 0xFFFFFF);
		}
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) 
	{
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
	{
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}
	
	@Override
	public boolean charTyped(char pCodePoint, int pModifiers) 
	{
		return super.charTyped(pCodePoint, pModifiers);
	}

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
    {
    	if(!this.isBookmark)
    	{
    	    this.scrollAmount = Mth.clamp(this.scrollAmount - pDelta * 20.0D, 0.0D, Math.max(0, this.innerHeight));
    	    this.updateCell(t -> true);
    	}
	    return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
	{
		
	}
    
    public void buildGrid() 
    {

    }

    public void updateSearch(String query) 
    {
        this.scrollAmount = 0;
	    this.updateCell(t ->
	    {
	    	if(t.match(query)) 
            {
		    	if(this.isBookmark && !t.isBookmark())
		    	{
		    		return false;
		    	}
            	return true;
            }
            return false;
	    });
    }
    
    private void updateCell(Predicate<TAMBSCell> predicate) 
    {
        int index = 0;
        for(TAMBSCell cell : this.all)
        {
        	cell.visible = false;
        	if(predicate.test(cell))
        	{
            	cell.visible = true;
        		this.recalculateCell(index, cell);
                index++;
        	}
        }
        this.updateInnerHeight(index);
    }
    
    public void recalculateCell(int index, TAMBSCell cell)
    {
        int width = this.width / COLUMN_COUNT;
        int column = index % COLUMN_COUNT;
        int row = index / COLUMN_COUNT;
        
        cell.setWidth(width);
        cell.setX(column * width);
        cell.setY((int)(this.height - TAMBSScreen.COLUMN_HEIGHT + (row * CELL_HEIGHT) - this.scrollAmount));
    }
    
    public void updateInnerHeight(int index)
    {
        this.innerHeight = ((index / COLUMN_COUNT) * CELL_HEIGHT) - TAMBSScreen.COLUMN_HEIGHT;
    }

	public void renderTab(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
    	int i = this.isFocused() ? -1 : -6250336;

    	pGuiGraphics.fill(this.getX(), this.getY(), this.width, this.height, i);
    	pGuiGraphics.fill(this.getX() + 1, this.getY() + 1, this.width - 1, this.height, -16777216);
    	pGuiGraphics.fill(this.getX(), this.height, this.width, this.height - TAMBSScreen.COLUMN_HEIGHT, -16777216);
    	
    	pGuiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 5, this.getY() + 5, 0xFFFFFF);
	}
	
	public void onCollapse()
	{
		this.searchBox.setFocused(false);
        for(TAMBSCell cell : this.all)
        {
        	cell.setFocused(false);
        }
	}
	
	public boolean isSelected()
	{
		return this.screen.getCurrentTab() == this;
	}
}
