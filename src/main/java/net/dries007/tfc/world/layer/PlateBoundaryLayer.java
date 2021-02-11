/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

import net.dries007.tfc.world.layer.traits.FastArea;
import net.dries007.tfc.world.layer.traits.ITypedAreaFactory;
import net.dries007.tfc.world.layer.traits.TypedArea;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public enum PlateBoundaryLayer implements NorthWestCoordinateTransformer
{
    INSTANCE;

    public static final float SHEAR_THRESHOLD = 0.9f;

    public static final float HIGH_ELEVATION = 0.66f;
    public static final float MID_ELEVATION = 0.33f;

    public LayerFactory<FastArea> run(LayerSampleContext<FastArea> context, ITypedAreaFactory<Plate> plateLayer)
    {
        return () -> {
            TypedArea<Plate> area = plateLayer.make();
            return context.createSampler((x, z) -> {
                context.initSeed(x, z);
                return apply(context, area, x, z);
            });
        };
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private int apply(LayerSampleContext<?> context, TypedArea<Plate> area, int x, int z)
    {
        return apply(context,
            area.get(transformX(x + 1), transformZ(z + 0)),
            area.get(transformX(x + 2), transformZ(z + 1)),
            area.get(transformX(x + 1), transformZ(z + 2)),
            area.get(transformX(x + 0), transformZ(z + 1)),
            area.get(transformX(x + 1), transformZ(z + 1))
        );
    }

    private int apply(LayerSampleContext<?> context, Plate north, Plate west, Plate south, Plate east, Plate center)
    {
        Plate boundary = null;
        int boundaryCount = 0;
        if (!north.equals(center))
        {
            boundaryCount++;
            boundary = north;
        }
        if (!west.equals(center))
        {
            boundaryCount++;
            if (boundary == null || context.nextInt(boundaryCount) == 0)
            {
                boundary = west;
            }
        }
        if (!south.equals(center))
        {
            boundaryCount++;
            if (boundary != null || context.nextInt(boundaryCount) == 0)
            {
                boundary = south;
            }
        }
        if (!east.equals(center))
        {
            boundaryCount++;
            if (boundary != null || context.nextInt(boundaryCount) == 0)
            {
                boundary = east;
            }
        }
        if (boundary != null)
        {
            return boundary(center, boundary);
        }
        return plate(center);
    }

    private int boundary(Plate center, Plate other)
    {
        float distX = center.getX() - other.getX(), distZ = center.getZ() - other.getZ();
        float vX = center.getDriftX() - other.getDriftX(), vZ = center.getDriftZ() - other.getDriftZ();
        float delta = distX * vX + distZ * vZ;
        if (delta > SHEAR_THRESHOLD)
        {
            // Converging
            if (center.isOceanic() && other.isOceanic())
            {
                return center.getElevation() > other.getElevation() ? OCEAN_OCEAN_CONVERGING_UPPER : OCEAN_OCEAN_CONVERGING_LOWER;
            }
            else if (center.isOceanic())
            {
                return OCEAN_CONTINENT_CONVERGING_LOWER;
            }
            else if (other.isOceanic())
            {
                return OCEAN_CONTINENT_CONVERGING_UPPER;
            }
            return CONTINENT_CONTINENT_CONVERGING;
        }
        else if (delta < -SHEAR_THRESHOLD)
        {
            // Diverging
            if (center.isOceanic() && other.isOceanic())
            {
                return OCEAN_OCEAN_DIVERGING;
            }
            else if (center.isOceanic() || other.isOceanic())
            {
                return OCEAN_CONTINENT_DIVERGING;
            }
            return CONTINENT_CONTINENT_DIVERGING;
        }
        return plate(center);
    }

    private int plate(Plate center)
    {
        if (center.isOceanic())
        {
            return OCEANIC;
        }
        if (center.getElevation() > HIGH_ELEVATION)
        {
            return CONTINENTAL_HIGH;
        }
        if (center.getElevation() > MID_ELEVATION)
        {
            return CONTINENTAL_MID;
        }
        return CONTINENTAL_LOW;
    }
}
