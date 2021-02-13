/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class IslandLayer implements InitLayer
{
    private final int islandFrequency;

    public IslandLayer(int islandFrequency)
    {
        this.islandFrequency = islandFrequency;
    }

    @Override
    public int sample(LayerRandomnessSource random, int x, int z)
    {
        if (x == 0 && z == 0)
        {
            return TFCLayerUtil.PLAINS;
        }
        else
        {
            return random.nextInt(islandFrequency) == 0 ? TFCLayerUtil.PLAINS : TFCLayerUtil.DEEP_OCEAN;
        }
    }
}