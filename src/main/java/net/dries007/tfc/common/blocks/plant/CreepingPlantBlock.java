/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

public abstract class CreepingPlantBlock extends PlantBlock
{
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;

    protected static final VoxelShape UP_SHAPE = createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape DOWN_SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    protected static final VoxelShape EAST_SHAPE = createCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = createCuboidShape(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_SHAPE = createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);

    protected static final Map<BooleanProperty, VoxelShape> SHAPES_BY_PROPERTY = ImmutableMap.<BooleanProperty, VoxelShape>builder().put(UP, UP_SHAPE).put(DOWN, DOWN_SHAPE).put(NORTH, NORTH_SHAPE).put(SOUTH, SOUTH_SHAPE).put(EAST, EAST_SHAPE).put(WEST, WEST_SHAPE).build();

    public static CreepingPlantBlock create(IPlant plant, Settings properties)
    {
        return new CreepingPlantBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected final Map<BlockState, VoxelShape> shapeCache;

    protected CreepingPlantBlock(Settings properties)
    {
        super(properties);

        shapeCache = getStateManager().getStates().stream().collect(Collectors.toMap(state -> state, state -> SHAPES_BY_PROPERTY.entrySet().stream().filter(entry -> state.get(entry.getKey())).map(Map.Entry::getValue).reduce(VoxelShapes::union).orElseGet(VoxelShapes::empty)));

        setDefaultState(getDefaultState().with(UP, false).with(DOWN, false).with(EAST, false).with(WEST, false).with(NORTH, false).with(SOUTH, false));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction direction, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        stateIn = stateIn.with(ConnectingBlock.FACING_PROPERTIES.get(direction), facingState.isIn(TFCTags.Blocks.CREEPING_PLANTABLE_ON));
        return isEmpty(stateIn) ? Blocks.AIR.getDefaultState() : stateIn;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (Direction direction : FACINGS)
        {
            if (worldIn.getBlockState(mutablePos.set(pos, direction)).isIn(TFCTags.Blocks.CREEPING_PLANTABLE_ON))
            {
                return true;
            }
        }
        return false;
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
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return shapeCache.get(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST));
    }

    @NotNull
    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return updateStateFromSides(context.getWorld(), context.getBlockPos(), updateStateWithCurrentMonth(getDefaultState()));
    }

    private BlockState updateStateFromSides(WorldAccess world, BlockPos pos, BlockState state)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean hasEarth = false;
        for (Direction direction : FACINGS)
        {
            mutablePos.set(pos, direction);
            boolean ground = world.getBlockState(mutablePos).isIn(TFCTags.Blocks.CREEPING_PLANTABLE_ON);

            state = state.with(ConnectingBlock.FACING_PROPERTIES.get(direction), ground);
            hasEarth |= ground;
        }
        return hasEarth ? state : Blocks.AIR.getDefaultState();
    }

    private boolean isEmpty(BlockState state)
    {
        for (BooleanProperty property : SHAPES_BY_PROPERTY.keySet())
        {
            if (state.get(property))
            {
                return false;
            }
        }
        return true;
    }
}
