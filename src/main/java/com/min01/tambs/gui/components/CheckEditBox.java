package com.min01.tambs.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    
    public CheckEditBox(Font font, int x, int y, int width, int height, Component message, int maxLimit, boolean top, Collection<SuggestionContent> suggestions)
    {
		super(font, x, y, width, height, message, maxLimit, top, suggestions);
	}
    
	public static Collection<SuggestionContent> mobEffects() 
    {
        return ForgeRegistries.MOB_EFFECTS.getKeys().stream().<SuggestionContent>map(res -> new SuggestionContent() 
        {
            @Override
            public boolean matches(String input)
            {
            	String modId = res.getNamespace();
            	boolean isModId = input.startsWith("@") && modId.startsWith(input.replace("@", ""));
            	return res.getPath().startsWith(input) || isModId;
            }

            @Override
            public String asString() 
            {
                return res.toString();
            }
        }).toList();
    }

	public static Collection<SuggestionContent> entities() 
    {
        return ForgeRegistries.ENTITY_TYPES.getKeys().stream().filter(t -> ForgeRegistries.ENTITY_TYPES.getValue(t).canSummon()).<SuggestionContent>map(res -> new SuggestionContent() 
        {
            @Override
            public boolean matches(String input)
            {
            	EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(res);
            	String modId = res.getNamespace();
            	boolean isModId = input.startsWith("@") && modId.startsWith(input.replace("@", ""));
            	boolean isTag = input.startsWith("#") && type.is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(input.replaceAll("[^a-zA-Z0-9:]", "").toLowerCase())));
            	return res.getPath().startsWith(input) || isModId || isTag;
            }

            @Override
            public String asString() 
            {
                return res.toString();
            }
        }).toList();
    }
	
	public static Collection<SuggestionContent> blocks() 
    {
        return ForgeRegistries.BLOCKS.getKeys().stream().<SuggestionContent>map(res -> new SuggestionContent() 
        {
            @Override
            public boolean matches(String input)
            {
            	Block block = ForgeRegistries.BLOCKS.getValue(res);
            	String modId = res.getNamespace();
            	boolean isModId = input.startsWith("@") && modId.startsWith(input.replace("@", ""));
            	boolean isTag = input.startsWith("#") && block.defaultBlockState().is(TagKey.create(Registries.BLOCK, ResourceLocation.parse(input.replaceAll("[^a-zA-Z0-9:]", "").toLowerCase())));
            	return res.getPath().startsWith(input) || isModId || isTag;
            }

            @Override
            public String asString() 
            {
                return res.toString();
            }
        }).toList();
    }
	
	@Override
	public void renderSuggestion(GuiGraphics guiGraphics, int mouseX, int mouseY) 
	{
        if(this.suggestionsHidden() || this.suggestions.length == 0)
            return;
        if(this.suggestions.length == 1 && this.getValue().equals(this.suggestions[0]))
            return;
        int idx = this.indexFromMouse(mouseY);
        if(idx >= 0 && idx < this.suggestions.length) 
        {
            this.select(idx);
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 50);
        guiGraphics.fill(this.rect.getX(), this.rect.getY(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight(), 0xe0101010);
        int x = this.getX() + this.paddingX;
        int y = this.rect.getY() + this.paddingY;
        for(int i = 0; i < this.suggestions.length; i++) 
        {
            int idxx = (this.offset + i) % this.suggestions.length;
            if(i >= 5 || idxx >= this.suggestions.length)
                break;
            String string = this.suggestions[idxx];
            guiGraphics.drawString(this.font, string, x + 10, y + i * this.lineHeight, this.current == idxx ? 0xFFFF55 : 0xFFFFFF);
            if(this.checked.contains(string))
            {
                guiGraphics.drawString(this.font, Component.literal("✓"), x, y + i * this.lineHeight, this.current == idxx ? 0xFFFF55 : 0xFFFFFF);
            }
        }
        guiGraphics.pose().popPose();
	}
	
	@Override
	public void useSuggestion() 
	{
    	Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        String suggestion = this.suggestions[this.current];
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