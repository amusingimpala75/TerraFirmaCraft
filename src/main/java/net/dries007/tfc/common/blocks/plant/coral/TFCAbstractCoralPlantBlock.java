/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import net.dries007.tfc.common.blocks.FluidBlockStateProprties;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

/**
 * {@link net.minecraft.block.CoralParentBlock}
 */
public class TFCAbstractCoralPlantBlock extends Block implements IFluidLoggable
{
    public static final FluidProperty FLUID = FluidBlockStateProprties.SALT_WATER;
    private static final VoxelShape AABB = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    public TFCAbstractCoralPlantBlock(AbstractBlock.Settings properties)
    {
        super(properties);
        setDefaultState(getStateManager().getDefaultState());
    }

    protected void tryScheduleDieTick(BlockState state, WorldAccess worldIn, BlockPos pos)
    {
        if (!scanForWater(state, worldIn, pos))
        {
            worldIn.getBlockTickScheduler().schedule(pos, this, 60 + worldIn.getRandom().nextInt(40));
        }

    }

    protected boolean scanForWater(BlockState state, BlockView worldIn, BlockPos pos)
    {
        if (state.get(getFluidProperty()).getFluid().isIn(FluidTags.WATER))
        {
            return true;
        }
        else
        {
            for (Direction direction : Direction.values())
            {
                if (worldIn.getFluidState(pos.offset(direction)).isIn(FluidTags.WATER))
                {
                    return true;
                }
            }
            return false;
        }
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        FluidState fluidstate = context.getWorld().getFluidState(context.getBlockPos());
        return this.getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor((fluidstate.isIn(FluidTags.WATER) && fluidstate.getLevel() == 8) ? TFCFluids.SALT_WATER.getSource() : Fluids.EMPTY));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.get(getFluidProperty()).getFluid().isIn(FluidTags.WATER))
        {
            worldIn.getFluidTickScheduler().schedule(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickRate(worldIn));
        }
        return facing == Direction.DOWN && !this.canPlaceAt(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.down();
        return worldIn.getBlockState(blockpos).isSideSolidFullSquare(worldIn, blockpos, Direction.UP);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        entityIn.damage(DamageSource.CACTUS, 1.0F);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(getFluidProperty());
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
}
