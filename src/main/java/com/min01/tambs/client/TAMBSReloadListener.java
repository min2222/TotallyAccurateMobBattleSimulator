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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.fml.loading.FMLPaths;

public class TAMBSReloadListener implements ResourceManagerReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
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
        public String mouse_distance = "200";
        public String play_speed = "1.0";
        public String fly_speed = "1.0";
        public List<String> bookmarks = new ArrayList<>();
        public FilterCategory overlay = new FilterCategory();
        public FilterCategory mob_griefing = new FilterCategory();
        public FilterCategory mob_kill = new FilterCategory();
        
        public boolean isBookmarked(ResourceLocation name)
        {
        	return this.bookmarks.contains(name.toString());
        }
        
        public void bookmark(ResourceLocation name, boolean remove)
        {
        	if(remove)
        	{
            	this.bookmarks.remove(name.toString());
        	}
        	else
        	{
        		this.bookmarks.add(name.toString());
        	}
        }
    }

    public static class FilterCategory 
    {
        public String mode = "blacklist";
        public List<String> blacklist = new ArrayList<>();
        public List<String> whitelist = new ArrayList<>();

        public boolean filter(ResourceLocation location)
        {
        	if(this.mode.equals("whitelist") && !this.isWhitelisted(location))
        	{
        		return true;
        	}
        	if(this.mode.equals("blacklist") && this.isBlacklisted(location))
        	{
        		return true;
        	}
        	return false;
        }
        public boolean isBlacklisted(ResourceLocation location) 
        {
            return this.blacklist != null && this.blacklist.contains(location.toString());
        }

        public boolean isWhitelisted(ResourceLocation location)
        {
            return this.whitelist != null && this.whitelist.contains(location.toString());
        }
    }
}
