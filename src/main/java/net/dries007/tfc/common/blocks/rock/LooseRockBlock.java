/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import org.jetbrains.annotations.NotNull;

public class LooseRockBlock extends GroundcoverBlock implements IFluidLoggable
{
    public static final IntProperty COUNT = TFCBlockStateProperties.COUNT_1_3;

    private static final VoxelShape ONE = createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    private static final VoxelShape TWO = createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    private static final VoxelShape THREE = createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);

    public LooseRockBlock(Settings properties)
    {
        super(properties, VoxelShapes.empty(), null);

        setDefaultState(getDefaultState().with(COUNT, 1));
    }

    @Override
    @NotNull
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState stateAt = context.getWorld().getBlockState(context.getBlockPos());
        if (stateAt.isOf(this))
        {
            return stateAt.with(COUNT, Math.min(3, stateAt.get(COUNT) + 1));
        }
        return super.getPlacementState(context);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder.add(COUNT));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        switch (state.get(COUNT))
        {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
        }
        throw new IllegalStateException("Unknown value for property LooseRockBlock#ROCKS: " + state.get(COUNT));
    }
}
