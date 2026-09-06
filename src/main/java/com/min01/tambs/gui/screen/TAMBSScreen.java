package com.min01.tambs.gui.screen;

import java.util.ArrayList;
import java.util.List;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.client.TAMBSReloadListener;
import com.min01.tambs.gui.components.MobCell;
import com.min01.tambs.gui.components.TextOnlyButton;
import com.min01.tambs.gui.tab.MobSelectTab;
import com.min01.tambs.gui.tab.OptionTab;
import com.min01.tambs.gui.tab.PresetTab;
import com.min01.tambs.gui.tab.TAMBSTab;
import com.min01.tambs.gui.tab.TAMBSTabNavigationBar;
import com.min01.tambs.gui.tab.ToolTab;
import com.min01.tambs.misc.TAMBSKeyMappings;
import com.min01.tambs.util.TAMBSClientUtil;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLPaths;

public class TAMBSScreen extends Screen
{
	public static final int TAB_HEIGHT = 100;
	
	private static final Component EXPAND = Component.translatable("tambs.button.expand");
	private static final Component COLLAPSE = Component.translatable("tambs.button.collapse");

	private final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);

	private TAMBSTabNavigationBar tabNavigationBar;
	public TextOnlyButton collapseButton;

	private boolean isCollapsed;
	private boolean isHidden;

	public TAMBSScreen()
	{
		super(Component.empty());
	}

	@Override
	protected void init()
	{
		this.tabNavigationBar = new TAMBSTabNavigationBar(this.height, this.width, this.tabManager, List.of(new MobSelectTab(this), new PresetTab(this), new ToolTab(this), new OptionTab(this)));
		this.addRenderableWidget(this.tabNavigationBar);

		this.collapseButton = new TextOnlyButton(Button.builder(COLLAPSE, pButton -> 
        {
			TAMBSClientData.pause(true);
            this.isCollapsed = !this.isCollapsed;
    		pButton.setMessage(this.isCollapsed ? EXPAND : COLLAPSE);
			FrameLayout.centerInRectangle(this.collapseButton, 0, this.isCollapsed ? this.height - 20 : this.height - (TAB_HEIGHT + 65), this.font.width(COLLAPSE) + 10, 20);
			this.tabNavigationBar.visitWidgets(widget -> widget.active = widget.visible = !this.isCollapsed);
            this.minecraft.player.setDeltaMovement(Vec3.ZERO);
            this.tabNavigationBar.tabs.forEach(t -> 
            {
    			if(t instanceof TAMBSTab tab)
    			{
    				tab.save(TAMBSClientData.INSTANCE);
    		    	TAMBSReloadListener.save(FMLPaths.CONFIGDIR.get());
    			}
            });
        }).bounds(0, 0, this.font.width(COLLAPSE) + 15, 20));
		this.addRenderableWidget(this.collapseButton);
		this.tabNavigationBar.selectTab(0, false);
		this.doLayout();
		this.tabNavigationBar.tabs.forEach(t -> 
		{
			if(t instanceof TAMBSTab tab)
			{
				tab.load(TAMBSClientData.INSTANCE);
			}
		});
	}

	public void doLayout()
	{
	    if(this.tabNavigationBar != null && this.collapseButton != null) 
	    {
	        this.tabNavigationBar.setWidth(this.width);
	        this.tabNavigationBar.arrangeElements();
	        this.collapseButton.setMessage(this.isCollapsed ? EXPAND : COLLAPSE);
	        FrameLayout.centerInRectangle(this.collapseButton, 0, this.isCollapsed ? this.height - 20 : this.height - (TAB_HEIGHT + 65), this.font.width(COLLAPSE) + 10, 20);
	        this.tabNavigationBar.visitWidgets(widget -> widget.active = widget.visible = !this.isCollapsed);
	        this.setTabArea();
	    }
	}

	public void setTabArea() 
	{
	    ScreenRectangle rectangle = new ScreenRectangle(0, this.height - (TAB_HEIGHT + 45), this.width, this.height);
	    this.tabManager.setTabArea(rectangle);
	}
	
	public void refresh()
	{
	    Tab current = this.getCurrentTab();
	    if(current != null)
	    {
	        current.visitChildren(this::removeWidget);
	        current.visitChildren(this::addRenderableWidget);
	    }
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
		this.minecraft.level.setDayTime(Long.valueOf(TAMBSClientData.INSTANCE.time));
		if(this.isPauseScreen())
		{
			this.minecraft.level.guardEntityTick(t -> this.minecraft.level.tickNonPassenger(t), this.minecraft.player);
		}
		else
		{
			TAMBSClientData.clear();
		}
		if(TAMBSClientUtil.isCameraMoving())
		{
			this.minecraft.mouseHandler.grabMouse();
			this.minecraft.mouseHandler.turnPlayer();
		}
		if(this.getCurrentTab() instanceof TAMBSTab tab)
		{
			tab.tick();
		}
	}
	
	@Override
	public void onClose() 
	{
		super.onClose();
		TAMBSClientData.MOBBATTLE_MODE = false;
		this.isHidden = false;
		this.minecraft.options.hideGui = false;
		this.tabNavigationBar.tabs.forEach(t -> 
		{
			if(t instanceof TAMBSTab tab)
			{
				tab.save(TAMBSClientData.INSTANCE);
		    	TAMBSReloadListener.save(FMLPaths.CONFIGDIR.get());
			}
		});
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers)
	{
		if(pKeyCode == InputConstants.KEY_F1)
		{
			if(TAMBSClientData.INSTANCE.hideOnlyTambsUI)
			{
				this.isHidden = !this.isHidden;
				return false;
			}
			this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
			return false;
		}
		else if(InputConstants.isKeyDown(this.minecraft.getWindow().getWindow(), 292) && this.minecraft.keyboardHandler.handleDebugKeys(pKeyCode))
		{
			return true;
		}
		if(this.isCollapsed && pKeyCode == TAMBSKeyMappings.PLAY.getKey().getValue())
		{
			TAMBSClientData.pause(!TAMBSClientData.isPaused());
        	this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    		this.minecraft.mouseHandler.releaseMouse();
			return false;
		}
		if(pKeyCode == 256 && this.shouldCloseOnEsc()) 
		{
			this.onClose();
			return true;
		}
		if(!(this.getFocused() instanceof EditBox) && pKeyCode == InputConstants.KEY_SPACE)
		{
			return false;
		}
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		if(this.minecraft.options.hideGui || this.isHidden)
		{
			return;
		}
		
		if(!this.isCollapsed)
		{
			this.renderDirtBackground(pGuiGraphics);
		}

		for(Renderable renderable : this.renderables) 
		{
			if(renderable instanceof MobCell cell)
			{
				cell.hidden = cell.getY() > this.height || cell.getY() + cell.getHeight() < this.height - TAB_HEIGHT;
	            pGuiGraphics.enableScissor(0, this.height - TAB_HEIGHT, this.width, this.height);
				cell.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
				pGuiGraphics.disableScissor();
			}
			else 
			{
				renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
			}
		}
	}

	@Override
	public void renderDirtBackground(GuiGraphics pGuiGraphics) 
	{
		pGuiGraphics.blit(CreateWorldScreen.LIGHT_DIRT_BACKGROUND, 0, this.height - (TAB_HEIGHT + 40), 0, 0.0F, 0.0F, this.width, this.height, 32, 32);
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		for(GuiEventListener eventListener : new ArrayList<>(this.children())) 
		{
			if(eventListener instanceof MobCell && pMouseY <= this.height - TAB_HEIGHT)
			{
				continue;
			}
			if(eventListener instanceof TAMBSTab && !this.canClick(pMouseY))
			{
				continue;
			}
			if(eventListener.mouseClicked(pMouseX, pMouseY, pButton))
			{
				this.setFocused(eventListener);
				if(pButton == 0) 
				{
					this.setDragging(true);
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) 
	{
		boolean flag = super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
		if(!flag)
		{
			if(this.getCurrentTab() instanceof TAMBSTab tab && this.canClick(pMouseY) && !this.collapseButton.isMouseOver(pMouseX, pMouseY))
			{
				tab.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
			}
		}
		return flag;
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) 
	{
		if(this.isCollapsed) 
		{
			return false;
		}
		if(this.getCurrentTab() instanceof TAMBSTab tab)
		{
			tab.mouseScrolled(pMouseX, pMouseY, pDelta);
		}
		return super.mouseScrolled(pMouseX, pMouseY, pDelta);
	}
	
	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
	{
		TAMBSClientData.release();
		this.minecraft.mouseHandler.releaseMouse();
		if(this.getCurrentTab() instanceof TAMBSTab tab)
		{
			tab.mouseReleased(pMouseX, pMouseY, pButton);
		}
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
	
	public boolean canClick(double pMouseY)
	{
		if(!this.isCollapsed)
		{
			return pMouseY <= this.height - (TAB_HEIGHT + 50);
		}
		return true;
	}
	
	public Tab getCurrentTab()
	{
		return this.tabManager.getCurrentTab();
	}
	
	public boolean isHidden() 
	{
		return this.isHidden;
	}

	public boolean isCollapsed()
	{
		return this.isCollapsed;
	}
}