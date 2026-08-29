package com.min01.tambs.gui.screen;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.components.MobSelectTab;
import com.min01.tambs.gui.components.OptionTab;
import com.min01.tambs.gui.components.PresetTab;
import com.min01.tambs.gui.components.TAMBSTab;
import com.min01.tambs.gui.components.TextOnlyButton;
import com.min01.tambs.gui.components.ToolTab;
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
    private MobSelectTab mobTab;
    private PresetTab presetTab;
    private ToolTab toolTab;
    private OptionTab optionTab;
    
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
        
        this.mobTab = new MobSelectTab(this, this.width, this.height, 0, Component.translatable("tambs.screen.mob_select"));
        this.presetTab = new PresetTab(this, this.width, this.height, 1, Component.translatable("tambs.screen.preset"));
        this.toolTab = new ToolTab(this, this.width, this.height, 2, Component.translatable("tambs.screen.tools"));
        this.optionTab = new OptionTab(this, this.width, this.height, 3, Component.translatable("tambs.screen.options"));
        
        this.addRenderableWidget(this.mobTab);
        this.addRenderableWidget(this.presetTab);
        this.addRenderableWidget(this.toolTab);
        this.addRenderableWidget(this.optionTab);
        
        this.currentTab = this.mobTab;
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
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
    {
        this.collapseButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.mobTab.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.presetTab.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.toolTab.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.optionTab.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
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