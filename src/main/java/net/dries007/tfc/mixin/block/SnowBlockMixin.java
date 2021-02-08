/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A series of modifications to snow blocks:
 * 1. they can melt based on the TFC climate
 * 2. allow placement on top of TFC leaves (which are non solid)
 * 3. Slow entities passing through them
 * 4. When broken, only break one layer of snow
 */
@Mixin(SnowBlock.class)
public abstract class SnowBlockMixin extends Block
{
    private SnowBlockMixin(Settings properties)
    {
        super(properties);
    }

    @Inject(method = "canPlaceAt", at = @At(value = "RETURN"), cancellable = true)
    private void inject$canSurvive(BlockState state, WorldView worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValueZ())
        {
            // Snow should not survive on ice (this adds to the big existing conditional
            BlockState belowState = worldIn.getBlockState(pos.down());
            if (belowState.isOf(TFCBlocks.SEA_ICE))
            {
                cir.setReturnValue(false);
            }
        }
        else
        {
            // Allow tfc leaves to accumulate a single layer of snow on them, despite not having a solid collision face
            if (state.get(SnowBlock.LAYERS) == 1)
            {
                BlockState stateDown = worldIn.getBlockState(pos.down());
                if (stateDown.getBlock() instanceof ILeavesBlock)
                {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At(value = "RETURN"), cancellable = true)
    private void inject$updateShape(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> cir)
    {
        // If we can't survive, see if we can survive with only one layer, to allow the above leaves check to pass instead
        if (cir.getReturnValue().isOf(Blocks.AIR) && stateIn.get(SnowBlock.LAYERS) > 1)
        {
            BlockState state = stateIn.with(SnowBlock.LAYERS, 1);
            if (state.canPlaceAt(worldIn, currentPos))
            {
                cir.setReturnValue(state);
            }
        }
    }

    @Inject(method = "randomTick", at = @At(value = "RETURN"))
    private void inject$randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (TFCConfig.SERVER.enableSnowAffectedByTemperature.get())
        {
            // Only run this if the default logic hasn't already set the block to air
            BlockState prevState = worldIn.getBlockState(pos);
            if (prevState == state && Climate.getTemperature(worldIn, pos) > Climate.SNOW_MELT_TEMPERATURE)
            {
                int layers = state.get(SnowBlock.LAYERS);
                if (layers != 8 || !worldIn.getBlockState(pos.up()).isOf(this)) // If the above block is also layers, that should decay first
                {
                    if (layers > 1)
                    {
                        worldIn.setBlockState(pos, state.with(SnowBlock.LAYERS, layers - 1));
                    }
                    else
                    {
                        dropStacks(state, worldIn, pos);
                        worldIn.removeBlock(pos, false);
                    }
                }
            }
        }
    }

    @Override
    public float getVelocityMultiplier()
    {
        if (TFCConfig.SERVER.enableSnowSlowEntities.get())
        {
            return 0.6f;
        }
        return 1.0f;
    }

    /**
     * Add behavior to snow blocks - when they are destroyed, they should only destroy one layer.
     */
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
    {
        onBreak(world, pos, state, player);
        final int prevLayers = state.get(SnowBlock.LAYERS);
        if (prevLayers > 1)
        {
            return world.setBlockState(pos, state.with(SnowBlock.LAYERS, prevLayers - 1), world.isClient ? 11 : 3);
        }
        return world.setBlockState(pos, fluid.getBlockState(), world.isClient ? 11 : 3);
    }
}
