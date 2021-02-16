/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public enum ForestInitLayer implements InitLayer
{
    INSTANCE;

    private final INoise2D forestBaseNoise;

    ForestInitLayer()
    {
        forestBaseNoise = new OpenSimplex2D(2).spread(0.3f);
    }

    @Override
    public int sample(LayerRandomnessSource context, int x, int z)
    {
        final float noise = forestBaseNoise.noise(x, z);
        if (noise < 0)
        {
            return TFCLayerUtil.FOREST_NONE;
        }
        else
        {
            return TFCLayerUtil.FOREST_NORMAL;
        }
    }
}
