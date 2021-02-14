/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class WaterPlantBlock extends PlantBlock implements IFluidLoggable
{
    public static WaterPlantBlock create(IPlant plant, FluidProperty fluid, Settings properties)
    {
        return new WaterPlantBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }

            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected WaterPlantBlock(Settings properties)
    {
        super(properties);

        setDefaultState(getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockPos pos = context.getBlockPos();
        FluidState fluidState = context.getWorld().getFluidState(pos);
        BlockState state = updateStateWithCurrentMonth(getDefaultState());
        if (getFluidProperty().canContain(fluidState.getFluid()))
        {
            state = state.with(getFluidProperty(), getFluidProperty().keyFor(fluidState.getFluid()));
        }
        return state;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        return belowState.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON) && state.get(getFluidProperty()) != getFluidProperty().keyFor(Fluids.EMPTY);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(getFluidProperty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }
}
