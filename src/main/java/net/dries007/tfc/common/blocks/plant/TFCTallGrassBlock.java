/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class TFCTallGrassBlock extends ShortGrassBlock implements ITallPlant
{
    protected static final EnumProperty<Part> PART = TFCBlockStateProperties.TALL_PLANT_PART;
    protected static final VoxelShape PLANT_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHORTER_PLANT_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);

    public static TFCTallGrassBlock create(IPlant plant, Settings properties)
    {
        return new TFCTallGrassBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected TFCTallGrassBlock(Settings properties)
    {
        super(properties);

        setDefaultState(getStateManager().getDefaultState().with(PART, Part.LOWER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        Part part = stateIn.get(PART);
        if (facing.getAxis() != Direction.Axis.Y || part == Part.LOWER != (facing == Direction.UP) || facingState.getBlock() == this && facingState.get(PART) != part)
        {
            return part == Part.LOWER && facing == Direction.DOWN && !stateIn.canPlaceAt(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        else
        {
            return Blocks.AIR.getDefaultState();
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        if (state.get(PART) == Part.LOWER)
        {
            return super.canPlaceAt(state, worldIn, pos);
        }
        else
        {
            BlockState blockstate = worldIn.getBlockState(pos.down());
            if (state.getBlock() != this)
            {
                return super.canPlaceAt(state, worldIn, pos); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            }
            return blockstate.getBlock() == this && blockstate.get(PART) == Part.LOWER;
        }
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockPos pos = context.getBlockPos();
        return pos.getY() < 255 && context.getWorld().getBlockState(pos.up()).canReplace(context) ? super.getPlacementState(context) : null;
    }

    @Override
    public void onPlaced(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        worldIn.setBlockState(pos.up(), getDefaultState().with(PART, Part.UPPER));
    }

    @Override
    public void onBreak(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if (!worldIn.isClient)
        {
            if (player.isCreative())
            {
                if (state.get(PART) == Part.UPPER)
                {
                    BlockPos blockpos = pos.down();
                    BlockState blockstate = worldIn.getBlockState(blockpos);
                    if (blockstate.getBlock() == state.getBlock() && blockstate.get(PART) == Part.LOWER)
                    {
                        worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
                        worldIn.syncWorldEvent(player, 2001, blockpos, Block.getRawIdFromState(blockstate));
                    }
                }
            }
            else
            {
                dropStacks(state, worldIn, pos, null, player, player.getMainHandStack());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        Part part = state.get(PART);
        if (part == Part.LOWER)
            return PLANT_SHAPE;
        return SHORTER_PLANT_SHAPE;
    }

    @Override
    public OffsetType getOffsetType()
    {
        return OffsetType.XYZ;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(PART);
    }

    public void placeTwoHalves(WorldAccess world, BlockPos pos, int flags, Random random)
    {
        int age = random.nextInt(3) + 1;
        world.setBlockState(pos, updateStateWithCurrentMonth(getDefaultState().with(TFCBlockStateProperties.TALL_PLANT_PART, Part.LOWER).with(TFCBlockStateProperties.AGE_3, age)), flags);
        world.setBlockState(pos.up(), updateStateWithCurrentMonth(getDefaultState().with(TFCBlockStateProperties.TALL_PLANT_PART, Part.UPPER).with(TFCBlockStateProperties.AGE_3, age)), flags);
    }
}
