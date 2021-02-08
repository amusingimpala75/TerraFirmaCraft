/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

/**
 * Grass blocks, which MUST
 * 1. react to connected texture based properties (see {@link ConnectedGrassBlock}
 * 2. can be converted to dirt
 */
public interface IGrassBlock extends ISoilBlock
{
    /**
     * Like {@link net.minecraft.block.SpreadableBlock# canBeGrass(BlockState, WorldView, BlockPos)}, but omits the requirement that snow layers only be one thick.
     * Represents if the current block state can be grass
     */
    default boolean canBeGrass(BlockState state, WorldView world, BlockPos pos)
    {
        BlockPos posUp = pos.up();
        BlockState stateUp = world.getBlockState(posUp);
        if (stateUp.isOf(Blocks.SNOW))
        {
            return true;
        }
        else if (stateUp.getFluidState().getLevel() == 8)
        {
            return false;
        }
        else
        {
            return ChunkLightProvider.getRealisticOpacity(world, state, pos, stateUp, posUp, Direction.UP, stateUp.getOpacity(world, posUp)) < world.getMaxLightLevel();
        }
    }

    /**
     * Like {@link net.minecraft.block.SpreadableBlock# canSpread(BlockState, WorldView, BlockPos)}
     * Represents if the current grass can spread to the given location.
     *
     * @param state The grass state to place
     */
    default boolean canPropagate(BlockState state, WorldView world, BlockPos pos)
    {
        BlockPos posUp = pos.up();
        return canBeGrass(state, world, pos) && !world.getFluidState(posUp).isIn(FluidTags.WATER);
    }
}
