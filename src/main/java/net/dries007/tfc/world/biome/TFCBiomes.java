/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.biome;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.class_5423;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBiomeReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.collections.FiniteLinkedHashMap;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.world.biome.BiomeBuilder.builder;

public final class TFCBiomes
{
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MOD_ID);

    private static final List<RegistryKey<Biome>> DEFAULT_BIOME_KEYS = new ArrayList<>(); // All possible biomes generated by the TFC BiomeProvider
    private static final List<BiomeVariants> VARIANTS = new ArrayList<>();
    private static final Map<RegistryKey<Biome>, BiomeExtension> EXTENSIONS = new IdentityHashMap<>(); // All extensions, indexed by registry key for quick access

    // Aquatic biomes
    public static final BiomeVariants OCEAN = register("ocean", builder(seed -> BiomeNoise.ocean(seed, -20, -12)).salty().group(BiomeVariants.LargeGroup.OCEAN).types(BiomeDictionary.Type.OCEAN)); // Ocean biome found near continents.
    public static final BiomeVariants OCEAN_REEF = register("ocean_reef", builder(seed -> BiomeNoise.ocean(seed, -16, -6)).salty().group(BiomeVariants.LargeGroup.OCEAN).types(BiomeDictionary.Type.OCEAN)); // Ocean biome with reefs depending on climate. Could be interpreted as either barrier, fringe, or platform reefs.
    public static final BiomeVariants DEEP_OCEAN = register("deep_ocean", builder(seed -> BiomeNoise.ocean(seed, -38, -18)).group(BiomeVariants.LargeGroup.OCEAN).salty().types(BiomeDictionary.Type.OCEAN)); // Deep ocean biome covering most all oceans.
    public static final BiomeVariants DEEP_OCEAN_TRENCH = register("deep_ocean_trench", builder(seed -> BiomeNoise.oceanRidge(seed, -46, -18)).group(BiomeVariants.LargeGroup.OCEAN).salty().types(BiomeDictionary.Type.OCEAN)); // Deeper ocean with sharp relief carving to create very deep trenches

    // Low biomes
    public static final BiomeVariants PLAINS = register("plains", builder(seed -> BiomeNoise.simple(seed, 4, 10)).types(BiomeDictionary.Type.PLAINS)); // Very flat, slightly above sea level.
    public static final BiomeVariants HILLS = register("hills", builder(seed -> BiomeNoise.simple(seed, -5, 16)).types(BiomeDictionary.Type.HILLS)); // Small hills, slightly above sea level.
    public static final BiomeVariants LOWLANDS = register("lowlands", builder(BiomeNoise::lowlands).types(BiomeDictionary.Type.SWAMP)); // Flat, swamp-like, lots of shallow pools below sea level.
    public static final BiomeVariants LOW_CANYONS = register("low_canyons", builder(seed -> BiomeNoise.canyons(seed, -5, 15)).types(BiomeDictionary.Type.HILLS, BiomeDictionary.Type.SWAMP)); // Sharp, small hills, with lots of water / snaking winding rivers.

    // Mid biomes
    public static final BiomeVariants ROLLING_HILLS = register("rolling_hills", builder(seed -> BiomeNoise.simple(seed, -5, 28)).types(BiomeDictionary.Type.HILLS)); // Higher hills, above sea level. Some larger / steeper hills.
    public static final BiomeVariants BADLANDS = register("badlands", builder(BiomeNoise::badlands).types(BiomeDictionary.Type.HILLS, BiomeDictionary.Type.MESA)); // Very high flat area with steep relief carving, similar to vanilla mesas.
    public static final BiomeVariants PLATEAU = register("plateau", builder(seed -> BiomeNoise.simple(seed, 20, 30)).types(BiomeDictionary.Type.PLATEAU)); // Very high area, very flat top.
    public static final BiomeVariants CANYONS = register("canyons", builder(seed -> BiomeNoise.canyons(seed, 2, 32)).volcanoes(6, 14, 30, 28).types(BiomeDictionary.Type.HILLS)); // Medium height with snake like ridges, minor volcanic activity

    // High biomes
    public static final BiomeVariants MOUNTAINS = register("mountains", builder(seed -> BiomeNoise.mountains(seed, 10, 70)).types(BiomeDictionary.Type.MOUNTAIN)); // High, picturesque mountains. Pointed peaks, low valleys well above sea level.
    public static final BiomeVariants VOLCANIC_MOUNTAINS = register("volcanic_mountains", builder(seed -> BiomeNoise.mountains(seed, 10, 60)).volcanoes(5, 25, 50, 40).types(BiomeDictionary.Type.MOUNTAIN)); // Volcanic mountains - slightly smaller, but with plentiful tall volcanoes
    public static final BiomeVariants OCEANIC_MOUNTAINS = register("oceanic_mountains", builder(seed -> BiomeNoise.mountains(seed, -16, 60)).salty().types(BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.MOUNTAIN)); // Mountains with high areas, and low, below sea level valleys. Water is salt water here.
    public static final BiomeVariants VOLCANIC_OCEANIC_MOUNTAINS = register("volcanic_oceanic_mountains", builder(seed -> BiomeNoise.mountains(seed, -24, 50)).salty().volcanoes(1, -12, 50, 20).types(BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.MOUNTAIN)); // Volcanic oceanic islands. Slightly smaller and lower but with very plentiful volcanoes
    public static final BiomeVariants OLD_MOUNTAINS = register("old_mountains", builder(seed -> BiomeNoise.mountains(seed, 16, 40)).types(BiomeDictionary.Type.PLATEAU, BiomeDictionary.Type.MOUNTAIN)); // Rounded top mountains, very large hills.

    // Shores
    public static final BiomeVariants SHORE = register("shore", builder(BiomeNoise::shore).group(BiomeVariants.LargeGroup.OCEAN).salty().types(BiomeDictionary.Type.BEACH)); // Standard shore / beach. Material will vary based on location

    // Water
    public static final BiomeVariants LAKE = register("lake", builder(BiomeNoise::lake).group(BiomeVariants.LargeGroup.LAKE).types(BiomeDictionary.Type.RIVER));
    public static final BiomeVariants RIVER = register("river", builder(BiomeNoise::river).group(BiomeVariants.LargeGroup.RIVER).group(BiomeVariants.SmallGroup.RIVER).types(BiomeDictionary.Type.RIVER));

    // Mountain Fresh water / carving biomes
    public static final CarvingBiomeVariants MOUNTAIN_RIVER = register("mountain_river", BiomeBuilder.carving(MOUNTAINS, BiomeNoise::riverCarving).types(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants VOLCANIC_MOUNTAIN_RIVER = register("volcanic_mountain_river", BiomeBuilder.carving(VOLCANIC_MOUNTAINS, BiomeNoise::riverCarving).types(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants OLD_MOUNTAIN_RIVER = register("old_mountain_river", BiomeBuilder.carving(OLD_MOUNTAINS, BiomeNoise::riverCarving).types(BiomeDictionary.Type.PLATEAU, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants OCEANIC_MOUNTAIN_RIVER = register("oceanic_mountain_river", BiomeBuilder.carving(OCEANIC_MOUNTAINS, BiomeNoise::riverCarving).types(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants VOLCANIC_OCEANIC_MOUNTAIN_RIVER = register("volcanic_oceanic_mountain_river", BiomeBuilder.carving(VOLCANIC_OCEANIC_MOUNTAINS, BiomeNoise::riverCarving).types(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.RIVER));

    public static final CarvingBiomeVariants MOUNTAIN_LAKE = register("mountain_lake", BiomeBuilder.carving(MOUNTAINS, BiomeNoise::lakeCarving).types(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants VOLCANIC_MOUNTAIN_LAKE = register("volcanic_mountain_lake", BiomeBuilder.carving(VOLCANIC_MOUNTAINS, BiomeNoise::lakeCarving).types(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants OLD_MOUNTAIN_LAKE = register("old_mountain_lake", BiomeBuilder.carving(OLD_MOUNTAINS, BiomeNoise::lakeCarving).types(BiomeDictionary.Type.PLATEAU, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants OCEANIC_MOUNTAIN_LAKE = register("oceanic_mountain_lake", BiomeBuilder.carving(OCEANIC_MOUNTAINS, BiomeNoise::lakeCarving).types(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants VOLCANIC_OCEANIC_MOUNTAIN_LAKE = register("volcanic_oceanic_mountain_lake", BiomeBuilder.carving(VOLCANIC_OCEANIC_MOUNTAINS, BiomeNoise::lakeCarving).types(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.RIVER));
    public static final CarvingBiomeVariants PLATEAU_LAKE = register("plateau_lake", BiomeBuilder.carving(PLATEAU, BiomeNoise::lakeCarving).types(BiomeDictionary.Type.PLATEAU, BiomeDictionary.Type.RIVER));


    private static final Map<Biome, BiomeExtension> CACHED_EXTENSIONS = new FiniteLinkedHashMap<>(16); // Faster route from biome -> extension

    public static BiomeExtension getExtensionOrThrow(IWorld world, Biome biome)
    {
        BiomeExtension extension = getExtension(world, biome);
        if (extension == null)
        {
            throw new IllegalStateException("Biome missing a required extension!");
        }
        return extension;
    }

    @Nullable
    public static BiomeExtension getExtension(Biome biome)
    {
        return getExtension(ServerLifecycleHooks.getCurrentServer().registryAccess(), biome);
    }

    @Nullable
    public static BiomeExtension getExtension(class_5423 world, Biome biome)
    {
        return getExtension(world.registryAccess(), biome);
    }

    @Nullable
    public static BiomeExtension getExtension(DynamicRegistries registries, Biome biome)
    {
        // First query the cache, it is fast
        BiomeExtension extension = CACHED_EXTENSIONS.get(biome);
        if (extension == BiomeExtension.EMPTY)
        {
            // No match for this biome - this exists as a cache miss marker
            return null;
        }
        else if (extension != null)
        {
            // Cache hit
            return extension;
        }
        else
        {
            // This lookup here is the comparatively slow operation - avoid it if possible as this is happening a lot.
            Registry<Biome> registry = registries.registryOrThrow(Registry.BIOME_REGISTRY);
            BiomeExtension lookupExtension = registry.getResourceKey(biome).map(EXTENSIONS::get).orElse(null);
            if (lookupExtension != null)
            {
                // Save the extension and biome to the cache
                CACHED_EXTENSIONS.put(biome, lookupExtension);
                return lookupExtension;
            }
            else
            {
                // Mark this as a cache miss with the empty extension
                CACHED_EXTENSIONS.put(biome, BiomeExtension.EMPTY);
                return null;
            }
        }
    }

    public static List<RegistryKey<Biome>> getAllKeys()
    {
        return DEFAULT_BIOME_KEYS;
    }

    public static List<BiomeVariants> getVariants()
    {
        return VARIANTS;
    }

    private static <V extends BiomeVariants> V register(String baseName, BiomeBuilder<V> builder)
    {
        V variants = builder.build();
        VARIANTS.add(variants);
        for (BiomeTemperature temp : BiomeTemperature.values())
        {
            for (BiomeRainfall rain : BiomeRainfall.values())
            {
                String name = baseName + "_" + temp.name().toLowerCase() + "_" + rain.name().toLowerCase();
                ResourceLocation id = new ResourceLocation(MOD_ID, name);
                RegistryKey<Biome> key = RegistryKey.create(Registry.BIOME_REGISTRY, id);
                BiomeExtension extension = new BiomeExtension(key, variants);

                EXTENSIONS.put(key, extension);
                DEFAULT_BIOME_KEYS.add(key);
                TFCBiomes.BIOMES.register(name, BiomeMaker::theVoidBiome);

                registerDefaultBiomeDictionaryTypes(key, temp, rain);
                builder.registerTypes(key);

                variants.put(temp, rain, extension);
            }
        }
        return variants;
    }

    private static void registerDefaultBiomeDictionaryTypes(RegistryKey<Biome> key, BiomeTemperature temp, BiomeRainfall rain)
    {
        BiomeDictionary.addTypes(key, BiomeDictionary.Type.OVERWORLD);
        if (temp == BiomeTemperature.FROZEN)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.COLD);
        }
        else if (temp == BiomeTemperature.COLD)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.COLD);
        }
        else if (temp == BiomeTemperature.WARM || temp == BiomeTemperature.LUKEWARM)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.HOT);
        }

        if (rain == BiomeRainfall.WET || rain == BiomeRainfall.DAMP)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.WET);
        }
        else if (rain == BiomeRainfall.DRY)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.DRY);
        }
        else if (rain == BiomeRainfall.ARID)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.DRY, BiomeDictionary.Type.SANDY);
        }

        if (rain == BiomeRainfall.WET && temp == BiomeTemperature.WARM)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.JUNGLE);
        }
        else if (rain == BiomeRainfall.ARID && temp == BiomeTemperature.WARM)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.SAVANNA);
        }
        else if (temp == BiomeTemperature.COLD && rain != BiomeRainfall.ARID)
        {
            BiomeDictionary.addTypes(key, BiomeDictionary.Type.CONIFEROUS);
        }
    }
}