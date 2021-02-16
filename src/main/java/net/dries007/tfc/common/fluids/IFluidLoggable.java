/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

/**
 * A generic interface for a block which is able to contain any number of predetermined fluid properties
 *
 * @see FluidProperty
 */
public interface IFluidLoggable extends Waterloggable, FluidFillable, FluidDrainable
{
    default boolean canFillWithFluid(BlockView worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid == Fluids.EMPTY)
        {
            return getFluidProperty().getPossibleFluids().contains(fluidIn);
        }
        return false;
    }

    default boolean tryFillWithFluid(WorldAccess worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid == Fluids.EMPTY && getFluidProperty().getPossibleFluids().contains(fluidStateIn.getFluid()))
        {
            if (!worldIn.isClient())
            {
                worldIn.setBlockState(pos, state.with(getFluidProperty(), getFluidProperty().keyFor(fluidStateIn.getFluid())), 3);
                worldIn.getFluidTickScheduler().schedule(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
            }
            return true;
        }
        return false;
    }

    default Fluid tryDrainFluid(WorldAccess worldIn, BlockPos pos, BlockState state)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            worldIn.setBlockState(pos, state.with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)), 3);
        }
        return containedFluid;
    }

    /**
     * Default implementation of {@link AbstractBlock#getFluidState(BlockState)} which allows arbitrary fluids based on the contained property.
     */
    @SuppressWarnings("deprecation")
    default FluidState getFluidState(BlockState state)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid instanceof FlowableFluid)
        {
            return ((FlowableFluid) containedFluid).getStill(false);
        }
        return containedFluid.getDefaultState();
    }

    /**
     * Modifies a state with a fluid.
     * Used to place automatic fluid logged blocks during world generation.
     *
     * @param state The original state
     * @param fluid The fluid to try and insert
     * @return The state with the fluid, if allowed, otherwise the input state.
     */
    default BlockState getStateWithFluid(BlockState state, Fluid fluid)
    {
        if (getFluidProperty().getPossibleFluids().contains(fluid))
        {
            return state.with(getFluidProperty(), getFluidProperty().keyFor(fluid));
        }
        return state;
    }

    FluidProperty getFluidProperty();
}
