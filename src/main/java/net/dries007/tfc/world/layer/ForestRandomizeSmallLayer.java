/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public enum ForestRandomizeSmallLayer implements SouthEastSamplingLayer
{
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int value)
    {
        if (value == FOREST_NORMAL || value == FOREST_OLD)
        {
            final int random = context.nextInt((value == FOREST_OLD ? 40 : 25));
            if (random == 0)
            {
                return FOREST_NONE;
            }
            else if (random == 1)
            {
                return FOREST_SPARSE;
            }
        }
        else if (value == FOREST_SPARSE || value == FOREST_NONE)
        {
            final int random = context.nextInt(30);
            if (random == 0)
            {
                return value == FOREST_SPARSE ? FOREST_NORMAL : FOREST_EDGE;
            }
        }
        return value;
    }
}
