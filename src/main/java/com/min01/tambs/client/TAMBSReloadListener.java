package com.min01.tambs.client;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.fml.loading.FMLPaths;

public class TAMBSReloadListener implements ResourceManagerReloadListener
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(CompoundTag.class, new CompoundTagSerializer()).create();
	
	@Override
	public void onResourceManagerReload(ResourceManager pResourceManager) 
	{
		load(FMLPaths.CONFIGDIR.get());
	}

    public static void load(Path pPath)
    {
        File file = new File(pPath.toFile(), "tambs_options.json");
        if(!file.exists()) 
        {
            save(pPath);
            return;
        }
        try(FileReader reader = new FileReader(file))
        {
        	TAMBSClientData.INSTANCE = GSON.fromJson(reader, Options.class);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    public static void save(Path pPath)
    {
        File file = new File(pPath.toFile(), "tambs_options.json");
        try(FileWriter writer = new FileWriter(file))
        {
            GSON.toJson(TAMBSClientData.INSTANCE, writer);
        } 
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static class Options
    {
        public String play_speed = "1.0";
        public String fast_motion_speed = "1.0";
        public String slow_motion_speed = "1.0";
        public String fly_speed = "1.0";
        public String mouse_distance = "200";
        public String time = "1000";
        public boolean hideOnlyTambsUI = true;
        public boolean clear_arrows = true;
        public List<String> bookmarks = new ArrayList<>();
        public List<Preset> presets = new ArrayList<>();
        public List<String> mob_griefing = new ArrayList<>();
        public List<String> mob_kill = new ArrayList<>();
        public List<String> mob_effect = new ArrayList<>();
        
        public boolean contains(List<String> list, ResourceLocation name)
        {
        	return list.contains(name.toString());
        }
    }
    
    public static class Preset
    {
        public String name = "";
        public boolean bookmark = false;
        public CompoundTag tag = new CompoundTag();

        public Preset(String name, boolean bookmark, CompoundTag tag) 
        {
            this.name = name;
            this.bookmark = bookmark;
            this.tag = tag != null ? tag : new CompoundTag();
        }
    }
}
