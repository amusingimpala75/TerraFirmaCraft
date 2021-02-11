/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

import java.util.Arrays;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.layer.util.LayerOperator;
import net.minecraft.world.biome.layer.util.LayerSampler;

import it.unimi.dsi.fastutil.HashCommon;

/**
 * A variant of {@link net.minecraft.world.biome.layer.util.CachingLayerSampler} which implements a non-synchronized, lossy cache
 */
public class FastArea implements LayerSampler
{
    private final LayerOperator factory;
    private final long[] keys;
    private final int[] values;
    private final int mask;

    public FastArea(LayerOperator factory, int maxCacheSize)
    {
        maxCacheSize = MathHelper.smallestEncompassingPowerOfTwo(maxCacheSize);

        this.factory = factory;
        this.keys = new long[maxCacheSize];
        this.values = new int[maxCacheSize];
        this.mask = maxCacheSize - 1;

        Arrays.fill(this.keys, Long.MIN_VALUE);
    }

    @Override
    public int sample(int x, int z)
    {
        final long key = ChunkPos.toLong(x, z);
        final int index = (int) HashCommon.mix(key) & mask;
        if (keys[index] == key)
        {
            return values[index];
        }
        else
        {
            final int value = factory.apply(x, z);
            values[index] = value;
            keys[index] = key;
            return value;
        }
    }
}
