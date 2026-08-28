package com.min01.tambs.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class TAMBSScreen extends Screen
{
	private static final int COLUMN_COUNT = 10;
	private static final int CELL_HEIGHT = 70;
	private static final int COLUMN_HEIGHT = 120;
	private static final Component EXPAND = Component.translatable("tambs.screen.expand");
	private static final Component COLLAPSE = Component.translatable("tambs.screen.collapse");

    private final List<MobCell> all = new ArrayList<>();
    
    private EditBox searchBox;
    private int innerHeight;
    
    private double scrollAmount;
    private boolean isCollapsed;

    public TAMBSScreen() 
    {
        super(Component.translatable(""));
    }
    
    @Override
    protected void init()
    {
        this.buildGrid();
        
        this.searchBox = new EditBox(this.font, 2, this.height - (COLUMN_HEIGHT + 15), this.width - 250, 12, Component.translatable(""));
        this.searchBox.setResponder(this::updateSearch);
        this.addRenderableWidget(this.searchBox);
    }
    
    @Override
    public void tick() 
    {
    	super.tick();
		this.minecraft.level.guardEntityTick(t -> this.minecraft.level.tickNonPassenger(t), this.minecraft.player);
    	if(this.isCollapsed && TAMBSClientUtil.isCameraMoving())
    	{
            this.minecraft.mouseHandler.grabMouse();
            this.minecraft.mouseHandler.turnPlayer();
    	}
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
    {
    	int top = this.height - COLUMN_HEIGHT;
        int y = this.isCollapsed ? this.height - 15 : top - 30;
        pGuiGraphics.drawString(this.font, this.isCollapsed ? EXPAND.getString() : COLLAPSE.getString(), 5, y + 4, 0xFFFFFF);

        if(this.isCollapsed)
        	return;

        pGuiGraphics.fill(0, top - 30, this.width, this.height, -16777216);
        for(Renderable renderable : this.renderables) 
        {
            if(renderable instanceof MobCell) 
            {
                pGuiGraphics.enableScissor(0, top, this.width, this.height);
                renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
                pGuiGraphics.disableScissor();
            }
            else 
            {
                renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        }
    }
    
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) 
    {
        int top = this.height - COLUMN_HEIGHT;
        int y = this.isCollapsed ? this.height : top - 15;
        int fontWidth = this.isCollapsed ? this.font.width(EXPAND) : this.font.width(COLLAPSE);
    	if(pMouseY <= y && pMouseY >= y - 15 && pMouseX <= fontWidth)
    	{
            if(pButton == 0) 
            {
                this.isCollapsed = !this.isCollapsed;
            	this.setFocused(null);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            	return true;
            }
    	}
    	else
        {
    		if(this.isCollapsed)
    		{
            	TAMBSClientUtil.placeOrRemoveMob(pButton);
            	return false;
    		}
    		else 
    		{
    			for(GuiEventListener guiEventListener : this.children()) 
    			{
        			boolean flag = guiEventListener instanceof MobCell ? pMouseY >= y + 15 : true;
    				if(flag)
    				{
        				if(guiEventListener.mouseClicked(pMouseX, pMouseY, pButton))
        				{
        					this.setFocused(guiEventListener);
        					if(pButton == 0)
        					{
        						this.setDragging(true);
        					}
        					return true;
        				}
    				}
    			}
    			return false;
    		}
        }
		return false;
    }
    
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) 
    {
    	if(this.isCollapsed)
    	{
        	TAMBSClientUtil.placeOrRemoveMob(pButton);
        	return false;
    	}
    	return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
    {
	    if(this.isCollapsed) 
	    {
	    	return false;
	    }
	    this.scrollAmount = Mth.clamp(this.scrollAmount - pDelta * 20.0D, 0.0D, Math.max(0, this.innerHeight));
	    
	    this.updateCell(t -> this.renderables.contains(t));
	    return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
    {
        TAMBSClientData.release();
        if(pButton == 2)
        {
            this.minecraft.mouseHandler.releaseMouse();
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
    
    private void buildGrid() 
    {
        int width = this.width / COLUMN_COUNT;
        int index = 0;
        
        for(EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues())
        {
            Entity entity = type.create(this.minecraft.level);
            if(entity instanceof LivingEntity living && type.canSummon())
            {
                int column = index % COLUMN_COUNT;
                int row = index / COLUMN_COUNT;
                MobCell cell = new MobCell(column * width, this.height - COLUMN_HEIGHT + (row * CELL_HEIGHT), width, CELL_HEIGHT, living);
                this.all.add(cell);
            	this.addRenderableWidget(cell);
                index++;
            }
        }
        this.updateInnerHeight(index);
    }

    public void updateSearch(String query) 
    {
	    this.updateCell(t ->
	    {
	    	t.visible = false;
	    	if(StringUtils.containsIgnoreCase(t.getMessage().getString(), query)) 
            {
            	t.visible = true;
            	return true;
            }
            return false;
	    });
        this.scrollAmount = 0;
    }
    
    private void updateCell(Predicate<MobCell> predicate) 
    {
        int index = 0;
        for(MobCell cell : this.all)
        {
        	if(predicate.test(cell))
        	{
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
        cell.setY((int)(this.height - COLUMN_HEIGHT + (row * CELL_HEIGHT) - this.scrollAmount));
    }
    
    private void updateInnerHeight(int index)
    {
        this.innerHeight = ((index / COLUMN_COUNT) * CELL_HEIGHT) - COLUMN_HEIGHT;
    }
    
    public static class MobCell extends AbstractWidget
    {
        private final LivingEntity entity;
        
        public MobCell(int pX, int pY, int pWidth, int pHeight, LivingEntity entity) 
        {
            super(pX, pY, pWidth, pHeight, entity.getDisplayName());
            this.entity = entity;
        }

        @Override
		protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
        {
            this.renderBorder(pGuiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            
            if(this.isHovered)
            {
                pGuiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x44FFFFFF);
            }
            
            pGuiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
            InventoryScreen.renderEntityInInventoryFollowsAngle(pGuiGraphics, this.getX() + (this.width / 2), this.getY() + this.height - 15, 30, 0, 0, this.entity);
            pGuiGraphics.disableScissor();
            
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            pGuiGraphics.drawString(font, this.getMessage(), this.getX() + (this.width / 2) - (font.width(this.getMessage()) / 2), this.getY() + 60, 0xFFFFFF, false);
        }
        
        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) 
        {
        	if(pButton == 0)
        	{
            	TAMBSClientData.select(this.entity.getType());
            	return super.mouseClicked(pMouseX, pMouseY, pButton);
        	}
        	return false;
        }
        
        protected void renderBorder(GuiGraphics pGuiGraphics, int pX, int pY, int pWidth, int pHeight)
        {
        	int i = this.isFocused() ? -1 : -6250336;
        	pGuiGraphics.fill(pX, pY, pX + pWidth, pY + pHeight, i);
        	pGuiGraphics.fill(pX + 1, pY + 1, pX + pWidth - 1, pY + pHeight - 1, -16777216);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
        {
        	
        }
    }
}