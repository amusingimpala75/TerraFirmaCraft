/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class RandomizeNeighborsLayer extends CallbackLimitLayer implements CrossSamplingLayer
{
    public RandomizeNeighborsLayer(int limit)
    {
        super(limit);
    }

    @Override
    public int sample(LayerRandomnessSource context, int north, int east, int south, int west, int center)
    {
        if (north == center || east == center || south == center || west == center)
        {
            // Pick a different random
            return context.nextInt(limit);
        }
        return center;
    }
}