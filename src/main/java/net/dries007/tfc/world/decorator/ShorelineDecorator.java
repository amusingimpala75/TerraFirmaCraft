/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

public class ShorelineDecorator extends Decorator<NearWaterConfig>
{
    public ShorelineDecorator(Codec<NearWaterConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext helper, Random random, NearWaterConfig config, BlockPos pos)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final int radius = config.getRadius();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                mutablePos.set(pos).move(x, 0, z);
                mutablePos.setY(helper.getTopY(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));
                BlockState state = helper.getBlockState(mutablePos);
                if (!state.isAir())
                    return Stream.empty();
                mutablePos.move(Direction.DOWN);
                state = helper.getBlockState(mutablePos);
                if (state.isIn(TFCTags.Blocks.BUSH_PLANTABLE_ON) || state.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                {
                    for (Direction d : Direction.Type.HORIZONTAL)
                    {
                        mutablePos.move(d);
                        if (helper.getBlockState(mutablePos).getFluidState().isIn(FluidTags.WATER))
                        {
                            return Stream.of(pos);
                        }
                        mutablePos.move(d.getOpposite());
                    }
                }
            }
        }
        return Stream.empty();
    }
}
