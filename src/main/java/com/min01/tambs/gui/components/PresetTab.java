package com.min01.tambs.gui.components;

import java.util.function.Consumer;

import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.min01.tambs.client.TAMBSReloadListener.Preset;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.gui.tab.MobSelectTab;
import com.min01.tambs.util.TAMBSClientUtil;
import com.min01.tambs.util.TAMBSUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class PresetTab extends MobSelectTab
{
	protected TextOnlyButton addButton;
	protected boolean isAdd;
	protected TAMBSScreen screen;
	
	public PresetTab(TAMBSScreen screen)
	{
		super(screen, Component.translatable("tambs.tab.preset"));
		this.screen = screen;
	}
	
	@Override
	public void init(TAMBSScreen screen) 
	{
		super.init(screen);
        this.addButton = new TextOnlyButton(Button.builder(Component.literal("+"), pButton -> 
        {
        	if(this.isActive())
        	{
            	boolean add = this.isAdd;
            	if(add)
            	{
            		this.addButton.setMessage(Component.literal("+"));
            	}
            	else
            	{
            		this.addButton.setMessage(Component.literal("+").withStyle(ChatFormatting.GREEN));
            	}
    			this.isAdd = !add;
        	}
        }).bounds(this.getX() + 24, this.getY() + 16, 15, 15));
	}
	
	@Override
	public void buildGrid()
	{
		
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer) 
	{
		pConsumer.accept(this.addButton);
		super.visitChildren(pConsumer);
	}
	
	@Override
	public boolean renderEntityPreview() 
	{
		return !this.isAdd;
	}
	
	@Override
	public boolean renderBlockHighlight() 
	{
		return !this.isAdd;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(!this.isActive())
		{
			TAMBSClientUtil.hover();
		}
 		if(TAMBSClientData.SELECTED_CELL instanceof PresetCell preset && preset.isDelete)
 		{
 			TAMBSClientData.selectCell(null);
 		}
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		if(!this.isActive() && this.isAdd)
		{
			if(pButton == 0)
			{
				TAMBSClientUtil.raycast(entity -> 
				{
	            	if(entity instanceof LivingEntity living)
	            	{
	            		PresetCell cell = new PresetCell(0, this.height - TAMBSScreen.TAB_HEIGHT, this.width / COLUMN_COUNT, CELL_HEIGHT, ForgeRegistries.ENTITY_TYPES.getKey(living.getType()), TAMBSUtil.saveEntity(living));
	            		cell.visible = !this.screen.isCollapsed();
	            		cell.active = !this.screen.isCollapsed();
	            		this.all.add(cell);
	            		this.screen.refresh();
	                    this.scrollAmount = 0;
	                    this.updateCell(t -> true);
	                	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
	                	TAMBSClientData.LAST_PLACED = living.blockPosition();
	            	}
				}, t -> {});
			}
            return false;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public void load(Options options) 
	{
		for(Preset preset : options.presets)
		{
    		PresetCell cell = new PresetCell(0, this.height - TAMBSScreen.TAB_HEIGHT, this.width / COLUMN_COUNT, CELL_HEIGHT, preset);
            this.all.add(cell);
		}
	}
	
	@Override
	public void save(Options options) 
	{
		options.presets.clear();
		this.all.removeIf(t -> t instanceof PresetCell preset && preset.isDelete);
		for(MobCell cell : this.all)
		{
			if(cell instanceof PresetCell preset)
			{
				preset.save(options);
			}
		}
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(!this.isActive() && this.isAdd)
		{
			return false;
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		if(this.isActive())
		{
			this.addButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		}
	}
}
