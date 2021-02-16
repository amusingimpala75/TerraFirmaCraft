/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import net.dries007.tfc.common.blocks.FluidBlockStateProprties;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

/**
 * {@link net.minecraft.block.SeaPickleBlock}
 */
public class TFCSeaPickleBlock extends Block implements IFluidLoggable
{
    public static final IntProperty PICKLES = Properties.PICKLES;
    public static final FluidProperty FLUID = FluidBlockStateProprties.SALT_WATER;
    protected static final VoxelShape ONE_AABB = Block.createCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    protected static final VoxelShape TWO_AABB = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
    protected static final VoxelShape THREE_AABB = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    protected static final VoxelShape FOUR_AABB = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

    public TFCSeaPickleBlock(AbstractBlock.Settings properties)
    {
        super(properties);
        setDefaultState(getStateManager().getDefaultState().with(PICKLES, 1));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState blockstate = context.getWorld().getBlockState(context.getBlockPos());
        if (blockstate.isOf(this))
        {
            return blockstate.with(PICKLES, Math.min(4, blockstate.get(PICKLES) + 1));
        }
        else
        {
            FluidState fluidstate = context.getWorld().getFluidState(context.getBlockPos());
            boolean flag = fluidstate.getFluid() == TFCFluids.SALT_WATER.getSource();
            return getDefaultState().with(getFluidProperty(), flag ? getFluidProperty().keyFor(TFCFluids.SALT_WATER.getSource()) : getFluidProperty().keyFor(Fluids.EMPTY));
        }
    }

    public static boolean isDead(BlockState state)
    {
        FluidProperty property = ((TFCSeaPickleBlock) state.getBlock()).getFluidProperty();
        return state.get(property) == property.keyFor(Fluids.EMPTY);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canReplace(BlockState state, ItemPlacementContext useContext)
    {
        return useContext.getStack().getItem() == this.asItem() && state.get(PICKLES) < 4 || super.canReplace(state, useContext);
    }

    protected boolean mayPlaceOn(BlockState state, BlockView worldIn, BlockPos pos)
    {
        return !state.getCollisionShape(worldIn, pos).getFace(Direction.UP).isEmpty() || state.isSideSolidFullSquare(worldIn, pos, Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.down();
        return mayPlaceOn(worldIn.getBlockState(blockpos), worldIn, blockpos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        switch (state.get(PICKLES))
        {
            case 1:
            default:
                return ONE_AABB;
            case 2:
                return TWO_AABB;
            case 3:
                return THREE_AABB;
            case 4:
                return FOUR_AABB;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canPlaceAt(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            if (stateIn.get(getFluidProperty()).getFluid() != Fluids.EMPTY)
            {
                worldIn.getFluidTickScheduler().schedule(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickRate(worldIn));
            }

            return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(PICKLES, getFluidProperty());
    }
}
