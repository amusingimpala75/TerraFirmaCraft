/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;

public class RandomPatchWaterLandFeature extends Feature<RandomPatchFeatureConfig>
{
    public RandomPatchWaterLandFeature(Codec<RandomPatchFeatureConfig> codec)
    {
        super(codec);
    }

    //unused: project, canReplace, y spread
    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, RandomPatchFeatureConfig config)
    {
        BlockState state = config.stateProvider.getBlockState(rand, pos);
        int i = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int j = 0; j < config.tries; ++j)
        {
            mutablePos.set(world.getTopPosition(Heightmap.Type.OCEAN_FLOOR_WG, pos), rand.nextInt(config.spreadX + 1) - rand.nextInt(config.spreadX + 1), -1, rand.nextInt(config.spreadZ + 1) - rand.nextInt(config.spreadZ + 1));
            BlockState belowState = world.getBlockState(mutablePos);
            mutablePos.move(Direction.UP);
            boolean flag1 = world.isAir(mutablePos);
            boolean flag2 = world.isWater(mutablePos);
            boolean flag3 = (config.whitelist.isEmpty() || config.whitelist.contains(belowState.getBlock())) && !config.blacklist.contains(belowState);
            if ((world.isAir(mutablePos) || world.isWater(mutablePos)) && state.canPlaceAt(world, mutablePos) && (config.whitelist.isEmpty() || config.whitelist.contains(belowState.getBlock())) && !config.blacklist.contains(belowState))
            {
                if (state.getBlock() instanceof IFluidLoggable)
                {
                    IFluidLoggable block = (IFluidLoggable) state.getBlock();
                    state = block.getStateWithFluid(state, world.getFluidState(mutablePos).getFluid());
                }
                config.blockPlacer.generate(world, mutablePos, state, rand);
                ++i;
            }
        }
        return i > 0;
    }
}
