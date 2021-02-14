/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class TallWaterPlantBlock extends TFCTallGrassBlock implements IFluidLoggable
{
    public static TallWaterPlantBlock create(IPlant plant, FluidProperty fluid, Settings properties)
    {
        return new TallWaterPlantBlock(properties)
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

    protected TallWaterPlantBlock(Settings properties)
    {
        super(properties);

        setDefaultState(getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).with(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        if (state.get(PART) == Part.LOWER)
        {
            return belowState.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
        }
        else
        {
            if (state.getBlock() != this)
            {
                return belowState.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            return belowState.getBlock() == this && belowState.get(PART) == Part.LOWER;
        }
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

        return pos.getY() < 255 && context.getWorld().getBlockState(pos.up()).canReplace(context) ? state : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(getFluidProperty());
    }

    @Override
    public void placeTwoHalves(WorldAccess world, BlockPos pos, int flags, Random random)
    {
        int age = random.nextInt(4);
        BlockState lowerState = getStateWithFluid(getDefaultState(), world.getFluidState(pos).getFluid());
        if (lowerState.get(getFluidProperty()).getFluid() == Fluids.EMPTY)
            return;
        world.setBlockState(pos, lowerState.with(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER).with(TFCBlockStateProperties.AGE_3, age), flags);
        world.setBlockState(pos.up(), getStateWithFluid(getDefaultState().with(TFCBlockStateProperties.TALL_PLANT_PART, Part.UPPER).with(TFCBlockStateProperties.AGE_3, age), world.getFluidState(pos.up()).getFluid()), flags);
    }
}
