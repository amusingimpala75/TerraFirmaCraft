/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

public class FallenLeavesBlock extends GroundcoverBlock
{
    public static final EnumProperty<Season> SEASON = TFCBlockStateProperties.SEASON_NO_SPRING;

    private static final VoxelShape VERY_FLAT = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public FallenLeavesBlock(Settings properties)
    {
        super(properties, VERY_FLAT, null);

        setDefaultState(getStateManager().getDefaultState().with(SEASON, Season.SUMMER));
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        return true; // Not for the purposes of leaf decay, but for the purposes of seasonal updates
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        // Adjust the season based on the current time
        Season oldSeason = state.get(SEASON);
        Season newSeason = getSeasonForState();
        if (oldSeason != newSeason)
        {
            worldIn.setBlockState(pos, state.with(SEASON, newSeason));
        }
    }

    @NotNull
    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return super.getPlacementState(context).with(SEASON, getSeasonForState());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder.add(SEASON));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return VERY_FLAT;
    }

    private Season getSeasonForState()
    {
        Season season = Calendars.SERVER.getCalendarMonthOfYear().getSeason();
        return season == Season.SPRING ? Season.SUMMER : season;
    }
}
