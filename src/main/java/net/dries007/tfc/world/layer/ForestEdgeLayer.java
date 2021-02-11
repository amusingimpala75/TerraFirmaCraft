package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public enum ForestEdgeLayer implements CrossSamplingLayer
{
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int north, int east, int south, int west, int center)
    {
        if (isFullForest(center))
        {
            if (!isFullForest(north) || !isFullForest(east) || !isFullForest(south) || !isFullForest(west))
            {
                return FOREST_EDGE;
            }
        }
        return center;
    }

    private boolean isFullForest(int value)
    {
        return value == FOREST_NORMAL || value == FOREST_OLD;
    }
}
