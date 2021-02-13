/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.function.Function;

import net.dries007.tfc.util.Helpers;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;

import com.mojang.serialization.Codec;

@SuppressWarnings("unused")
public class TFCCarvers
{
    public static final TFCCaveCarver CAVE = register("cave", TFCCaveCarver::new, ProbabilityConfig.CODEC);
    public static final TFCRavineCarver CANYON = register("canyon", TFCRavineCarver::new, ProbabilityConfig.CODEC);
    public static final TFCUnderwaterCaveCarver UNDERWATER_CAVE = register("underwater_cave", TFCUnderwaterCaveCarver::new, ProbabilityConfig.CODEC);
    public static final TFCUnderwaterRavineCarver UNDERWATER_CANYON = register("underwater_canyon", TFCUnderwaterRavineCarver::new, ProbabilityConfig.CODEC);

    public static final WorleyCaveCarver WORLEY_CAVE = register("worley_cave", WorleyCaveCarver::new, WorleyCaveConfig.CODEC);

    private static <C extends CarverConfig, WC extends Carver<C>> WC register(String name, Function<Codec<C>, WC> factory, Codec<C> codec)
    {
        return Registry.register(Registry.CARVER, Helpers.identifier(name), factory.apply(codec));
    }

    public static void register() {}
}