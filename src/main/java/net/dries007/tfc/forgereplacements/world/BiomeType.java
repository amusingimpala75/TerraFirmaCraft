/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.forgereplacements.world;

import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("ALL")
public enum BiomeType {
    OCEAN(),
    PLAINS(),
    HILLS(OverworldBiomes::addHillsBiome),
    SWAMP(),
    MESA(OverworldBiomes::addHillsBiome),
    PLATEAU(OverworldBiomes::addHillsBiome),
    MOUNTAIN(OverworldBiomes::addHillsBiome),
    BEACH(OverworldBiomes::addShoreBiome),
    RIVER(OverworldBiomes::setRiverBiome),
    SNOWY(OverworldClimate.SNOWY),
    DRY(OverworldClimate.DRY),
    COLD(OverworldClimate.COOL),
    TEMPERATE(OverworldClimate.TEMPERATE),
    HOT(),
    SANDY(),
    WET(),
    JUNGLE(),
    SAVANNA(),
    CONIFEROUS();

    private TriConsumer<RegistryKey<Biome>, RegistryKey<Biome>, Double> registryFunc;
    private BiConsumer<RegistryKey<Biome>, RegistryKey<Biome>> biomeRegFunc;
    private OverworldClimate climate;

    private List<RegistryKey<Biome>> biomes = new ArrayList<>();

    BiomeType(TriConsumer<RegistryKey<Biome>, RegistryKey<Biome>, Double> regFunc) {
        this.registryFunc = regFunc;
    }
    BiomeType(BiConsumer<RegistryKey<Biome>, RegistryKey<Biome>> biomeRegFunc) {
        this.biomeRegFunc = biomeRegFunc;
    }
    BiomeType(OverworldClimate climate) {this.climate = climate;}
    BiomeType() {}

    public TriConsumer<RegistryKey<Biome>, RegistryKey<Biome>, Double> getRegistryFuncIfPresent() {
        return registryFunc;
    }

    public BiConsumer<RegistryKey<Biome>, RegistryKey<Biome>> getBiomeRegFuncIfPresent() {
        return biomeRegFunc;
    }

    public static void addTypes(RegistryKey<Biome> biome, BiomeType... types) {
        for (BiomeType type : types) {
            type.biomes.add(biome);
            if (type.registryFunc != null) {
                type.registryFunc.accept(biome, biome, 1.0D);
            } else if (type.biomeRegFunc != null) {
                type.biomeRegFunc.accept(biome, biome);
            } else if (type.climate != null) {
                OverworldBiomes.addContinentalBiome(biome, type.climate, 1.0F);
            }
        }
    }

    public List<RegistryKey<Biome>> getBiomes() {
        return biomes;
    }
}
