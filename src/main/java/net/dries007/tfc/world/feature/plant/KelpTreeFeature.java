/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.plant.KelpTreeFlowerBlock;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

public class KelpTreeFeature extends Feature<SingleStateFeatureConfig>
{
    public KelpTreeFeature(Codec<SingleStateFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, SingleStateFeatureConfig config)
    {
        pos = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, pos);
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;
        for (int i = 0; i < 20; i++)
        {
            mutablePos.set(pos, rand.nextInt(10) - rand.nextInt(10), 0, rand.nextInt(10) - rand.nextInt(10));
            if (!world.isWater(mutablePos) || world.getBlockState(mutablePos).getBlock() instanceof IFluidLoggable)
                continue;
            mutablePos.move(Direction.DOWN);
            if (!world.getBlockState(mutablePos).isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                continue;
            mutablePos.move(Direction.UP);
            for (int j = 0; j < 4; j++)
            {
                mutablePos.move(Direction.UP);
                //horrendously inefficient check but whatever
                if (world.getBlockState(mutablePos).getBlock() instanceof IFluidLoggable || !world.isWater(pos))
                    break;
            }
            mutablePos.move(Direction.DOWN, 4);
            Fluid fluid = world.getFluidState(mutablePos).getFluid();
            KelpTreeFlowerBlock flower = (KelpTreeFlowerBlock) config.state.getBlock();
            if (!flower.getFluidProperty().canContain(fluid))
                return false;
            flower.generatePlant(world, mutablePos, rand, 8, fluid);
            placedAny = true;
        }
        return placedAny;
    }
}
