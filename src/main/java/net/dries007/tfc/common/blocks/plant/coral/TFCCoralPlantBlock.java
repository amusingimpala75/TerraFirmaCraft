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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.world.WorldAccess;

/**
 * {@link net.minecraft.block.CoralBlock}
 */
public class TFCCoralPlantBlock extends TFCAbstractCoralPlantBlock
{
    private final Supplier<? extends Block> deadBlock;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public TFCCoralPlantBlock(Supplier<? extends Block> deadBlock, AbstractBlock.Settings properties)
    {
        super(properties);
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
            this.tryScheduleDieTick(stateIn, worldIn, currentPos);
            if (stateIn.get(getFluidProperty()).getFluid().isIn(FluidTags.WATER))
            {
                worldIn.getFluidTickScheduler().schedule(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickRate(worldIn));
            }

            return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }
}
