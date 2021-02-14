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
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.world.WorldAccess;

public abstract class TFCKelpTopBlock extends TopPlantBlock implements IFluidLoggable
{
    private final Supplier<? extends Block> bodyBlock;

    public static TFCKelpTopBlock create(AbstractBlock.Settings properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape, FluidProperty fluid)
    {
        return new TFCKelpTopBlock(properties, bodyBlock, direction, shape)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected TFCKelpTopBlock(AbstractBlock.Settings properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape)
    {
        super(properties, bodyBlock, direction, shape);
        this.bodyBlock = bodyBlock;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        World world = context.getWorld();
        BlockState state = getDefaultState().with(AGE, world.getRandom().nextInt(25));
        FluidState fluidState = world.getFluidState(context.getBlockPos());
        if (getFluidProperty().canContain(fluidState.getFluid()))
        {
            return state.with(getFluidProperty(), getFluidProperty().keyFor(fluidState.getFluid()));
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == growthDirection.getOpposite() && !stateIn.canPlaceAt(worldIn, currentPos))
        {
            worldIn.getBlockTickScheduler().schedule(currentPos, this, 1);
        }
        if (facing != growthDirection || !facingState.isOf(this) && !facingState.isOf(getPlant()))
        {
            //Not sure if this is necessary
            Fluid fluid = stateIn.getFluidState().getFluid();
            worldIn.getFluidTickScheduler().schedule(currentPos, fluid, fluid.getTickRate(worldIn));
            return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        else// this is where it converts the top block to a body block when it gets placed on top of another top block
        {
            return this.getPlant().getDefaultState().with(getFluidProperty(), stateIn.get(getFluidProperty()));
        }
    }

    @Override
    protected boolean chooseStemState(BlockState state)
    {
        Fluid fluid = state.getFluidState().getFluid();
        return getFluidProperty().canContain(fluid) && fluid != Fluids.EMPTY;
    }

    @Override
    protected boolean canAttachTo(Block blockIn)
    {
        return blockIn != Blocks.MAGMA_BLOCK;
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
    public boolean canPlaceLiquid(BlockView worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        return false;
    }

    @Override
    public boolean placeLiquid(WorldAccess worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        return false;
    }

    @Override
    protected AbstractPlantBlock getPlant()
    {
        return (AbstractPlantBlock) bodyBlock.get();
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType()
    {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        VoxelShape voxelshape = super.getOutlineShape(state, worldIn, pos, context);
        Vec3d vector3d = state.getModelOffset(worldIn, pos);
        return voxelshape.offset(vector3d.x, vector3d.y, vector3d.z);
    }
}
