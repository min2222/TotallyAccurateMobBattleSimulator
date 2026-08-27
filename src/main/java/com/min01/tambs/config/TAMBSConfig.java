package com.min01.tambs.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TAMBSConfig 
{
	public static final ForgeConfigSpec CONFIG_SPEC = build();
	
    public static ForgeConfigSpec build() 
    {
    	ForgeConfigSpec.Builder config = new ForgeConfigSpec.Builder();
        return config.build();
    }
}
