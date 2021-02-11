/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ExactZoomLayer implements ParentedLayer
{
    INSTANCE;

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler area, int x, int z)
    {
        return area.sample(transformX(x), transformZ(z));
    }

    @Override
    public int transformX(int x)
    {
        return x >> 1;
    }

    @Override
    public int transformZ(int z)
    {
        return z >> 1;
    }
}