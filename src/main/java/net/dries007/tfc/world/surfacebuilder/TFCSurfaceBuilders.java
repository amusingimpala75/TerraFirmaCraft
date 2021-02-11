/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Lazy;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCSurfaceBuilders
{
    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, MOD_ID);

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

    public static final Lazy<TernarySurfaceConfig> BASALT_CONFIG = config(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.BASALT).get(Rock.BlockType.RAW));

    /**
     * Tries to apply a {@link IContextSurfaceBuilder} if it exists, otherwise delegates to the standard method.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <C extends ISurfaceBuilderConfig> void applySurfaceBuilderWithContext(ConfiguredSurfaceBuilder<C> configuredSurfaceBuilder, IWorld worldIn, Random random, ChunkData chunkData, IChunk chunk, Biome biome, int posX, int posZ, int posY, double noise, long seed, BlockState defaultBlock, BlockState defaultFluid, int seaLevel)
    {
        configuredSurfaceBuilder.surfaceBuilder.initNoise(seed);
        if (configuredSurfaceBuilder.surfaceBuilder instanceof IContextSurfaceBuilder)
        {
            // Need an ugly cast here to verify the config type
            ((IContextSurfaceBuilder) configuredSurfaceBuilder.surfaceBuilder).applyWithContext(worldIn, chunkData, random, chunk, biome, posX, posZ, posY, noise, defaultBlock, defaultFluid, seaLevel, seed, configuredSurfaceBuilder.config);
        }
        else
        {
            configuredSurfaceBuilder.surfaceBuilder.apply(random, chunk, biome, posX, posZ, posY, noise, defaultBlock, defaultFluid, seaLevel, seed, configuredSurfaceBuilder.config);
        }
    }

    public static <C extends SurfaceConfig> void applySurfaceBuilder(ConfiguredSurfaceBuilder<C> surfaceBuilder, Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed)
    {
        applySurfaceBuilder(surfaceBuilder.surfaceBuilder, random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, surfaceBuilder.config);
    }

    /**
     * Runs a surface builder directly from a provided builder and config, and ensures noise was initialized beforehand.
     */
    public static <C extends SurfaceConfig> void applySurfaceBuilder(SurfaceBuilder<C> surfaceBuilder, Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, C config)
    {
        surfaceBuilder.initNoise(seed);
        surfaceBuilder.apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
    }

    private static <C extends SurfaceConfig, S extends SurfaceBuilder<C>> S register(String name, Function<Codec<C>, S> factory, Codec<C> codec)
    {
        return SURFACE_BUILDERS.register(name, () -> factory.apply(codec));
    }

    private static Lazy<TernarySurfaceConfig> config(Supplier<? extends Block> all)
    {
        return config(all, all, all);
    }

    private static Lazy<TernarySurfaceConfig> config(Supplier<? extends Block> top, Supplier<? extends Block> under, Supplier<? extends Block> underwater)
    {
        return Lazy.of(() -> new TernarySurfaceConfig(top.get().getDefaultState(), under.get().getDefaultState(), underwater.get().getDefaultState()));
    }
}