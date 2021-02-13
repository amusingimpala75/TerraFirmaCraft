/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.function.Function;

import net.dries007.tfc.util.Helpers;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

import com.mojang.serialization.Codec;

@SuppressWarnings("unused")
public final class TFCDecorators
{
    public static final FlatEnoughDecorator FLAT_ENOUGH = register("flat_enough", FlatEnoughDecorator::new, FlatEnoughConfig.CODEC);
    public static final ClimateDecorator CLIMATE = register("climate", ClimateDecorator::new, ClimateConfig.CODEC);
    public static final NearWaterDecorator NEAR_WATER = register("near_water", NearWaterDecorator::new, NearWaterConfig.CODEC);
    public static final BoundedCarvingMaskDecorator BOUNDED_CARVING_MASK = register("bounded_carving_mask", BoundedCarvingMaskDecorator::new, BoundedCarvingMaskConfig.CODEC);
    public static final VolcanoDecorator VOLCANO = register("volcano", VolcanoDecorator::new, VolcanoConfig.CODEC);
    public static final ShorelineDecorator SHORELINE = register("shoreline", ShorelineDecorator::new, NearWaterConfig.CODEC);

    private static <C extends DecoratorConfig, D extends Decorator<C>> D register(String name, Function<Codec<C>, D> factory, Codec<C> codec)
    {
        return Registry.register(Registry.DECORATOR, Helpers.identifier(name), factory.apply(codec));
    }

    public static void register() {}
}
