package com.min01.tambs.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

//CommandSuggestions;
public class SuggestionEditBox extends EditBox
{
	protected final Font font;
	protected Collection<SuggestionProvider> suggestions;

	protected Rect2i rect;
	protected final int limit;
	protected final int lineStartOffset;

	protected int offset;
	protected int current;
	protected boolean hidden;
	protected List<SuggestionProvider> filtered = new ArrayList<>();
	protected Consumer<String> externalResponder;

	public SuggestionEditBox(Font font, int x, int y, int width, int height, Component message, int limit, Collection<SuggestionProvider> suggestions)
	{
		this(font, x, y, width, height, message, limit, 1, suggestions);
	}

	public SuggestionEditBox(Font font, int x, int y, int width, int height, Component message, int limit, int lineStartOffset, Collection<SuggestionProvider> suggestions)
	{
		super(font, x, y, width, height, message);
		this.font = font;
		this.limit = limit;
		this.lineStartOffset = lineStartOffset;
		this.suggestions = suggestions;
		super.setResponder(this::onEdited);
		this.updateFiltered(this.getValue());
	}

	public static List<SuggestionProvider> of(Collection<String> strings)
	{
		return strings.stream().<SuggestionProvider>map(t -> new SuggestionProvider()
		{
			@Override
			public boolean matches(String query)
			{
				return t.startsWith(query);
			}

			@Override
			public String getString()
			{
				return t;
			}
		}).toList();
	}

	@Override
	public void setResponder(Consumer<String> pResponder)
	{
		this.externalResponder = pResponder;
	}

	private void onEdited(String value)
	{
		this.hidden = false;
		this.updateFiltered(value);
		if(this.externalResponder != null)
		{
			this.externalResponder.accept(value);
		}
	}

	private void updateFiltered(String query)
	{
	    this.filtered = this.suggestions.stream().filter(t -> t.matches(query)).collect(Collectors.toList());
	    this.offset = 0;
	    this.select(0);
	    this.hidden = this.filtered.isEmpty();
	    int height = Math.min(this.filtered.size(), this.limit) * 12;
	    this.rect = new Rect2i(this.getX() - 1, this.getY() - 3 - height, this.width + 100, height);
	}

	@Override
	public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.renderSuggestions(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	}
	
	public void renderSuggestions(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
	{
		if(!this.isFocused() || this.hidden)
			return;
        int size = Math.min(this.filtered.size(), this.limit);
        PoseStack stack = pGuiGraphics.pose();
        stack.pushPose();
        stack.translate(0.0F, 0.0F, 50.0F);
        for(int index = 0; index < size; ++index)
        {
        	String suggestion = this.filtered.get(index + this.offset).getString();
        	pGuiGraphics.fill(this.rect.getX(), this.rect.getY() + 12 * index, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * index + 12, -805306368);
        	if(pMouseX > this.rect.getX() && pMouseX < this.rect.getX() + this.rect.getWidth() && pMouseY > this.rect.getY() + 12 * index && pMouseY < this.rect.getY() + 12 * index + 12) 
        	{
    			this.select(index + this.offset);
        	}
        	pGuiGraphics.drawString(this.font, suggestion, this.rect.getX() + 1, this.rect.getY() + 2 + 12 * index, index + this.offset == this.current ? -256 : -5592406);
        }
        pGuiGraphics.pose().popPose();
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
	    boolean flag = super.mouseClicked(pMouseX, pMouseY, pButton);
	    if(!flag)
	    {
	    	return this.mouseClickedSuggestions(pMouseX, pMouseY, pButton);
	    }
	    return flag;
	}

	public boolean mouseClickedSuggestions(double pMouseX, double pMouseY, int pButton)
	{
	    if(!this.isFocused() || this.hidden)
	    {
	        return false;
	    }
	    if(!this.rect.contains((int) pMouseX, (int) pMouseY))
	    {
	        return false;
	    }
	    int i = (int) ((pMouseY - this.rect.getY()) / 12 + this.offset);
	    if(i >= 0 && i < this.filtered.size())
	    {
	        this.select(i);
	        this.useSuggestion();
	    }
	    return true;
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
	{
    	if(this.rect.contains((int) pMouseX, (int) pMouseY))
    	{
    		pDelta = Mth.clamp(pDelta, -1.0D, 1.0D);
    		this.offset = (int) Mth.clamp(this.offset - pDelta, 0, Math.max(this.filtered.size() - this.limit, 0));
    		return true;
    	}
		return super.mouseScrolled(pMouseX, pMouseY, pDelta);
	}

	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
	    if(this.isFocused() && !this.hidden && this.rect.contains((int) pMouseX, (int) pMouseY))
	    {
	        return true;
	    }
	    return super.isMouseOver(pMouseX, pMouseY);
	}

	public void cycle(int pChange)
	{
        this.select(this.current + pChange);
        if(this.current < this.offset)
        {
        	this.offset = Mth.clamp(this.current, 0, Math.max(this.filtered.size() - this.limit, 0));
        } 
        else if(this.current > this.offset + this.limit - 1) 
        {
        	this.offset = Mth.clamp(this.current + this.lineStartOffset - this.limit, 0, Math.max(this.filtered.size() - this.limit, 0));
        }
	}

	public void select(int pIndex)
	{
		if(this.filtered.isEmpty())
		{
			this.current = 0;
			return;
		}
		this.current = pIndex;
		if(this.current < 0) 
		{
			this.current += this.filtered.size();
		}
		if(this.current >= this.filtered.size()) 
		{
			this.current -= this.filtered.size();
		}
	}

	public void useSuggestion()
	{
		if(this.filtered.isEmpty())
			return;
		String suggestion = this.filtered.get(this.current).getString();
		this.setValue(suggestion);
        this.setCursorPosition(suggestion.length());
        this.setHighlightPos(suggestion.length());
        this.select(this.current);
        this.hidden = true;
	}

	public void updateSuggestions(Collection<SuggestionProvider> suggestions)
	{
		this.suggestions = suggestions;
		this.updateFiltered(this.getValue());
	}
	
	public boolean isHidden()
	{
		return this.hidden;
	}

	public interface SuggestionProvider
	{
		boolean matches(String query);

		String getString();
	}
}