/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import com.mojang.serialization.Codec;

public class TFCCoralClawFeature extends TFCCoralFeature
{
    public TFCCoralClawFeature(Codec<DefaultFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    protected boolean placeFeature(WorldAccess world, Random rand, BlockPos blockPos_, BlockState state)
    {
        if (!placeCoralBlock(world, rand, blockPos_, state))
        {
            return false;
        }
        else
        {
            Direction direction = Direction.Type.HORIZONTAL.random(rand);
            int directionTries = rand.nextInt(2) + 2;
            List<Direction> dirs = Lists.newArrayList(direction, direction.rotateYClockwise(), direction.rotateYCounterclockwise());
            Collections.shuffle(dirs, rand);

            for (Direction d : dirs.subList(0, directionTries))
            {
                BlockPos.Mutable mutablePos = blockPos_.mutableCopy();
                int j = rand.nextInt(2) + 1;
                mutablePos.move(d);
                int k;
                Direction direction2;
                if (d == direction)
                {
                    direction2 = direction;
                    k = rand.nextInt(3) + 2;
                }
                else
                {
                    mutablePos.move(Direction.UP);
                    Direction[] upOrSide = new Direction[] {d, Direction.UP};
                    direction2 = Util.getRandom(upOrSide, rand);
                    k = rand.nextInt(3) + 3;
                }

                for (int l = 0; l < j && placeCoralBlock(world, rand, mutablePos, state); ++l)
                {
                    mutablePos.move(direction2);
                }

                mutablePos.move(direction2.getOpposite());
                mutablePos.move(Direction.UP);

                for (int i1 = 0; i1 < k; ++i1)
                {
                    mutablePos.move(direction);
                    if (!placeCoralBlock(world, rand, mutablePos, state))
                    {
                        break;
                    }

                    if (rand.nextFloat() < 0.25F)
                    {
                        mutablePos.move(Direction.UP);
                    }
                }
            }

            return true;
        }
    }
}
