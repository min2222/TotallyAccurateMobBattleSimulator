package com.min01.tambs.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CheckEditBox extends SuggestionEditBox 
{
    public final List<String> checked = new ArrayList<>();
    
    public CheckEditBox(Font font, int x, int y, int width, int height, Component message, int maxLimit, Collection<SuggestionProvider> suggestions)
    {
		super(font, x, y, width, height, message, maxLimit, suggestions);
	}
    
	public static Collection<SuggestionProvider> mobEffects() 
    {
        return ForgeRegistries.MOB_EFFECTS.getKeys().stream().<SuggestionProvider>map(t -> new SuggestionProvider() 
        {
            @Override
            public boolean matches(String query)
            {
            	String modId = t.getNamespace();
            	boolean isModId = query.startsWith("@") && modId.startsWith(query.replace("@", ""));
            	return t.getPath().startsWith(query) || isModId;
            }
            
            @Override
            public String getString() 
            {
            	return t.toString();
            }
        }).toList();
    }

	public static Collection<SuggestionProvider> entities() 
    {
        return ForgeRegistries.ENTITY_TYPES.getKeys().stream().filter(t -> ForgeRegistries.ENTITY_TYPES.getValue(t).canSummon()).<SuggestionProvider>map(t -> new SuggestionProvider() 
        {
            @Override
            public boolean matches(String query)
            {
            	EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(t);
            	String modId = t.getNamespace();
            	boolean isModId = query.startsWith("@") && modId.startsWith(query.replace("@", ""));
            	boolean isTag = query.startsWith("#") && type.is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(query.replaceAll("[^a-zA-Z0-9:]", "").toLowerCase())));
            	return t.getPath().startsWith(query) || isModId || isTag;
            }
            
            @Override
            public String getString() 
            {
            	return t.toString();
            }
        }).toList();
    }
	
	public static Collection<SuggestionProvider> blocks() 
    {
        return ForgeRegistries.BLOCKS.getKeys().stream().<SuggestionProvider>map(t -> new SuggestionProvider() 
        {
            @Override
            public boolean matches(String query)
            {
            	Block block = ForgeRegistries.BLOCKS.getValue(t);
            	String modId = t.getNamespace();
            	boolean isModId = query.startsWith("@") && modId.startsWith(query.replace("@", ""));
            	boolean isTag = query.startsWith("#") && block.defaultBlockState().is(TagKey.create(Registries.BLOCK, ResourceLocation.parse(query.replaceAll("[^a-zA-Z0-9:]", "").toLowerCase())));
            	return t.getPath().startsWith(query) || isModId || isTag;
            }
            
            @Override
            public String getString() 
            {
            	return t.toString();
            }
        }).toList();
    }

	@Override
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
        	pGuiGraphics.drawString(this.font, suggestion, this.rect.getX() + 11, this.rect.getY() + 2 + 12 * index, index + this.offset == this.current ? -256 : -5592406);
        	if(this.checked.contains(suggestion))
        	{
            	pGuiGraphics.drawString(this.font, Component.literal("✓"), this.rect.getX() + 2, this.rect.getY() + 2 + 12 * index, index + this.offset == this.current ? -256 : -5592406);
        	}
        }
        pGuiGraphics.pose().popPose();
	}
	
	@Override
	public void useSuggestion() 
	{
    	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		String suggestion = this.filtered.get(this.current).getString();
        if(this.checked.contains(suggestion))
        {
        	this.checked.remove(suggestion);
        }
        else
        {
        	this.checked.add(suggestion);
        }
	}
}