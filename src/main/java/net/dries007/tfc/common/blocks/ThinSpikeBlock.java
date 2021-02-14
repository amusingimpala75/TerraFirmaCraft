/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ThinSpikeBlock extends Block
{
    public static final VoxelShape PILLAR_SHAPE = VoxelShapes.union(
        createCuboidShape(9.5, 0, 12.5, 11.5, 16, 14.5),
        createCuboidShape(8, 0, 1, 11, 16, 4),
        createCuboidShape(3.5, 0, 1.5, 5.5, 16, 3.5),
        createCuboidShape(4, 0, 11, 7, 16, 14),
        createCuboidShape(2.5, 0, 8.5, 4.5, 16, 10.5),
        createCuboidShape(9.5, 0, 4.5, 11.5, 16, 6.5),
        createCuboidShape(11, 0, 8, 14, 16, 11),
        createCuboidShape(4, 0, 4, 8, 16, 8)
    );

    public static final VoxelShape TIP_SHAPE = VoxelShapes.union(
        createCuboidShape(5, 4, 12, 6, 8, 13),
        createCuboidShape(4, 12, 11, 7, 16, 14),
        createCuboidShape(4.5, 8, 11.5, 6.5, 12, 13.5),
        createCuboidShape(9, 4, 2, 10, 8, 3),
        createCuboidShape(8, 12, 1, 11, 16, 4),
        createCuboidShape(8.5, 8, 1.5, 10.5, 12, 3.5),
        createCuboidShape(5, 2, 5, 7, 7, 7),
        createCuboidShape(4, 11, 4, 8, 16, 8),
        createCuboidShape(4.5, 6, 4.5, 7.5, 11, 7.5),
        createCuboidShape(12, 5, 9, 13, 9, 10),
        createCuboidShape(11, 13, 8, 14, 16, 11),
        createCuboidShape(11.5, 9, 8.5, 13.5, 13, 10.5),
        createCuboidShape(10, 6, 5, 11, 12, 6),
        createCuboidShape(9.5, 12, 4.5, 11.5, 16, 6.5),
        createCuboidShape(3, 10, 9, 4, 14, 10),
        createCuboidShape(2.5, 14, 8.5, 4.5, 16, 10.5),
        createCuboidShape(4, 10, 2, 5, 13, 3),
        createCuboidShape(3.5, 13, 1.5, 5.5, 16, 3.5),
        createCuboidShape(10, 9, 13, 11, 14, 14),
        createCuboidShape(9.5, 14, 12.5, 11.5, 16, 14.5)
    );

    public static final BooleanProperty TIP = TFCBlockStateProperties.TIP;

    public ThinSpikeBlock(Settings properties)
    {
        super(properties);

        setDefaultState(getDefaultState().with(TIP, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (!canPlaceAt(state, worldIn, pos))
        {
            worldIn.breakBlock(pos, false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        BlockPos posDown = pos.down();
        BlockState otherState = worldIn.getBlockState(posDown);
        if (otherState.getBlock() == this)
        {
            worldIn.getBlockTickScheduler().schedule(posDown, this, 0);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockPos abovePos = pos.up();
        BlockState aboveState = worldIn.getBlockState(abovePos);
        return (aboveState.getBlock() == this && !aboveState.get(TIP)) || aboveState.isSideSolidFullSquare(worldIn, abovePos, Direction.DOWN);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.DOWN && !facingState.isOf(this))
        {
            return stateIn.with(TIP, true);
        }
        return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return state.get(TIP) ? TIP_SHAPE : PILLAR_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        worldIn.breakBlock(pos, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(TIP);
    }
}
