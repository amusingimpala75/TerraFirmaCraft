/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.UnderwaterRavineCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import com.mojang.serialization.Codec;
import net.dries007.tfc.mixin.world.gen.carver.CanyonWorldCarverAccessor;
import net.dries007.tfc.world.chunkdata.RockData;
import org.jetbrains.annotations.Nullable;

public class TFCUnderwaterRavineCarver extends UnderwaterRavineCarver implements IContextCarver
{
    private final SaltWaterBlockCarver blockCarver;

    private boolean initialized;

    public TFCUnderwaterRavineCarver(Codec<ProbabilityConfig> codec)
    {
        super(codec);
        blockCarver = new SaltWaterBlockCarver();
        initialized = false;
    }

    @Override
    public boolean carve(Chunk chunkIn, Function<BlockPos, Biome> biomePos, Random rand, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config)
    {
        if (!initialized)
        {
            throw new IllegalStateException("Not properly initialized! Cannot use TFCUnderwaterRavineCarver with a chunk generator that does not respect IContextCarver");
        }
        double xOffset = chunkXOffset * 16 + rand.nextInt(16);
        double yOffset = rand.nextInt(70) + 20; // Modified to use sea level, should reach surface more often
        double zOffset = chunkZOffset * 16 + rand.nextInt(16);
        float yaw = rand.nextFloat() * ((float) Math.PI * 2F);
        float pitch = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
        float width = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
        int branchAmount = 112 - rand.nextInt(28);
        ((CanyonWorldCarverAccessor) this).call$genCanyon(chunkIn, biomePos, rand.nextLong(), seaLevel, chunkX, chunkZ, xOffset, yOffset, zOffset, width, yaw, pitch, 0, branchAmount, 3.0D, carvingMask);
        return true;
    }

    @Override
    public void setContext(long worldSeed, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, @Nullable BitSet waterAdjacencyMask)
    {
        this.blockCarver.setContext(worldSeed, airCarvingMask, liquidCarvingMask, rockData, waterAdjacencyMask);
        this.initialized = true;
    }

    @Override
    protected boolean carveAtPoint(Chunk chunkIn, Function<BlockPos, Biome> lazyBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutablePos1, BlockPos.Mutable mutablePos2, BlockPos.Mutable mutablePos3, int seaLevel, int chunkX, int chunkZ, int actualX, int actualZ, int localX, int y, int localZ, MutableBoolean reachedSurface)
    {
        mutablePos1.set(actualX, y, actualZ);
        return blockCarver.carve(chunkIn, mutablePos1, random, seaLevel);
    }
}