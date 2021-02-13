/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

public class TFCTree extends SaplingGenerator
{
    private final Identifier normalTree;
    private final Identifier oldGrowthTree;

    public TFCTree(Identifier normalTree, Identifier oldGrowthFeatureFactory)
    {
        this.normalTree = normalTree;
        this.oldGrowthTree = oldGrowthFeatureFactory;
    }

    public ConfiguredFeature<?, ?> getNormalFeature(Registry<ConfiguredFeature<?, ?>> registry)
    {
        return registry.getOrEmpty(normalTree).orElseThrow(() -> new IllegalStateException("Missing tree feature: " + normalTree));
    }

    public ConfiguredFeature<?, ?> getOldGrowthFeature(Registry<ConfiguredFeature<?, ?>> registry)
    {
        return registry.getOrEmpty(oldGrowthTree).orElseGet(() -> getNormalFeature(registry));
    }

    @Nullable
    @Override
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random randomIn, boolean largeHive)
    {
        return null; // Not using vanilla's feature config
    }

    @Override
    public boolean generate(ServerWorld worldIn, ChunkGenerator chunkGeneratorIn, BlockPos blockPosIn, BlockState blockStateIn, Random randomIn)
    {
        ConfiguredFeature<?, ?> feature = getNormalFeature(worldIn.getRegistryManager().get(Registry.CONFIGURED_FEATURE_WORLDGEN));
        worldIn.setBlockState(blockPosIn, Blocks.AIR.getDefaultState(), 4);
        if (feature.generate(worldIn, chunkGeneratorIn, randomIn, blockPosIn))
        {
            return true;
        }
        else
        {
            worldIn.setBlockState(blockPosIn, blockStateIn, 4);
            return false;
        }
    }
}