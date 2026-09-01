package com.min01.tambs.gui.tab;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.min01.tambs.client.TAMBSReloadListener.Options;
import com.min01.tambs.gui.components.MobCell;
import com.min01.tambs.gui.components.TextOnlyButton;
import com.min01.tambs.gui.screen.TAMBSScreen;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class MobSelectTab extends TAMBSTab
{
	protected static final int COLUMN_COUNT = 10;
	protected static final int CELL_HEIGHT = 70;
	
	protected final List<MobCell> all = new ArrayList<>();
	
	protected EditBox searchBox;
	protected TextOnlyButton bookmarkButton;
	protected boolean isBookmark;

	protected double scrollAmount;
	protected int innerHeight;
	
	public MobSelectTab(TAMBSScreen screen)
	{
		this(screen, Component.translatable("tambs.tab.mob_select"));
	}
	
	public MobSelectTab(TAMBSScreen screen, Component message)
	{
		super(0, screen.height - (TAMBSScreen.TAB_HEIGHT + 45), screen.width, screen.height, message);
		this.init(screen);
	}
	
	public void init(TAMBSScreen screen)
	{
        this.buildGrid();
        this.searchBox = new EditBox(Minecraft.getInstance().font, 2, screen.height - (TAMBSScreen.TAB_HEIGHT + 15), (this.width / 4) - 5, 12, Component.empty());
        this.searchBox.setResponder(this::updateSearch);
        
        this.bookmarkButton = new TextOnlyButton(Button.builder(Component.literal("☆"), pButton -> 
        {
        	if(this.isActive())
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
        	}
        }).bounds(this.getX() + 13, this.getY() + 16, 15, 15));
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(!this.isActive())
		{
			TAMBSClientUtil.hover();
		}
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) 
	{
		if(this.isActive())
		{
	        for(MobCell cell : this.all)
	        {
	            pGuiGraphics.enableScissor(0, this.height - TAMBSScreen.TAB_HEIGHT, this.width, this.height);
	            cell.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	            pGuiGraphics.disableScissor();
	        }
			this.searchBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
			this.bookmarkButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	    	pGuiGraphics.drawString(Minecraft.getInstance().font, Component.literal("🔎"), this.getX() + 5, this.getY() + 20, 0xFFFFFF);
		}
	}
	
	@Override
	public void load(Options options) 
	{
		for(MobCell cell : this.all)
		{
			cell.load(options);
		}
	}
	
	@Override
	public void save(Options options) 
	{
		for(MobCell cell : this.all)
		{
			cell.save(options);
		}
	}
	
	@Override
	public boolean renderEntityPreview()
	{
		return true;
	}
	
	@Override
	public boolean renderBlockHighlight()
	{
		return true;
	}
	
	@Override
	public void doLayout(ScreenRectangle pRectangle) 
	{
		super.doLayout(pRectangle);
		if(this.isActive())
		{
		    this.updateSearch(this.searchBox.getValue());
		}
	}
	
	@Override
	public void visitChildren(Consumer<AbstractWidget> pConsumer) 
	{
        for(MobCell cell : this.all)
        {
        	pConsumer.accept(cell);
        }
    	pConsumer.accept(this.searchBox);
    	pConsumer.accept(this.bookmarkButton);
    	if(this.isBookmark)
    	{
    	    this.updateCell(MobCell::isBookmark);
    	}
    	else
    	{
    	    this.updateCell(t -> true);
    	}
		super.visitChildren(pConsumer);
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		if(!this.isActive())
		{
			TAMBSClientUtil.placeOrRemoveMob(pButton);
			return false;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) 
	{
		if(!this.isActive())
		{
			TAMBSClientUtil.placeOrRemoveMob(pButton);
			return false;
		}
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
    {
    	if(!this.isBookmark && this.isActive())
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
        int width = this.width / COLUMN_COUNT;
        int index = 0;
        
        for(EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues())
        {
            Entity entity = type.create(Minecraft.getInstance().level);
            if(entity instanceof LivingEntity living && type.canSummon())
            {
                int column = index % COLUMN_COUNT;
                int row = index / COLUMN_COUNT;
                MobCell cell = new MobCell(column * width, this.height - TAMBSScreen.TAB_HEIGHT + (row * CELL_HEIGHT), width, CELL_HEIGHT, ForgeRegistries.ENTITY_TYPES.getKey(living.getType()));
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
    
    public void updateCell(Predicate<MobCell> predicate) 
    {
        int index = 0;
        for(MobCell cell : this.all)
        {
        	cell.active = false;
        	if(predicate.test(cell))
        	{
            	cell.active = true;
        		this.recalculateCell(index, cell);
                index++;
        	}
        }
        this.updateInnerHeight(index);
    }
    
    public void recalculateCell(int index, MobCell cell)
    {
        int width = this.width / COLUMN_COUNT;
        int column = index % COLUMN_COUNT;
        int row = index / COLUMN_COUNT;
        
        cell.setWidth(width);
        cell.setX(column * width);
        cell.setY((int)(this.height - TAMBSScreen.TAB_HEIGHT + (row * CELL_HEIGHT) - this.scrollAmount));
    }
    
    public void updateInnerHeight(int index)
    {
        this.innerHeight = ((index / COLUMN_COUNT) * CELL_HEIGHT) - TAMBSScreen.TAB_HEIGHT;
    }
}