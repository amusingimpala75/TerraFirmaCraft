/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.CoralClawFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import com.mojang.serialization.Codec;

public class TFCCoralClawFeature extends CoralClawFeature
{
    public TFCCoralClawFeature(Codec<DefaultFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    protected boolean spawnCoralPiece(WorldAccess world, Random random, BlockPos pos, BlockState state)
    {
        return CoralHelpers.placeCoralBlock(world, random, pos, state);

    }
}
