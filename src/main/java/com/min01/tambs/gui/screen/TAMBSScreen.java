package com.min01.tambs.gui.screen;

import java.util.List;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.gui.components.MobCell;
import com.min01.tambs.gui.components.MobSelectTab;
import com.min01.tambs.gui.components.OptionTab;
import com.min01.tambs.gui.components.PresetTab;
import com.min01.tambs.gui.components.TAMBSTab;
import com.min01.tambs.gui.components.TAMBSTabNavigationBar;
import com.min01.tambs.gui.components.TextOnlyButton;
import com.min01.tambs.gui.components.ToolTab;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class TAMBSScreen extends Screen
{
	public static final int TAB_HEIGHT = 120;
	
	private static final Component EXPAND = Component.translatable("tambs.button.expand");
	private static final Component COLLAPSE = Component.translatable("tambs.button.collapse");

	private final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);

	private TAMBSTabNavigationBar tabNavigationBar;
	public TextOnlyButton collapseButton;

	private boolean isCollapsed;

	public TAMBSScreen()
	{
		super(Component.empty());
	}

	@Override
	protected void init()
	{
		this.tabNavigationBar = new TAMBSTabNavigationBar(this.height, this.width, this.tabManager, List.of(new MobSelectTab(this), new PresetTab(), new ToolTab(this), new OptionTab(this)));
		this.addRenderableWidget(this.tabNavigationBar);

		this.collapseButton = new TextOnlyButton(Button.builder(COLLAPSE, pButton -> 
        {
            this.isCollapsed = !this.isCollapsed;
    		pButton.setMessage(this.isCollapsed ? EXPAND : COLLAPSE);
			FrameLayout.centerInRectangle(this.collapseButton, 0, this.isCollapsed ? this.height - 20 : this.height - (TAB_HEIGHT + 65), this.font.width(COLLAPSE) + 10, 20);
			this.tabNavigationBar.visitWidgets(widget -> widget.active = widget.visible = !this.isCollapsed);
            this.minecraft.player.setDeltaMovement(Vec3.ZERO);
        }).bounds(0, 0, this.font.width(COLLAPSE) + 10, 20));
		this.addRenderableWidget(this.collapseButton);
		this.tabNavigationBar.selectTab(0, false);
		this.repositionElements();
	}

	@Override
	public void repositionElements() 
	{
		if(this.tabNavigationBar != null && this.collapseButton != null) 
		{
			this.tabNavigationBar.setWidth(this.width);
			this.tabNavigationBar.arrangeElements();
			this.collapseButton.setMessage(this.isCollapsed ? EXPAND : COLLAPSE);
			FrameLayout.centerInRectangle(this.collapseButton, 0, this.isCollapsed ? this.height - 20 : this.height - (TAB_HEIGHT + 65), this.font.width(COLLAPSE) + 10, 20);
			this.tabNavigationBar.visitWidgets(widget -> widget.active = widget.visible = !this.isCollapsed);
			ScreenRectangle rectangle = new ScreenRectangle(0, this.height - (TAB_HEIGHT + 45), this.width, this.height);
			this.tabManager.setTabArea(rectangle);
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
		if(this.isCollapsed)
		{
			if(pKeyCode == 256 && this.shouldCloseOnEsc()) 
			{
				this.onClose();
				return true;
			}
			return false;
		}
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		if(!this.isCollapsed)
		{
			this.renderDirtBackground(pGuiGraphics);
		}

		for(Renderable renderable : this.renderables) 
		{
			if(renderable instanceof MobCell cell)
			{
	            pGuiGraphics.enableScissor(0, this.height - TAMBSScreen.TAB_HEIGHT, this.width, this.height);
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
		if(this.getCurrentTab() instanceof TAMBSTab tab && !this.collapseButton.isMouseOver(pMouseX, pMouseY))
		{
			tab.mouseClicked(pMouseX, pMouseY, pButton);
		}
		for(GuiEventListener eventListener : this.children()) 
		{
			if(eventListener instanceof MobCell && !this.isCollapsed && pMouseY <= this.height - TAMBSScreen.TAB_HEIGHT)
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
		if(this.getCurrentTab() instanceof TAMBSTab tab && !this.collapseButton.isMouseOver(pMouseX, pMouseY))
		{
			tab.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
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
		if(this.getCurrentTab() instanceof TAMBSTab tab)
		{
			tab.mouseScrolled(pMouseX, pMouseY, pDelta);
		}
		return super.mouseScrolled(pMouseX, pMouseY, pDelta);
	}
	
	@Override
	public void mouseMoved(double pMouseX, double pMouseY) 
	{
		if(this.getCurrentTab() instanceof TAMBSTab tab)
		{
			tab.mouseMoved(pMouseX, pMouseY);
		}
		super.mouseMoved(pMouseX, pMouseY);
	}

	@Override
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) 
	{
		this.minecraft.mouseHandler.releaseMouse();
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}
	
	public Tab getCurrentTab()
	{
		return this.tabManager.getCurrentTab();
	}

	public boolean isCollapsed()
	{
		return this.isCollapsed;
	}
}