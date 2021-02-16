/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;


import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.util.LayerOperator;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.source.SeedMixer;

@SuppressWarnings("Since15")
public class FastAreaContext implements LayerSampleContext<FastArea>
{
    private final long seed;
    private long rval;

    private static long mixSeed(long left, long right)
    {
        long mixRight = SeedMixer.mixSeed(right, right);
        mixRight = SeedMixer.mixSeed(mixRight, right);
        mixRight = SeedMixer.mixSeed(mixRight, right);
        long mixLeft = SeedMixer.mixSeed(left, mixRight);
        mixLeft = SeedMixer.mixSeed(mixLeft, mixRight);
        return SeedMixer.mixSeed(mixLeft, mixRight);
    }

    public FastAreaContext(long seed, long seedModifier)
    {
        this.seed = mixSeed(seed, seedModifier);
    }

    @Override
    public void initSeed(long x, long z)
    {
        long value = this.seed;
        value = SeedMixer.mixSeed(value, x);
        value = SeedMixer.mixSeed(value, z);
        value = SeedMixer.mixSeed(value, x);
        value = SeedMixer.mixSeed(value, z);
        this.rval = value;
    }

    @Override
    public FastArea createSampler(LayerOperator pixelTransformer)
    {
        return new FastArea(pixelTransformer, 256);
    }

    @Override
    //DO NOT REMOVE INT CAST OTHERWISE: incompatible types: possible lossy conversion from long to int
    public int nextInt(int bound)
    {
        final int value = (int) Math.floorMod(rval >> 24, bound);
        rval = SeedMixer.mixSeed(rval, seed);
        return value;
    }

    @Override
    public PerlinNoiseSampler getNoiseSampler()
    {
        throw new IllegalStateException("Go away");
    }
}
