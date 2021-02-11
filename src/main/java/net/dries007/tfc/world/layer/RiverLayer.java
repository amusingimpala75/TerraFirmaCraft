/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import static net.dries007.tfc.world.layer.TFCLayerUtil.NULL_MARKER;
import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER_MARKER;

public enum RiverLayer implements CrossSamplingLayer
{
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int north, int east, int south, int west, int center)
    {
        if (center != north || center != south || center != west || center != east)
        {
            return RIVER_MARKER;
        }
        return NULL_MARKER;
    }
}