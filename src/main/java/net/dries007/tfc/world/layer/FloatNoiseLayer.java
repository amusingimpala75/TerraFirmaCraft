/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import net.dries007.tfc.world.noise.INoise2D;

public class FloatNoiseLayer implements InitLayer
{
    private final INoise2D noise;

    public FloatNoiseLayer(INoise2D noise)
    {
        this.noise = noise;
    }

    @Override
    public int sample(LayerRandomnessSource context, int x, int z)
    {
        return Float.floatToRawIntBits(noise.noise(x, z));
    }
}
