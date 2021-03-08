/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * {@link net.minecraft.block.CoralWallFanBlock}
 */
public class LivingCoralWallFanBlock extends CoralWallFanBlock
{
    private final Supplier<? extends Block> deadBlock;

    public LivingCoralWallFanBlock(Supplier<? extends Block>  deadBlock, AbstractBlock.Settings builder)
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
            worldIn.setBlockState(pos, deadBlock.get().getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).with(FACING, state.get(FACING)), 2);
        }

    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getOpposite() == stateIn.get(FACING) && !stateIn.canPlaceAt(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            if (stateIn.get(getFluidProperty()).getFluid().isIn(FluidTags.WATER))
            {
                worldIn.getFluidTickScheduler().schedule(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickRate(worldIn));
            }

            this.tryScheduleDieTick(stateIn, worldIn, currentPos);
            return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }
}
