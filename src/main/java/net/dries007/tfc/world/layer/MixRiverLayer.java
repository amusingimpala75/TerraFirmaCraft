/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER_MARKER;

public enum MixRiverLayer implements MergingLayer, IdentityCoordinateTransformer
{
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, LayerSampler mainArea, LayerSampler riverArea, int x, int z)
    {
        int mainValue = mainArea.sample(transformX(x), transformZ(z));
        int riverValue = riverArea.sample(transformX(x), transformZ(z));
        if (riverValue == RIVER_MARKER && TFCLayerUtil.hasRiver(mainValue))
        {
            return TFCLayerUtil.riverFor(mainValue);
        }
        return mainValue;
    }
}