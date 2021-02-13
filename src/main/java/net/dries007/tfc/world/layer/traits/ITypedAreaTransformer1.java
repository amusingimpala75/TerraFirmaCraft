/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;

/**
 * Like {@link net.minecraft.world.biome.layer.util.CoordinateTransformer} but with {@link TypedArea}
 */
public interface ITypedAreaTransformer1<A> extends CoordinateTransformer
{
    default ITypedAreaFactory<A> run(ITypedNoiseRandom<A> context, ITypedAreaFactory<A> areaFactory)
    {
        return () -> {
            TypedArea<A> area = areaFactory.make();
            return context.createTypedResult((x, z) -> {
                context.initSeed(x, z);
                return apply(context, area, x, z);
            });
        };
    }

    A apply(ITypedNoiseRandom<A> context, TypedArea<A> area, int x, int z);
}
