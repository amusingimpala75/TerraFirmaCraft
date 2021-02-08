/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface ITallPlant
{
    default Part getPlantPart(BlockView world, BlockPos pos)
    {
        if (world.getBlockState(pos.down()).getBlock() != this)
        {
            return Part.LOWER;
        }
        return Part.UPPER;
    }

    enum Part implements StringIdentifiable
    {
        UPPER,
        LOWER;

        @Override
        public String toString()
        {
            return this.asString();
        }

        @Override
        public String asString()
        {
            return name().toLowerCase();
        }
    }
}
