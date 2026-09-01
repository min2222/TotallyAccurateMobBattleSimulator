package com.min01.tambs.client;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public class CompoundTagSerializer implements JsonSerializer<CompoundTag>, JsonDeserializer<CompoundTag> 
{
    @Override
    public JsonElement serialize(CompoundTag src, Type typeOfSrc, JsonSerializationContext context)
    {
        return new Dynamic<>(NbtOps.INSTANCE, src).convert(JsonOps.INSTANCE).getValue();
    }

    @Override
    public CompoundTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException 
    {
        Tag tag = new Dynamic<>(JsonOps.INSTANCE, json).convert(NbtOps.INSTANCE).getValue();
        if(tag instanceof CompoundTag compoundTag)
        {
            return compoundTag;
        }
        return new CompoundTag();
    }
}
