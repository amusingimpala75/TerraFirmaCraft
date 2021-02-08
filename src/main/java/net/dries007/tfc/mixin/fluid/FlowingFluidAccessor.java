/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowableFluid.class)
public interface FlowingFluidAccessor
{
    /**
     * This method checks if fluid can flow between two positions, based on the shapes of potential waterlogged block states in the way
     * We do not override it as it queries a thread local static cache in {@link FlowableFluid}
     */
    @Invoker("receivesFlow")
    boolean invoke$canPassThroughWall(Direction direction, BlockView world, BlockPos pos, BlockState state, BlockPos adjacentPos, BlockState adjacentState);
}
