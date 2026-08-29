package com.min01.tambs.gui.screen;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.components.MobSelectTab;
import com.min01.tambs.gui.components.PresetTab;
import com.min01.tambs.gui.components.TAMBSTab;
import com.min01.tambs.gui.components.TeamSettingTab;
import com.min01.tambs.gui.components.TextOnlyButton;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class TAMBSScreen extends Screen
{
	public static final int COLUMN_HEIGHT = 120;
	private static final Component EXPAND = Component.translatable("tambs.screen.expand");
	private static final Component COLLAPSE = Component.translatable("tambs.screen.collapse");

    private TextOnlyButton collapseButton;
    private MobSelectTab selectTab;
    private TeamSettingTab teamTab;
    private PresetTab presetTab;
    
    private TAMBSTab currentTab;
    private boolean isCollapsed;
    
    public TAMBSScreen() 
    {
        super(Component.empty());
    }
    
    @Override
    protected void init()
    {
        this.collapseButton = new TextOnlyButton(Button.builder(COLLAPSE, pButton -> 
        {
        	boolean collapsed = this.isCollapsed;
        	if(collapsed)
        	{
        		this.collapseButton.setMessage(COLLAPSE);
        		this.collapseButton.setY(this.height - (COLUMN_HEIGHT + 62));
        	}
        	else
        	{
        		this.collapseButton.setMessage(EXPAND);
        		this.collapseButton.setY(this.height - 20);
        	}
            this.isCollapsed = !collapsed;
        	this.currentTab.onCollapse();
            this.minecraft.player.setDeltaMovement(Vec3.ZERO);
        	this.setFocused(null);
        	
        }).bounds(-2, this.height - (COLUMN_HEIGHT + 62), this.font.width(COLLAPSE) + 10, 20));
        this.addRenderableWidget(this.collapseButton);
        
        this.selectTab = new MobSelectTab(this.width, this.height, 0, Component.translatable("tambs.screen.mob_select"));
        this.addRenderableWidget(this.selectTab);
        
        this.teamTab = new TeamSettingTab(this.width, this.height, 1, Component.translatable("tambs.screen.team_setting"));
        this.addRenderableWidget(this.teamTab);

        this.presetTab = new PresetTab(this.width, this.height, 2, Component.translatable("tambs.screen.preset"));
        this.addRenderableWidget(this.presetTab);
        
        this.currentTab = this.selectTab;
        this.setInitialFocus(this.currentTab);
    }
    
    @Override
    public boolean isPauseScreen() 
    {
    	return TAMBSClientData.isPaused();
    }
    
    @Override
    public void tick() 
    {
    	super.tick();
    	if(this.isPauseScreen())
    	{
    		this.minecraft.level.guardEntityTick(t -> this.minecraft.level.tickNonPassenger(t), this.minecraft.player);
        	if(this.isCollapsed && TAMBSClientUtil.isCameraMoving())
        	{
                this.minecraft.mouseHandler.grabMouse();
                this.minecraft.mouseHandler.turnPlayer();
        	}
    	}
    }
    
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
    {
        if(pKeyCode == 256 && this.shouldCloseOnEsc())
        {
            this.onClose();
            return true;
        }
        this.currentTab.keyPressed(pKeyCode, pScanCode, pModifiers);
    	return false;
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
    {
        this.collapseButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        
        if(!this.isCollapsed)
        {
            this.selectTab.renderTab(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            this.teamTab.renderTab(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            this.presetTab.renderTab(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            
            this.currentTab.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    	this.currentTab.renderCollapse(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
    
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) 
    {
    	if(!this.isCollapsed)
    	{
    		if(this.selectTab.isMouseOver(pMouseX, pMouseY))
    		{
    			this.currentTab = this.selectTab;
    		}
    		else if(this.teamTab.isMouseOver(pMouseX, pMouseY))
    		{
    			this.currentTab = this.teamTab;
    		}
    		else if(this.presetTab.isMouseOver(pMouseX, pMouseY))
    		{
    			this.currentTab = this.presetTab;
    		}
    		this.currentTab.setFocused(true);
    		this.setFocused(this.currentTab);
    	}
    	else if(!this.collapseButton.isMouseOver(pMouseX, pMouseY))
		{
    		return this.currentTab.mouseClickedCollapse(pMouseX, pMouseY, pButton);
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) 
    {
        this.currentTab.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    	if(this.isCollapsed && !this.collapseButton.isMouseOver(pMouseX, pMouseY))
    	{
        	return this.currentTab.mouseDraggedCollapse(pMouseX, pMouseY, pButton, pDragX, pDragY);
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
	    return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
    {
        this.currentTab.mouseReleased(pMouseX, pMouseY, pButton);
        this.minecraft.mouseHandler.releaseMouse();
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
    
    public TAMBSTab getCurrentTab()
    {
    	return this.currentTab;
    }
    
    public boolean isCollapsed()
    {
    	return this.isCollapsed;
    }
}