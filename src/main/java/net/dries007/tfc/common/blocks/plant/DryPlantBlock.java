/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.common.TFCTags;
import net.minecraft.world.WorldView;

public abstract class DryPlantBlock extends PlantBlock
{
    public static DryPlantBlock create(IPlant plant, Settings properties)
    {
        return new DryPlantBlock(properties)
        {

            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected DryPlantBlock(Settings properties)
    {
        super(properties);

        setDefaultState(getDefaultState().with(getPlant().getStageProperty(), 0).with(AGE, 0));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        return belowState.isIn(BlockTags.SAND) || belowState.isIn(TFCTags.Common.SAND) || belowState.isIn(TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }
}
