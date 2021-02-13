/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.mixin.world.gen.feature.WorldDecoratingHelperAccessor;

public abstract class SeededDecorator<C extends DecoratorConfig> extends Decorator<C>
{
    private long cachedSeed;
    private boolean initialized;

    protected SeededDecorator(Codec<C> codec)
    {
        super(codec);
    }

    @Override
    public final Stream<BlockPos> getPositions(DecoratorContext helper, Random rand, C config, BlockPos pos)
    {
        long seed = ((WorldDecoratingHelperAccessor) helper).accessor$getLevel().getSeed();
        if (!initialized || cachedSeed != seed)
        {
            initSeed(seed);
            cachedSeed = seed;
            initialized = true;
        }
        return getSeededPositions(helper, rand, config, pos);
    }

    protected abstract void initSeed(long seed);

    protected abstract Stream<BlockPos> getSeededPositions(DecoratorContext helper, Random rand, C config, BlockPos pos);
}
