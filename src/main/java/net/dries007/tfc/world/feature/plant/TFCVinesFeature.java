/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.plant.TFCVineBlock;

public class TFCVinesFeature extends Feature<VineConfig>
{
    private static final Direction[] DIRECTIONS = Direction.values();

    public TFCVinesFeature(Codec<VineConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, VineConfig config)
    {
        BlockPos.Mutable mutablePos = pos.mutableCopy();
        BlockState state = config.getState();
        List<Direction> dirs = new ArrayList<>(4);
        int r = config.getRadius();

        for (int j = 0; j < config.getTries(); j++)
        {
            for (int y = config.getMinHeight(); y < config.getMaxHeight(); ++y)
            {
                mutablePos.set(pos);
                mutablePos.move(rand.nextInt(r) - rand.nextInt(r), 0, rand.nextInt(r) - rand.nextInt(r));
                mutablePos.setY(y);
                if (world.isAir(mutablePos))
                {
                    for (Direction direction : DIRECTIONS)
                    {
                        mutablePos.move(direction);
                        BlockState foundState = world.getBlockState(mutablePos);
                        if (direction != Direction.DOWN && (foundState.isIn(TFCTags.Blocks.CREEPING_PLANTABLE_ON) || foundState.isIn(BlockTags.LOGS) || foundState.isIn(BlockTags.LEAVES)))
                        {
                            mutablePos.move(direction.getOpposite());
                            world.setBlockState(mutablePos, state.with(TFCVineBlock.getFacingProperty(direction), true), 2);
                            if (direction != Direction.UP)
                                dirs.add(direction);
                            break;
                        }
                        mutablePos.move(direction.getOpposite());
                    }
                    if (!dirs.isEmpty())
                    {
                        for (int k = 0; k < 6 + rand.nextInt(13); k++)
                        {
                            mutablePos.move(Direction.DOWN);
                            if (world.isAir(mutablePos))
                            {
                                for (Direction direction : dirs)
                                {
                                    world.setBlockState(mutablePos, state.with(TFCVineBlock.getFacingProperty(direction), true), 2);
                                }
                            }
                        }
                        dirs.clear();
                    }
                }
            }
        }


        return true;
    }
}
