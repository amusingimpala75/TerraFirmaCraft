/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.world.WorldAccess;

public abstract class TFCKelpBlock extends BodyPlantBlock implements IFluidLoggable
{
    private final Supplier<? extends Block> headBlock;

    public static TFCKelpBlock create(AbstractBlock.Settings properties, Supplier<? extends Block> headBlock, Direction direction, VoxelShape shape, FluidProperty fluid)
    {
        return new TFCKelpBlock(properties, headBlock, shape, direction)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected TFCKelpBlock(AbstractBlock.Settings properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties, headBlock, shape, direction);
        this.headBlock = headBlock;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == this.growthDirection.getOpposite() && !stateIn.canPlaceAt(worldIn, currentPos))
        {
            worldIn.getBlockTickScheduler().schedule(currentPos, this, 1);
        }
        //This is where vanilla assumes (wrongly) that the abstract block has correct waterlogged handling
        AbstractPlantStemBlock abstracttopplantblock = this.getHeadBlock();
        if (facing == this.growthDirection)
        {
            Block block = facingState.getBlock();
            if (block != this && block != abstracttopplantblock)
            {
                return abstracttopplantblock.getRandomGrowthState(worldIn).with(getFluidProperty(), stateIn.get(getFluidProperty()));
            }
        }
        if (this.tickWater)
        {
            worldIn.getFluidTickScheduler().schedule(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
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
    public boolean canFillWithFluid
        (BlockView worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        return false;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        return false;
    }

    protected AbstractPlantStemBlock getHeadBlock()
    {
        return (AbstractPlantStemBlock) headBlock.get();
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType()
    {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        VoxelShape voxelshape = super.getOutlineShape(state, worldIn, pos, context);
        Vec3d vector3d = state.getModelOffset(worldIn, pos);
        return voxelshape.offset(vector3d.x, vector3d.y, vector3d.z);
    }
}
