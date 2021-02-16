/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import com.google.common.annotations.VisibleForTesting;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.fabric.cca.ChunkDataChunkComponent;
import net.dries007.tfc.forgereplacements.world.ServerUtil;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;

/**
 * This acts as a bridge between the {@link ChunkGenerator}, TFC's chunk data caches and tracking, and the {@link IChunkDataGenerator}.
 * In order to customize the chunk data generation, see {@link IChunkDataGenerator}
 */
public final class ChunkDataProvider
{
    /**
     * Directly tries to access the chunk data provider through the overworld.
     */
    public static ChunkDataProvider getOrThrow()
    {
        return getOrThrow(ServerUtil.getCurrentServer().getOverworld());
    }

    public static ChunkDataProvider getOrThrow(WorldAccess world)
    {
        ChunkManager chunkProvider = world.getChunkManager();
        if (chunkProvider instanceof ServerChunkManager)
        {
            return getOrThrow(((ServerChunkManager) chunkProvider).getChunkGenerator());
        }
        throw new IllegalStateException("Tried to access ChunkDataProvider but no ServerChunkProvider was found on world: " + world);
    }

    /**
     * Tries to access the chunk data provider through the chunk generator, mostly used during feature generation when we have direct access to the generator.
     */
    public static ChunkDataProvider getOrThrow(ChunkGenerator chunkGenerator)
    {
        if (chunkGenerator instanceof ITFCChunkGenerator)
        {
            return ((ITFCChunkGenerator) chunkGenerator).getChunkDataProvider();
        }
        throw new IllegalStateException("Tried to access ChunkDataProvider but none was present on " + chunkGenerator);
    }

    private final IChunkDataGenerator generator;

    public ChunkDataProvider(IChunkDataGenerator generator)
    {
        this.generator = generator;
    }

    /**
     * Gets the chunk data for a chunk, during world generation.
     * The default implementation generates chunk data using TFC semantics, and stores generated data in {@link ChunkDataCache#WORLD_GEN}
     * Implementors are free to return any form of data.
     *
     * @param pos            The chunk position
     * @param requiredStatus The minimum status of the chunk data returned
     * @return A chunk data for the provided chunk pos
     */
    public final ChunkDataChunkComponent get(BlockPos pos, ChunkDataChunkComponent.Status requiredStatus)
    {
        return get(new ChunkPos(pos), requiredStatus);
    }

    /**
     * Gets the chunk data for a chunk, during world generation.
     * The default implementation generates chunk data using TFC semantics, and stores generated data in {@link ChunkDataCache#WORLD_GEN}
     * Implementors are free to return any form of data.
     *
     * @param pos            The chunk position
     * @param requiredStatus The minimum status of the chunk data returned
     * @return A chunk data for the provided chunk pos
     */
    public final ChunkDataChunkComponent get(ChunkPos pos, ChunkDataChunkComponent.Status requiredStatus)
    {
        final ChunkDataChunkComponent data = ChunkDataCache.WORLD_GEN.getOrCreate(pos);
        while (!data.getStatus().isAtLeast(requiredStatus))
        {
            final ChunkDataChunkComponent.Status next = data.getStatus().next();
            generator.generate(data, next);
            data.setStatus(next);
        }
        return data;
    }

    @VisibleForTesting
    public IChunkDataGenerator getGenerator()
    {
        return generator;
    }

    @Override
    public String toString()
    {
        return "ChunkDataProvider[" + generator.getClass().getSimpleName() + ']';
    }
}