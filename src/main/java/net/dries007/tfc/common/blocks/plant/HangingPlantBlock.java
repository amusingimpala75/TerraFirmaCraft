/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;


import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class HangingPlantBlock extends PlantBlock
{
    protected static final BooleanProperty HANGING = Properties.HANGING;
    protected static final VoxelShape NOT_HANGING_SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);

    public static HangingPlantBlock create(IPlant plant, Settings properties)
    {
        return new HangingPlantBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected HangingPlantBlock(Settings properties)
    {
        super(properties);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        for (Direction direction : Direction.Type.VERTICAL)
        {
            BlockState attach = worldIn.getBlockState(currentPos.offset(direction));
            if (attach.getMaterial() == Material.LEAVES)
            {
                return stateIn.with(HANGING, direction == Direction.UP);
            }
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        for (Direction direction : Direction.Type.VERTICAL)
        {
            if (worldIn.getBlockState(pos.offset(direction)).getMaterial() == Material.LEAVES)
            {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        if (context.getWorld().getBlockState(context.getBlockPos().offset(Direction.UP)).getMaterial() == Material.LEAVES)
        {
            return getDefaultState().with(HANGING, true);
        }
        if (context.getWorld().getBlockState(context.getBlockPos().offset(Direction.DOWN)).getMaterial() == Material.LEAVES)
        {
            return getDefaultState().with(HANGING, false);
        }
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        if (state.get(HANGING))
        {
            return super.getOutlineShape(state, worldIn, pos, context);
        }
        else
        {
            return NOT_HANGING_SHAPE;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(HANGING);
    }
}
