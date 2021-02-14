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
import net.minecraft.world.gen.decorator.DecoratorContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.mixin.world.gen.feature.WorldDecoratingHelperAccessor;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class ClimateDecorator extends Decorator<ClimateConfig>
{
    public ClimateDecorator(Codec<ClimateConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext helper, Random random, ClimateConfig config, BlockPos pos)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(((WorldDecoratingHelperAccessor) helper).accessor$getGenerator());
        final ChunkData data = provider.get(pos, ChunkData.Status.CLIMATE);
        if (config.isValid(data, pos, random))
        {
            return Stream.of(pos);
        }
        return Stream.empty();
    }
}
