/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

/**
 * Initialize a layer filled with NULL_MARKER
 */
public enum NullLayer implements InitLayer
{
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int x, int z)
    {
        return TFCLayerUtil.NULL_MARKER;
    }
}
