/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Random;

import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;
import net.minecraft.world.WorldAccess;


public abstract class TFCLeavesBlock extends Block implements ILeavesBlock
{
    public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;
    public static final EnumProperty<Season> SEASON_NO_SPRING = TFCBlockStateProperties.SEASON_NO_SPRING;

    public static TFCLeavesBlock create(Settings properties, int maxDecayDistance)
    {
        final IntProperty distanceProperty = getDistanceProperty(maxDecayDistance);
        return new TFCLeavesBlock(properties, maxDecayDistance)
        {
            @Override
            protected IntProperty getDistanceProperty()
            {
                return distanceProperty;
            }
        };
    }

    private static IntProperty getDistanceProperty(int maxDecayDistance)
    {
        if (maxDecayDistance >= 7 && maxDecayDistance < 7 + TFCBlockStateProperties.DISTANCES.length)
        {
            return TFCBlockStateProperties.DISTANCES[maxDecayDistance - 7];
        }
        throw new IllegalArgumentException("No property set for distance: " + maxDecayDistance);
    }

    /* The maximum value of the decay property. */
    private final int maxDecayDistance;

    protected TFCLeavesBlock(Settings properties, int maxDecayDistance)
    {
        super(properties);
        this.maxDecayDistance = maxDecayDistance;

        // Distance is dependent on tree species
        setDefaultState(getStateManager().getDefaultState().with(getDistanceProperty(), 1).with(PERSISTENT, false).with(SEASON_NO_SPRING, Season.SUMMER));
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        int distance = getDistance(facingState) + 1;
        if (distance != 1 || stateIn.get(getDistanceProperty()) != distance)
        {
            worldIn.getBlockTickScheduler().schedule(currentPos, this, 1);
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity(BlockState state, BlockView worldIn, BlockPos pos)
    {
        return 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        // Adjust the season based on the current time
        Season oldSeason = state.get(SEASON_NO_SPRING);
        Season newSeason = Calendars.SERVER.getCalendarMonthOfYear().getSeason();
        if (newSeason == Season.SPRING)
        {
            newSeason = Season.SUMMER; // Skip spring
        }
        if (oldSeason != newSeason)
        {
            worldIn.setBlockState(pos, state.with(SEASON_NO_SPRING, newSeason));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        int distance = updateDistance(worldIn, pos);
        if (distance > maxDecayDistance)
        {
            if (!state.get(PERSISTENT))
            {
                // Send a message, help the dev's figure out which trees need larger leaf decay radii:
                LOGGER.info("Block: {} decayed at distance {}", Registry.BLOCK.getId(state.getBlock()), distance);
                worldIn.removeBlock(pos, false);
            }
            else
            {
                worldIn.setBlockState(pos, state.with(getDistanceProperty(), maxDecayDistance), 3);
            }
        }
        else
        {
            worldIn.setBlockState(pos, state.with(getDistanceProperty(), distance), 3);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (TerraFirmaCraft.getConfig().serverConfig.blocks.leaves.enableLeavesSlowEntities)
        {
            Helpers.slowEntityInBlock(entityIn, 0.3f, 5);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        return true; // Not for the purposes of leaf decay, but for the purposes of seasonal updates
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        Season season = Calendars.get(context.getWorld()).getCalendarMonthOfYear().getSeason();
        Season newSeason = season == Season.SPRING ? Season.SUMMER : season;
        return getStateManager().getDefaultState().with(SEASON_NO_SPRING, newSeason).with(PERSISTENT, context.getPlayer() != null);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(PERSISTENT, SEASON_NO_SPRING, getDistanceProperty());
    }

    /**
     * The reason this is not a constructor parameter is because the super class (Block) will use this directly, and nothing else is initialized in time.
     */
    protected abstract IntProperty getDistanceProperty();

    private int updateDistance(WorldAccess worldIn, BlockPos pos)
    {
        int distance = 1 + maxDecayDistance;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (Direction direction : Direction.values())
        {
            mutablePos.set(pos).move(direction);
            distance = Math.min(distance, getDistance(worldIn.getBlockState(mutablePos)) + 1);
            if (distance == 1)
            {
                break;
            }
        }
        return distance;
    }

    private int getDistance(BlockState neighbor)
    {
        if (BlockTags.LOGS.contains(neighbor.getBlock()))
        {
            return 0;
        }
        else
        {
            // Check against this leaf block only, not any leaves
            return neighbor.getBlock() == this ? neighbor.get(getDistanceProperty()) : maxDecayDistance;
        }
    }
}