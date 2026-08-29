package com.min01.tambs.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class MobSelectTab extends TAMBSTab
{
	private static final int COLUMN_COUNT = 10;
	private static final int CELL_HEIGHT = 70;
	
    private final List<MobCell> all = new ArrayList<>();
    private EditBox searchBox;
    private TextOnlyButton bookmarkButton;
    private boolean isBookmark;

    private double scrollAmount;
    private int innerHeight;
    
	public MobSelectTab(int pWidth, int pHeight, int index, Component pMessage)
	{
		super(pWidth, pHeight, index, pMessage);

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
        	    this.updateCell(MobCell::isBookmark);
        	}
			this.isBookmark = !bookmark;
        }).bounds(this.getX() + 13, this.getY() + 16, 15, 15));
	}

	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
        for(MobCell cell : this.all)
        {
            pGuiGraphics.enableScissor(0, this.height - TAMBSScreen.COLUMN_HEIGHT, this.width, this.height);
            cell.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            pGuiGraphics.disableScissor();
        }
	}

	@Override
	public void renderTab(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		super.renderTab(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.searchBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.bookmarkButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    	pGuiGraphics.drawString(Minecraft.getInstance().font, Component.literal("🔎"), this.getX() + 5, this.getY() + 20, 0xFFFFFF);
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) 
	{
		if(this.searchBox.isMouseOver(pMouseX, pMouseY))
		{
			this.searchBox.mouseClicked(pMouseX, pMouseY, pButton);
			this.searchBox.setFocused(true);
		}
		else if(this.bookmarkButton.isMouseOver(pMouseX, pMouseY))
		{
	    	this.bookmarkButton.mouseClicked(pMouseX, pMouseY, pButton);
		}
		else
		{
			this.searchBox.setFocused(false);
	        for(MobCell cell : this.all)
	        {
	        	if(cell.isMouseOver(pMouseX, pMouseY))
	        	{
	        		cell.mouseClicked(pMouseX, pMouseY, pButton);
	        		cell.setFocused(true);
	        	}
	        	else
	        	{
	        		cell.setFocused(false);
	        	}
	        }
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean mouseClickedCollapse(double pMouseX, double pMouseY, int pButton) 
	{
    	TAMBSClientUtil.placeOrRemoveMob(pButton);
    	return false;
	}
	
	@Override
	public boolean mouseDraggedCollapse(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
    	TAMBSClientUtil.placeOrRemoveMob(pButton);
    	return false;
	}
	
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
	{
        TAMBSClientData.release();
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public void onCollapse()
	{
		this.searchBox.setFocused(false);
        for(MobCell cell : this.all)
        {
        	cell.setFocused(false);
        }
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
	{
		if(this.searchBox.isFocused())
		{
			this.searchBox.keyPressed(pKeyCode, pScanCode, pModifiers);
		}
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}
	
	@Override
	public boolean charTyped(char pCodePoint, int pModifiers) 
	{
		if(this.searchBox.isFocused())
		{
			this.searchBox.charTyped(pCodePoint, pModifiers);
		}
		return super.charTyped(pCodePoint, pModifiers);
	}

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
    {
    	if(!this.isBookmark && !this.searchBox.isFocused())
    	{
    	    this.scrollAmount = Mth.clamp(this.scrollAmount - pDelta * 20.0D, 0.0D, Math.max(0, this.innerHeight));
    	    this.updateCell(t -> true);
    	}
	    return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
    
    private void buildGrid() 
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
    
    private void updateCell(Predicate<MobCell> predicate) 
    {
        int index = 0;
        for(MobCell cell : this.all)
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
    
    private void recalculateCell(int index, MobCell cell)
    {
        int width = this.width / COLUMN_COUNT;
        int column = index % COLUMN_COUNT;
        int row = index / COLUMN_COUNT;
        
        cell.setWidth(width);
        cell.setX(column * width);
        cell.setY((int)(this.height - TAMBSScreen.COLUMN_HEIGHT + (row * CELL_HEIGHT) - this.scrollAmount));
    }
    
    private void updateInnerHeight(int index)
    {
        this.innerHeight = ((index / COLUMN_COUNT) * CELL_HEIGHT) - TAMBSScreen.COLUMN_HEIGHT;
    }
}
