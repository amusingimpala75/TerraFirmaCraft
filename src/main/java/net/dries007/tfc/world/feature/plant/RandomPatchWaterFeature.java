/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

public class RandomPatchWaterFeature extends Feature<RandomPatchFeatureConfig>
{
    public RandomPatchWaterFeature(Codec<RandomPatchFeatureConfig> codec)
    {
        super(codec);
    }

    //unused: project, canReplace
    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, RandomPatchFeatureConfig config)
    {
        BlockState blockstate = config.stateProvider.getBlockState(rand, pos);
        int i = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int j = 0; j < config.tries; ++j)
        {
            mutablePos.set(world.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, pos), rand.nextInt(config.spreadX + 1) - rand.nextInt(config.spreadX + 1), rand.nextInt(config.spreadY + 1) - rand.nextInt(config.spreadY + 1) - 1, rand.nextInt(config.spreadZ + 1) - rand.nextInt(config.spreadZ + 1));
            BlockState state = world.getBlockState(mutablePos);
            mutablePos.move(Direction.UP);
            if ((world.isWater(mutablePos)) && !(world.getBlockState(mutablePos).getBlock() instanceof IFluidLoggable) && state.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON) && (config.whitelist.isEmpty() || config.whitelist.contains(state.getBlock())) && !config.blacklist.contains(state))
            {
                config.blockPlacer.generate(world, mutablePos, blockstate, rand);
                ++i;
            }
        }
        return i > 0;
    }
}
