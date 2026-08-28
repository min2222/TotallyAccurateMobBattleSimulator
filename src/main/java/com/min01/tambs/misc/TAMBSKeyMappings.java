package com.min01.tambs.misc;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class TAMBSKeyMappings
{
	public static final KeyMapping ENTER_MOB_BATTLE_MODE = new KeyMapping("key.tambs.enter_mob_battle_mode", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, InputConstants.KEY_M, "key.categories.tambs");
}
