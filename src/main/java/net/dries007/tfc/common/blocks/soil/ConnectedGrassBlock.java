/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.fabric.duck.WorldDuck;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ConnectedGrassBlock extends Block implements IGrassBlock
{
    // Used to determine connected textures
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;

    public static final BooleanProperty SNOWY = Properties.SNOWY;

    private static final Map<Direction, BooleanProperty> PROPERTIES = ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.WEST, WEST, Direction.SOUTH, SOUTH);

    private final Supplier<? extends Block> dirt;
    @Nullable private final Supplier<? extends Block> grassPath;
    @Nullable private final Supplier<? extends Block> farmland;

    public ConnectedGrassBlock(Settings properties, SoilBlockType dirtType, SoilBlockType.Variant soilType)
    {
        this(properties, () -> TFCBlocks.SOIL.get(dirtType).get(soilType), () -> TFCBlocks.SOIL.get(SoilBlockType.GRASS_PATH).get(soilType), () -> TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(soilType));
    }

    public ConnectedGrassBlock(Settings properties, Supplier<? extends Block> dirt, @Nullable Supplier<? extends Block> grassPath, @Nullable Supplier<? extends Block> farmland)
    {
        super(properties.postProcess(TFCBlocks::always));

        this.dirt = dirt;
        this.grassPath = grassPath;
        this.farmland = farmland;

        setDefaultState(getStateManager().getDefaultState().with(SOUTH, false).with(EAST, false).with(NORTH, false).with(WEST, false).with(SNOWY, false));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.UP)
        {
            return stateIn.with(SNOWY, facingState.isIn(TFCTags.Blocks.SNOW));
        }
        else if (facing != Direction.DOWN)
        {
            return updateStateFromDirection(worldIn, currentPos, stateIn, facing);
        }
        return stateIn;
    }

    @Override
    public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        worldIn.getBlockTickScheduler().schedule(pos, this, 0);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            worldIn.getBlockTickScheduler().schedule(pos.offset(direction).up(), this, 0);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            worldIn.getBlockTickScheduler().schedule(pos.offset(direction).up(), this, 0);
        }
        super.onStateReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (!canBeGrass(state, worldIn, pos))
        {
            if (((WorldDuck)worldIn).inject$isAreaLoaded(pos, 3))
            {
                // Turn to not-grass
                worldIn.setBlockState(pos, getDirt());
            }
        }
        else
        {
            if (worldIn.getLightLevel(pos.up()) >= 9)
            {
                for (int i = 0; i < 4; ++i)
                {
                    BlockPos posAt = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    BlockState stateAt = worldIn.getBlockState(posAt);
                    if (stateAt.getBlock() instanceof IDirtBlock)
                    {
                        // Spread grass to others
                        BlockState grassState = ((IDirtBlock) stateAt.getBlock()).getGrass();
                        if (canPropagate(grassState, worldIn, posAt))
                        {
                            worldIn.setBlockState(posAt, updateStateFromNeighbors(worldIn, posAt, grassState));
                        }
                    }
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.setBlockState(pos, getToolModifiedState(state, world, pos, player, player.getStackInHand(hand), player.getStackInHand(hand).getItem()));
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (((WorldDuck)worldIn).inject$isAreaLoaded(pos, 2))
        {
            worldIn.setBlockState(pos, updateStateFromNeighbors(worldIn, pos, state), 2);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState stateUp = context.getWorld().getBlockState(context.getBlockPos().up());
        return updateStateFromNeighbors(context.getWorld(), context.getBlockPos(), getDefaultState()).with(SNOWY, stateUp.isOf(Blocks.SNOW_BLOCK) || stateUp.isOf(Blocks.SNOW));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST, SNOWY);
    }

    @Override
    public BlockState getDirt()
    {
        return dirt.get().getDefaultState();
    }

    @Nullable
    //@Override
    public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, Item toolType)
    {
        if (toolType.isIn(FabricToolTags.HOES) && TerraFirmaCraft.getConfig().serverConfig.blocks.farmland.enableFarmlandCreation && farmland != null)
        {
            return farmland.get().getDefaultState();
        }
        else if (toolType.isIn(FabricToolTags.SHOVELS) && TerraFirmaCraft.getConfig().serverConfig.blocks.grassPath.enableGrassPathCreation && grassPath != null)
        {
            return grassPath.get().getDefaultState();
        }
        return state;
    }

    /**
     * When a grass block changes (is placed or added), this is called to send updates to all diagonal neighbors to update their state from this one
     *
     * @param world The world
     * @param pos   The position of the changing grass block
     */
    protected void updateSurroundingGrassConnections(WorldAccess world, BlockPos pos)
    {
        if (((WorldDuck)world).inject$isAreaLoaded(pos, 2))
        {
            for (Direction direction : Direction.Type.HORIZONTAL)
            {
                BlockPos targetPos = pos.up().offset(direction);
                BlockState targetState = world.getBlockState(targetPos);
                if (targetState.getBlock() instanceof IGrassBlock)
                {
                    world.setBlockState(targetPos, updateStateFromDirection(world, targetPos, targetState, direction.getOpposite()), 2);
                }
            }
        }
    }

    /**
     * Update the state of a grass block from all horizontal directions
     *
     * @param worldIn The world
     * @param pos     The position of the grass block
     * @param state   The initial state
     * @return The updated state
     */
    protected BlockState updateStateFromNeighbors(BlockView worldIn, BlockPos pos, BlockState state)
    {
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            state = updateStateFromDirection(worldIn, pos, state, direction);
        }
        return state;
    }

    /**
     * Update the state of a grass block from the provided direction
     *
     * @param worldIn   The world
     * @param pos       The position of the grass block
     * @param stateIn   The state of the grass block
     * @param direction The direction in which to look for adjacent, diagonal grass blocks
     * @return The updated state
     */
    protected BlockState updateStateFromDirection(BlockView worldIn, BlockPos pos, BlockState stateIn, Direction direction)
    {
        return stateIn.with(PROPERTIES.get(direction), worldIn.getBlockState(pos.offset(direction).down()).getBlock() instanceof IGrassBlock);
    }
}