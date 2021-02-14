/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public abstract class TFCSeagrassBlock extends WaterPlantBlock
{
    protected static final VoxelShape GRASS_SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHORTER_GRASS_SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);
    protected static final VoxelShape SHORT_GRASS_SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    protected static final VoxelShape SHORTEST_GRASS_SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public static TFCSeagrassBlock create(IPlant plant, FluidProperty fluid, AbstractBlock.Settings properties)
    {
        return new TFCSeagrassBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }

            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected TFCSeagrassBlock(AbstractBlock.Settings properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        switch (state.get(AGE))
        {
            case 0:
                return SHORTEST_GRASS_SHAPE;
            case 1:
                return SHORTER_GRASS_SHAPE;
            case 2:
                return SHORT_GRASS_SHAPE;
            default:
                return GRASS_SHAPE;
        }
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType()
    {
        return AbstractBlock.OffsetType.XZ;
    }
}
