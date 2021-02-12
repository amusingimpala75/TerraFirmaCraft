/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

public class CavePatchFeature extends Feature<RandomPatchFeatureConfig>
{
    public CavePatchFeature(Codec<RandomPatchFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, RandomPatchFeatureConfig config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;
        for (int i = 0; i < config.tries; ++i)
        {
            mutablePos.set(pos, rand.nextInt(config.spreadX + 1) - rand.nextInt(config.spreadX + 1), -1, rand.nextInt(config.spreadZ + 1) - rand.nextInt(config.spreadZ + 1));
            final BlockState belowState = world.getBlockState(mutablePos);
            mutablePos.move(Direction.UP);
            final BlockState state = config.stateProvider.getBlockState(rand, mutablePos);

            if (world.isAir(mutablePos) && state.canPlaceAt(world, mutablePos) && (config.whitelist.isEmpty() || config.whitelist.contains(belowState.getBlock())) && !config.blacklist.contains(belowState))
            {
                config.blockPlacer.generate(world, mutablePos, state, rand);
                placedAny = true;
            }
        }
        return placedAny;
    }
}
