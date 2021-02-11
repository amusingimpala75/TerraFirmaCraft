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

import static net.dries007.tfc.world.layer.TFCLayerUtil.LAKE_MARKER;

/**
 * Mixes lakes into the standard biome layer
 */
public enum MixLakeLayer implements MergingLayer, IdentityCoordinateTransformer
{
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, LayerSampler mainArea, LayerSampler lakeArea, int x, int z)
    {
        int mainValue = mainArea.sample(transformX(x), transformZ(z));
        int lakeValue = lakeArea.sample(transformX(x), transformZ(z));
        if (lakeValue == LAKE_MARKER && TFCLayerUtil.hasLake(mainValue))
        {
            return TFCLayerUtil.lakeFor(mainValue);
        }
        return mainValue;
    }
}
