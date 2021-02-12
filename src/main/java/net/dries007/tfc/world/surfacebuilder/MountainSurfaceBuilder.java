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
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class MountainSurfaceBuilder extends SeededSurfaceBuilder<TernarySurfaceConfig>
{
    private INoise2D surfaceMaterialNoise;

    public MountainSurfaceBuilder(Codec<TernarySurfaceConfig> codec)
    {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, TernarySurfaceConfig config)
    {
        double heightNoise = noise * 3f + startHeight;
        if (heightNoise > 130)
        {
            float surfaceMaterialValue = surfaceMaterialNoise.noise(x, z) + 0.1f * random.nextFloat() - 0.05f;
            if (surfaceMaterialValue > 0.3f)
            {
                TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.NORMAL, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, TFCSurfaceBuilders.COBBLE_COBBLE_RED_SAND_CONFIG.get());
            }
            else if (surfaceMaterialValue < -0.3f)
            {
                TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.NORMAL, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRAVEL_CONFIG);
            }
            else
            {
                TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.NORMAL, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.STONE_CONFIG);
            }
        }
        else
        {
            TFCSurfaceBuilders.applySurfaceBuilder(TFCSurfaceBuilders.THIN, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRASS_CONFIG);
        }
    }

    @Override
    protected void initSeed2(long seed)
    {
        surfaceMaterialNoise = new OpenSimplex2D(seed).octaves(2).spread(0.02f);
    }
}