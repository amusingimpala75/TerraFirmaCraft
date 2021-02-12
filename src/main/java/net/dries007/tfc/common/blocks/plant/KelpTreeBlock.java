/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class KelpTreeBlock extends ConnectingBlock implements IFluidLoggable
{
    public static KelpTreeBlock create(AbstractBlock.Settings builder, FluidProperty fluid)
    {
        return new KelpTreeBlock(builder)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected KelpTreeBlock(AbstractBlock.Settings builder)
    {
        super(0.3125F, builder);
        setDefaultState(getStateManager().getDefaultState().with(NORTH, Boolean.FALSE).with(EAST, Boolean.FALSE).with(SOUTH, Boolean.FALSE).with(WEST, Boolean.FALSE).with(UP, Boolean.FALSE).with(DOWN, Boolean.FALSE).with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return getStateForPlacement(context.getWorld(), context.getBlockPos());
    }

    public BlockState getStateForPlacement(BlockView world, BlockPos pos)
    {
        Block downBlock = world.getBlockState(pos.down()).getBlock();
        Block upBlock = world.getBlockState(pos.up()).getBlock();
        Block northBlock = world.getBlockState(pos.north()).getBlock();
        Block eastBlock = world.getBlockState(pos.east()).getBlock();
        Block southBlock = world.getBlockState(pos.south()).getBlock();
        Block westBlock = world.getBlockState(pos.west()).getBlock();
        return getDefaultState()
            .with(DOWN, downBlock.isIn(TFCTags.Blocks.KELP_TREE) || downBlock.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
            .with(UP, upBlock.isIn(TFCTags.Blocks.KELP_TREE))
            .with(NORTH, northBlock.isIn(TFCTags.Blocks.KELP_TREE))
            .with(EAST, eastBlock.isIn(TFCTags.Blocks.KELP_TREE))
            .with(SOUTH, southBlock.isIn(TFCTags.Blocks.KELP_TREE))
            .with(WEST, westBlock.isIn(TFCTags.Blocks.KELP_TREE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canPlaceAt(worldIn, currentPos))
        {
            worldIn.getBlockTickScheduler().schedule(currentPos, this, 1);
            updateFluid(worldIn, stateIn, currentPos);
            return stateIn;
        }
        else
        {
            updateFluid(worldIn, stateIn, currentPos);
            boolean flag = facingState.isIn(TFCTags.Blocks.KELP_TREE) || (facing == Direction.DOWN && facingState.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON));
            return stateIn.with(FACING_PROPERTIES.get(facing), flag);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.canPlaceAt(worldIn, pos))
        {
            worldIn.breakBlock(pos, true);
        }
    }

    @Override
    public void onBreak(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        updateFluid(worldIn, state, pos);
    }

    /**
     * {@link ChorusPlantBlock#canPlaceAt(BlockState, WorldView, BlockPos)}
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            BlockPos relativePos = pos.offset(direction);
            if (worldIn.getBlockState(relativePos).getBlock().isIn(TFCTags.Blocks.KELP_BRANCH))
            {

                Block below = worldIn.getBlockState(relativePos.down()).getBlock();
                if (below.isIn(TFCTags.Blocks.KELP_BRANCH) || below.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                {
                    return true;
                }
            }
        }
        Block blockIn = belowState.getBlock();
        return blockIn.isIn(TFCTags.Blocks.KELP_BRANCH) || blockIn.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
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
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    private void updateFluid(WorldAccess world, BlockState state, BlockPos pos)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            world.getFluidTickScheduler().schedule(pos, containedFluid, containedFluid.getTickRate(world));
        }
    }
}
