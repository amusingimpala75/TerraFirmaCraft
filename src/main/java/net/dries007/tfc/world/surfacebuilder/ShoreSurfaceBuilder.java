/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class ShoreSurfaceBuilder extends SeededSurfaceBuilder<TernarySurfaceConfig>
{
    private INoise2D variantNoise;

    public ShoreSurfaceBuilder(Codec<TernarySurfaceConfig> codec)
    {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, TernarySurfaceConfig config)
    {
        float variantNoiseValue = variantNoise.noise(x, z);
        if (variantNoiseValue > 0.6f)
        {
            TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.NORMAL, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, TFCSurfaceBuilders.RED_SAND_CONFIG.get());
        }
        else
        {
            TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.NORMAL, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, TFCSurfaceBuilders.RED_SANDSTONE_CONFIG.get());
        }
    }

    @Override
    protected void initSeed2(long seed)
    {
        variantNoise = new OpenSimplex2D(seed).octaves(2).spread(0.003f).abs();
    }
}