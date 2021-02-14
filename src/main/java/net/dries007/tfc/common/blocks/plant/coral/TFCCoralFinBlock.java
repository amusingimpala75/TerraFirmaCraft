/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * {@link CoralFanBlock}
 */
public class TFCCoralFinBlock extends TFCCoralFanBlock
{
    private final Supplier<? extends Block> deadBlock;

    public TFCCoralFinBlock(Supplier<? extends Block> deadBlock, AbstractBlock.Settings builder)
    {
        super(builder);
        this.deadBlock = deadBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        tryScheduleDieTick(state, worldIn, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!scanForWater(state, worldIn, pos))
        {
            worldIn.setBlockState(pos, deadBlock.get().getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)), 2);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !stateIn.canPlaceAt(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            tryScheduleDieTick(stateIn, worldIn, currentPos);
            if (stateIn.get(getFluidProperty()).getFluid().isIn(FluidTags.WATER))
            {
                worldIn.getFluidTickScheduler().schedule(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            }
            return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }
}
