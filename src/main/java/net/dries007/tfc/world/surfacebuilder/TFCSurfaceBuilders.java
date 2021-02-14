/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import net.dries007.tfc.util.Helpers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.Codecs;

@SuppressWarnings("unused")
public class TFCSurfaceBuilders
{
    public static final NormalSurfaceBuilder NORMAL = register("normal", NormalSurfaceBuilder::new, Codecs.LENIENT_SURFACE_BUILDER_CONFIG);
    public static final ThinSurfaceBuilder THIN = register("thin", ThinSurfaceBuilder::new, Codecs.LENIENT_SURFACE_BUILDER_CONFIG);
    public static final BadlandsSurfaceBuilder BADLANDS = register("badlands", BadlandsSurfaceBuilder::new, Codecs.LENIENT_SURFACE_BUILDER_CONFIG);
    public static final MountainSurfaceBuilder MOUNTAINS = register("mountains", MountainSurfaceBuilder::new, Codecs.NOOP_SURFACE_BUILDER_CONFIG);
    public static final ShoreSurfaceBuilder SHORE = register("shore", ShoreSurfaceBuilder::new, Codecs.NOOP_SURFACE_BUILDER_CONFIG);
    public static final UnderwaterSurfaceBuilder UNDERWATER = register("underwater", UnderwaterSurfaceBuilder::new, Codecs.NOOP_SURFACE_BUILDER_CONFIG);
    public static final FrozenUnderwaterSurfaceBuilder FROZEN_UNDERWATER = register("frozen_underwater", FrozenUnderwaterSurfaceBuilder::new, Codecs.NOOP_SURFACE_BUILDER_CONFIG);

    public static final VolcanoesSurfaceBuilder WITH_VOLCANOES = register("with_volcanoes", VolcanoesSurfaceBuilder::new, ParentedSurfaceBuilderConfig.CODEC);

    // Used for shores - red sand = normal beach sand, sandstone = variant beach sand (pink / black)
    public static final Lazy<TernarySurfaceConfig> RED_SAND_CONFIG = config(() -> Blocks.RED_SAND);
    public static final Lazy<TernarySurfaceConfig> RED_SANDSTONE_CONFIG = config(() -> Blocks.RED_SANDSTONE);
    public static final Lazy<TernarySurfaceConfig> COBBLE_COBBLE_RED_SAND_CONFIG = config(() -> Blocks.COBBLESTONE, () -> Blocks.COBBLESTONE, () -> Blocks.RED_SAND);

    public static final Lazy<TernarySurfaceConfig> BASALT_CONFIG = config(() -> TFCBlocks.ROCK_BLOCKS.get(Rock.Default.BASALT).get(Rock.BlockType.RAW));

    /**
     * Tries to apply a {@link IContextSurfaceBuilder} if it exists, otherwise delegates to the standard method.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <C extends SurfaceConfig> void applySurfaceBuilderWithContext(ConfiguredSurfaceBuilder<C> configuredSurfaceBuilder, WorldAccess worldIn, Random random, ChunkData chunkData, Chunk chunk, Biome biome, int posX, int posZ, int posY, double noise, long seed, BlockState defaultBlock, BlockState defaultFluid, int seaLevel)
    {
        configuredSurfaceBuilder.surfaceBuilder.initSeed(seed);
        if (configuredSurfaceBuilder.surfaceBuilder instanceof IContextSurfaceBuilder)
        {
            // Need an ugly cast here to verify the config type
            ((IContextSurfaceBuilder) configuredSurfaceBuilder.surfaceBuilder).applyWithContext(worldIn, chunkData, random, chunk, biome, posX, posZ, posY, noise, defaultBlock, defaultFluid, seaLevel, seed, configuredSurfaceBuilder.config);
        }
        else
        {
            configuredSurfaceBuilder.surfaceBuilder.generate(random, chunk, biome, posX, posZ, posY, noise, defaultBlock, defaultFluid, seaLevel, seed, configuredSurfaceBuilder.config);
        }
    }

    public static <C extends SurfaceConfig> void applySurfaceBuilder(ConfiguredSurfaceBuilder<C> surfaceBuilder, Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed)
    {
        applySurfaceBuilder(surfaceBuilder.surfaceBuilder, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, surfaceBuilder.config);
    }

    /**
     * Runs a surface builder directly from a provided builder and config, and ensures noise was initialized beforehand.
     */
    public static <C extends SurfaceConfig> void applySurfaceBuilder(SurfaceBuilder<C> surfaceBuilder, Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, C config)
    {
        surfaceBuilder.initSeed(seed);
        surfaceBuilder.generate(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
    }

    private static <C extends SurfaceConfig, S extends SurfaceBuilder<C>> S register(String name, Function<Codec<C>, S> factory, Codec<C> codec)
    {
        return Registry.register(Registry.SURFACE_BUILDER, Helpers.identifier(name), factory.apply(codec));
    }

    private static Lazy<TernarySurfaceConfig> config(Supplier<? extends Block> all)
    {
        return config(all, all, all);
    }

    private static Lazy<TernarySurfaceConfig> config(Supplier<? extends Block> top, Supplier<? extends Block> under, Supplier<? extends Block> underwater)
    {
        return new Lazy<>(() -> new TernarySurfaceConfig(top.get().getDefaultState(), under.get().getDefaultState(), underwater.get().getDefaultState()));
    }

    public static void register() {}
}