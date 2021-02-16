/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.mixin.fabric.world.GeneratorTypeAccessor;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

import java.util.List;

public class TFCWorldType
{
    public static final GeneratorType WORLD_TYPE = new GeneratorType("terrafirmacraft.world_type") {
        @Override
        protected ChunkGenerator getChunkGenerator(Registry<Biome> biomeRegistry, Registry<ChunkGeneratorSettings> chunkGeneratorSettingsRegistry, long seed) {
            return TFCChunkGenerator.createDefaultPreset(chunkGeneratorSettingsRegistry.getOrThrow(ChunkGeneratorSettings.OVERWORLD), biomeRegistry, seed);
        }
    };
    //WORLD_TYPES.register("tng", () -> new ForgeWorldType((biomeRegistry, dimensionSettingsRegistry, seed) -> TFCChunkGenerator.createDefaultPreset(() -> dimensionSettingsRegistry.getOrThrow(DimensionSettings.OVERWORLD), biomeRegistry, seed)));


    /*
     * Override the default world type, in a safe, mixin free, and API providing manner :D
     * Thank you gigahertz!
     */
    /*@SuppressWarnings({"rawtypes", "unchecked"})
    public static void setup()
    {
        if (TFCConfig.COMMON.setTFCWorldTypeAsDefault.get() && ForgeConfig.COMMON.defaultWorldType.get().equals("default"))
        {
            ((ForgeConfigSpec.ConfigValue) ForgeConfig.COMMON.defaultWorldType).set(TFCWorldType.WORLD_TYPE.getId().toString());
        }
    }*/

    //TODO: Fix
    public static void setup()
    {
        if (TerraFirmaCraft.getConfig().commonConfig.general.setTFCWorldTypeAsDefault)
        {
            List<GeneratorType> types = GeneratorTypeAccessor.accessor$getVALUES();
            types.add(0, WORLD_TYPE);
        }
        else
        {
            GeneratorTypeAccessor.accessor$getVALUES().add(WORLD_TYPE);
        }

    }

    public static void register() {}
}
