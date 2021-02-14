/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public abstract class PlantBlock extends TFCBushBlock
{
    public static final IntProperty AGE = TFCBlockStateProperties.AGE_3;

    protected static final VoxelShape PLANT_SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public static PlantBlock create(IPlant plant, AbstractBlock.Settings properties)
    {
        return new PlantBlock(properties)
        {

            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected PlantBlock(AbstractBlock.Settings properties) {
        super(properties);

        setDefaultState(getDefaultState().with(getPlant().getStageProperty(), 0).with(AGE, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (random.nextDouble() < TFCConfig.SERVER.plantGrowthChance.get())
        {
            state = state.with(AGE, Math.min(state.get(AGE) + 1, 3));
        }
        world.setBlockState(pos, updateStateWithCurrentMonth(state));
    }

    /**
     * Gets the plant metadata for this block.
     *
     * The stage property is isolated and referenced via this as it is needed in the {@link net.minecraft.block.Block} constructor - which builds the state container, and requires all property references to be computed in {@link Block#appendProperties(StateManager.Builder)}.
     *
     * See the various {@link PlantBlock#create(IPlant, Settings)} methods and subclass versions for how to use.
     */
    public abstract IPlant getPlant();

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(getPlant().getStageProperty(), AGE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return updateStateWithCurrentMonth(getDefaultState());
    }

    protected BlockState updateStateWithCurrentMonth(BlockState stateIn)
    {
        return stateIn.with(getPlant().getStageProperty(), getPlant().stageFor(Calendars.SERVER.getCalendarMonthOfYear()));
    }
}
