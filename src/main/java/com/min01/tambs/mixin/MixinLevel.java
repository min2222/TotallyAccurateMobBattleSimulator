package com.min01.tambs.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.min01.tambs.client.TAMBSClientData;
import com.min01.tambs.util.TAMBSClientUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

@Mixin(Level.class)
public class MixinLevel 
{
    @WrapMethod(method = "destroyBlock")
    private boolean tambs$destroyBlock(BlockPos pPos, boolean pDropBlock, Entity pEntity, int pRecursionLeft, Operation<Boolean> original)
    {
        BlockState old = ((Level) (Object) this).getBlockState(pPos);
    	if(TAMBSClientUtil.isMobBattleMode() && TAMBSClientData.INSTANCE.contains(TAMBSClientData.INSTANCE.mob_griefing, ForgeRegistries.BLOCKS.getKey(old.getBlock())))
    	{
            return false;
    	}
        return original.call(pPos, pDropBlock, pEntity, pRecursionLeft);
    }
    
    @WrapMethod(method = "removeBlock")
    private boolean tambs$removeBlock(BlockPos pPos, boolean pIsMoving, Operation<Boolean> original)
    {
        BlockState old = ((Level) (Object) this).getBlockState(pPos);
    	if(TAMBSClientUtil.isMobBattleMode() && TAMBSClientData.INSTANCE.contains(TAMBSClientData.INSTANCE.mob_griefing, ForgeRegistries.BLOCKS.getKey(old.getBlock())))
    	{
            return false;
    	}
        return original.call(pPos, pIsMoving);
    }
    
    @WrapMethod(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z")
    private boolean tambs$setBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft, Operation<Boolean> original)
    {
        BlockState old = ((Level) (Object) this).getBlockState(pPos);
    	if(TAMBSClientUtil.isMobBattleMode())
    	{
    		if(TAMBSClientData.INSTANCE.contains(TAMBSClientData.INSTANCE.mob_griefing, ForgeRegistries.BLOCKS.getKey(pState.getBlock())) || TAMBSClientData.INSTANCE.contains(TAMBSClientData.INSTANCE.mob_griefing, ForgeRegistries.BLOCKS.getKey(old.getBlock())))
    		{
                return false;
    		}
    	}
        return original.call(pPos, pState, pFlags, pRecursionLeft);
    }
}
