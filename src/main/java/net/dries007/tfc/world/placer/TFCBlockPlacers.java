/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import net.dries007.tfc.mixin.fabric.world.BlockPlacerAccessor;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

import com.mojang.serialization.Codec;

public class TFCBlockPlacers
{
    public static final BlockPlacerType<TallPlantPlacer> TALL_PLANT = register("tall_plant", TallPlantPlacer.CODEC);
    public static final BlockPlacerType<EmergentPlantPlacer> EMERGENT_PLANT = register("emergent", EmergentPlantPlacer.CODEC);
    public static final BlockPlacerType<WaterPlantPlacer> WATER_PLANT = register("water_plant", WaterPlantPlacer.CODEC);

    public static final BlockPlacerType<UndergroundPlacer> UNDERGROUND = register("underground", UndergroundPlacer.CODEC);

    private static <P extends BlockPlacer> BlockPlacerType<P> register(String name, Codec<P> codec)
    {
        return BlockPlacerAccessor.call$register(Helpers.identifier(name).toString(), codec);
    }

    public static void register() {}
}
