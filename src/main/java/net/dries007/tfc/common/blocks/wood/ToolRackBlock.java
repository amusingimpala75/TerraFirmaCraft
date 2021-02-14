/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ToolRackBlock extends Block implements Waterloggable
{
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape RACK_EAST_AABB = Block.createCuboidShape(0.0D, 3.0D, 0.0D, 2.0D, 12.0D, 16.0D);
    public static final VoxelShape RACK_WEST_AABB = Block.createCuboidShape(14.0D, 3.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    public static final VoxelShape RACK_SOUTH_AABB = Block.createCuboidShape(0.0D, 3.0D, 0.0D, 16.0D, 12.0D, 2.0D);
    public static final VoxelShape RACK_NORTH_AABB = Block.createCuboidShape(0.0D, 3.0D, 14.0D, 16.0D, 12.0D, 16.0D);

    public ToolRackBlock(Settings properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing.getOpposite() == stateIn.get(FACING) && !stateIn.canPlaceAt(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else if (stateIn.get(WATERLOGGED))
        {
            worldIn.getFluidTickScheduler().schedule(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        Direction direction = state.get(FACING);
        return canAttachTo(worldIn, pos.offset(direction.getOpposite()), direction);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        switch (state.get(FACING))
        {
            case NORTH:
                return RACK_NORTH_AABB;
            case SOUTH:
                return RACK_SOUTH_AABB;
            case WEST:
                return RACK_WEST_AABB;
            case EAST:
            default:
                return RACK_EAST_AABB;
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState contextualState;
        if (!context.canReplaceExisting())
        {
            contextualState = context.getWorld().getBlockState(context.getBlockPos().offset(context.getSide().getOpposite()));
            if (contextualState.getBlock() == this && contextualState.get(FACING) == context.getSide())
            {
                return null;
            }
        }

        contextualState = getDefaultState();
        WorldView world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        FluidState fluidState = world.getFluidState(context.getBlockPos());
        Direction[] directionList = context.getPlacementDirections();

        for (Direction direction : directionList)
        {
            if (direction.getAxis().isHorizontal())
            {
                contextualState = contextualState.with(FACING, direction.getOpposite());
                if (contextualState.canPlaceAt(world, pos))
                {
                    return contextualState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
                }
            }
        }

        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, WATERLOGGED);
    }

    private boolean canAttachTo(BlockView blockReader, BlockPos pos, Direction directionIn)
    {
        BlockState blockstate = blockReader.getBlockState(pos);
        return !blockstate.emitsRedstonePower() && blockstate.isSideSolidFullSquare(blockReader, pos, directionIn);
    }
}