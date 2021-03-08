/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.Random;
import java.util.function.Supplier;

import net.dries007.tfc.common.blocks.FluidBlockStateProprties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

/**
 * {@link net.minecraft.block.CoralBlock}
 */
public class TFCCoralPlantBlock extends Block implements IFluidLoggable
{
    public static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    public static final VoxelShape BIG_SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    private final VoxelShape shape;

    public static final FluidProperty FLUID = FluidBlockStateProprties.SALT_WATER;

    public TFCCoralPlantBlock(VoxelShape shape, AbstractBlock.Settings properties)
    {
        super(properties);

        this.shape = shape;
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        FluidState fluidstate = context.getWorld().getFluidState(context.getBlockPos());
        return this.getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor((fluidstate.isIn(FluidTags.WATER) && fluidstate.getLevel() == 8) ? TFCFluids.SALT_WATER.getSource() : Fluids.EMPTY));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder.add(getFluidProperty()));
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
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }


    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
            BlockPos posBelow = pos.down();
        return worldIn.getBlockState(posBelow).isSideSolidFullSquare(worldIn, posBelow, Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return shape;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        entityIn.damage(DamageSource.CACTUS, 1.0F);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    /**
     * @link net.minecraft.block.AbstractCoralPlantBlock#tryScheduleDieTick(BlockState, IWorld, BlockPos)}
     */
    protected void tryScheduleDieTick(BlockState state, WorldAccess worldIn, BlockPos pos)
    {
        if (!scanForWater(state, worldIn, pos))
        {
            worldIn.getBlockTickScheduler().schedule(pos, this, 60 + worldIn.getRandom().nextInt(40));
        }
    }

    /**
     * @link net.minecraft.block.AbstractCoralPlantBlock#scanForWater(BlockState, IBlockReader, BlockPos)}
     */
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
}
