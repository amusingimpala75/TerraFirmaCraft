/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.recipes.CollapseRecipe;

@SuppressWarnings("deprecation")
public class RockSpikeBlock extends Block implements IFluidLoggable, IFallableBlock
{
    public static final EnumProperty<Part> PART = TFCBlockStateProperties.ROCK_SPIKE_PART;
    public static final FluidProperty FLUID = TFCBlockStateProperties.WATER_AND_LAVA;

    public static final VoxelShape BASE_SHAPE = createCuboidShape(2, 0, 2, 14, 16, 14);
    public static final VoxelShape MIDDLE_SHAPE = createCuboidShape(4, 0, 4, 12, 16, 12);
    public static final VoxelShape TIP_SHAPE = createCuboidShape(6, 0, 6, 10, 16, 10);

    public RockSpikeBlock(Settings properties)
    {
        super(properties);

        setDefaultState(getStateManager().getDefaultState().with(PART, Part.BASE).with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        world.getBlockTickScheduler().schedule(pos, this, 1);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        // Check support from above or below
        BlockPos belowPos = pos.down();
        BlockState belowState = worldIn.getBlockState(belowPos);
        if (belowState.getBlock() == this && belowState.get(PART).isLargerThan(state.get(PART)))
        {
            // Larger spike below. Tick that to ensure it is supported
            worldIn.getBlockTickScheduler().schedule(belowPos, this, 1);
            return;
        }
        else if (belowState.isSideSolidFullSquare(worldIn, belowPos, Direction.UP))
        {
            // Full block below, this is supported
            return;
        }

        // No support below, try above
        BlockPos abovePos = pos.up();
        BlockState aboveState = worldIn.getBlockState(abovePos);
        if (aboveState.getBlock() == this && aboveState.get(PART).isLargerThan(state.get(PART)))
        {
            // Larger spike above. Tick to ensure that it is supported
            worldIn.getBlockTickScheduler().schedule(abovePos, this, 1);
            return;
        }
        else if (aboveState.isSideSolidFullSquare(worldIn, abovePos, Direction.DOWN))
        {
            // Full block above, this is supported
            return;
        }

        // No support, so either collapse, or break
        if (TFCTags.Blocks.CAN_COLLAPSE.contains(this) && CollapseRecipe.collapseBlock(worldIn, pos, state))
        {
            worldIn.playSound(null, pos, TFCSounds.ROCK_SLIDE_SHORT, SoundCategory.BLOCKS, 0.8f, 1.0f);
        }
        else
        {
            worldIn.breakBlock(pos, true);
        }
    }

    @Override
    public void onceFinishedFalling(World worldIn, BlockPos pos, FallingBlockEntity fallingBlock)
    {
        // todo: better shatter sound
        worldIn.breakBlock(pos, false);
        worldIn.playSound(null, pos, TFCSounds.ROCK_SLIDE_SHORT, SoundCategory.BLOCKS, 0.8f, 2.0f);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        switch (state.get(PART))
        {
            case BASE:
                return BASE_SHAPE;
            case MIDDLE:
                return MIDDLE_SHAPE;
            case TIP:
            default:
                return TIP_SHAPE;
        }
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(PART, getFluidProperty());
    }

    public enum Part implements StringIdentifiable
    {
        BASE, MIDDLE, TIP;

        @Override
        public String asString()
        {
            return name().toLowerCase();
        }

        public boolean isLargerThan(Part other)
        {
            return this.ordinal() <= other.ordinal();
        }
    }
}