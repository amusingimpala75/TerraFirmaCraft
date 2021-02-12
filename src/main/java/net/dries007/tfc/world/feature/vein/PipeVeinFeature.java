/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.Random;

import net.minecraft.util.math.BlockBox;

import com.mojang.serialization.Codec;

public class PipeVeinFeature extends VeinFeature<PipeVeinConfig, Vein>
{
    public PipeVeinFeature(Codec<PipeVeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected BlockBox getBoundingBox(PipeVeinConfig config)
    {
        return new BlockBox(-config.getRadius(), -config.getSize(), -config.getRadius(), config.getRadius(), config.getSize(), config.getRadius());
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, Vein vein, PipeVeinConfig config)
    {
        if (Math.abs(y) < config.getSize() && (x * x) + (z * z) < config.getRadius() * config.getRadius())
        {
            return config.getDensity();
        }
        return 0;
    }

    @Override
    protected Vein createVein(int chunkX, int chunkZ, Random random, PipeVeinConfig config)
    {
        return new Vein(defaultPos(chunkX, chunkZ, random, config));
    }
}
