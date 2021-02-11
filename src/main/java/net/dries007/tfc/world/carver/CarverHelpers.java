/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;

import net.dries007.tfc.mixin.world.gen.carver.ConfiguredCarverAccessor;
import net.dries007.tfc.world.chunkdata.RockData;
import org.jetbrains.annotations.Nullable;

public final class CarverHelpers
{
    public static BitSet createWaterAdjacencyMask(ProtoChunk chunk, int seaLevel)
    {
        final BitSet waterAdjacencyMask = new BitSet(16 * 16 * (1 + seaLevel));

        // Sections
        for (int sectionY = 0; sectionY < 16; sectionY++)
        {
            final ChunkSection section = chunk.getSection(sectionY);
            for (int localY = 0; localY < 16; localY++)
            {
                final int y = (sectionY << 4) | localY;
                if (y > seaLevel)
                {
                    // Exit condition
                    return waterAdjacencyMask;
                }

                // Positions within the section
                for (int x = 0; x < 16; x++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        final BlockState state = section.getBlockState(x, localY, z);
                        if (state.getFluidState().isIn(FluidTags.WATER))
                        {
                            // Update a region around the water block in the mask
                            for (int xi = -2; xi <= 2; xi++)
                            {
                                for (int yi = -2; yi <= 0; yi++)
                                {
                                    for (int zi = -2; zi <= 2; zi++)
                                    {
                                        final int posX = x + xi;
                                        final int posY = y + yi;
                                        final int posZ = z + zi;
                                        if (posX >> 4 == 0 && posZ >> 4 == 0 && posY >= 0 && posY <= seaLevel)
                                        {
                                            waterAdjacencyMask.set(posX | (posZ << 4) | (posY << 8));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new IllegalStateException("We should have exited earlier!");
    }

    /**
     * Computes an index into a carving mask bit set, used during world gen
     */
    public static int maskIndex(BlockPos pos)
    {
        return (pos.getX() & 15) | ((pos.getZ() & 15) << 4) | (pos.getY() << 8);
    }

    public static int maskIndex(int x, int y, int z)
    {
        return (x & 15) | ((z & 15) << 4) | (y << 8);
    }

    public static void runCarversWithContext(long worldSeed, Chunk chunk, BiomeAccess delegateBiomeManager, GenerationSettings biomeGenerationSettings, ChunkRandom random, GenerationStep.Carver stage, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, @Nullable BitSet waterAdjacencyMask, int seaLevel)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final List<Supplier<ConfiguredCarver<?>>> carvers = biomeGenerationSettings.getCarversForStep(stage);

        // Setup IContextCarvers
        for (Supplier<ConfiguredCarver<?>> lazyCarver : carvers)
        {
            final Carver<?> carver = ((ConfiguredCarverAccessor) lazyCarver.get()).accessor$getWorldCarver();
            if (carver instanceof IContextCarver)
            {
                ((IContextCarver) carver).setContext(worldSeed, airCarvingMask, liquidCarvingMask, rockData, waterAdjacencyMask);
            }
        }

        // Vanilla carving
        for (int x = chunkPos.x - 8; x <= chunkPos.x + 8; ++x)
        {
            for (int z = chunkPos.z - 8; z <= chunkPos.z + 8; ++z)
            {
                int index = 0;
                for (Supplier<ConfiguredCarver<?>> lazyCarver : carvers)
                {
                    final ConfiguredCarver<?> carver = lazyCarver.get();

                    random.setCarverSeed(worldSeed + index, x, z);
                    if (carver.shouldCarve(random, x, z))
                    {
                        carver.carve(chunk, delegateBiomeManager::getBiome, random, seaLevel, x, z, chunkPos.x, chunkPos.z, stage == GenerationStep.Carver.AIR ? airCarvingMask : liquidCarvingMask);
                    }
                    index++;
                }
            }
        }
    }
}
