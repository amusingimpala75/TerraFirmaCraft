/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import static net.dries007.tfc.world.layer.TFCLayerUtil.INLAND_MARKER;
import static net.dries007.tfc.world.layer.TFCLayerUtil.LAKE_MARKER;

/**
 * Adds instances of lake markers to a layer randomly
 */
public enum AddLakesLayer implements SouthEastSamplingLayer
{
    SMALL(40),
    LARGE(160);

    private final int chance;

    AddLakesLayer(int chance)
    {
        this.chance = chance;
    }

    @Override
    public int sample(LayerRandomnessSource context, int value)
    {
        if (value == INLAND_MARKER && context.nextInt(chance) == 0)
        {
            return LAKE_MARKER;
        }
        return value;
    }
}