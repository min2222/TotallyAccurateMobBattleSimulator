package com.min01.tambs;

import com.min01.tambs.config.TAMBSConfig;
import com.min01.tambs.network.TAMBSNetwork;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TAMBS.MODID)
public class TAMBS
{
	public static final String MODID = "tambs";
	
	public TAMBS(FMLJavaModLoadingContext ctx) 
	{
		TAMBSNetwork.registerMessages();
		ctx.registerConfig(Type.COMMON, TAMBSConfig.CONFIG_SPEC, "tambs.toml");
	}
}
