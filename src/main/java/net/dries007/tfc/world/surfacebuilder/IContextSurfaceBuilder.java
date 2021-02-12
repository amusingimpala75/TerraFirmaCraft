/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;

import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Indicates that a surface builder requires more (or possibly just different) context in order to generate.
 * In order to support this with a custom world generator, call {@link TFCSurfaceBuilders# applyIfPresent(ConfiguredSurfaceBuilder, Random, ChunkData, net.minecraft.world.chunk.Chunk, Biome, int, int, int, double, long, BlockState, BlockState, int)} during surface building rather than {@link Biome# buildSurfaceAt(Random, IChunk, int, int, int, double, BlockState, BlockState, int, long)}
 *
 * @param <C> The config type. MUST be a supertype of the surface builder config type.
 */
public interface IContextSurfaceBuilder<C extends SurfaceConfig>
{
    /**
     * @param worldIn   The world, for querying biome extensions
     * @param chunkData Chunk data, generated to at least {@link ChunkData.Status#ROCKS}
     */
    void applyWithContext(WorldAccess worldIn, ChunkData chunkData, Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, C config);
}
