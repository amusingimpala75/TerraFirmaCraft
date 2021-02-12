/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

public class CaveVegetationFeature extends Feature<CaveVegetationConfig>
{
    public CaveVegetationFeature(Codec<CaveVegetationConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess worldIn, ChunkGenerator generator, Random rand, BlockPos pos, CaveVegetationConfig config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int i = 0; i < 128; i++)
        {
            if (rand.nextFloat() < 0.8f)//mossy cobble
            {
                mutablePos.set(pos, rand.nextInt(15) - rand.nextInt(15), -1 * rand.nextInt(2) - 1, rand.nextInt(15) - rand.nextInt(15));
                if (worldIn.isAir(mutablePos))
                {
                    for (int j = 0; j < 7; j++)
                    {
                        mutablePos.move(0, -1, 0);
                        if (!worldIn.isAir(mutablePos))
                        {
                            break;
                        }
                    }
                    BlockState generateState = config.getStateToGenerate(worldIn.getBlockState(mutablePos), rand);
                    if (generateState != null)
                    {
                        setBlockState(worldIn, mutablePos, generateState);
                    }
                }
            }
            if (rand.nextFloat() < 0.003f)//extra springs
            {
                mutablePos.set(pos, rand.nextInt(15) - rand.nextInt(15), 4 + rand.nextInt(7), rand.nextInt(15) - rand.nextInt(15));
                if (worldIn.isAir(mutablePos))
                {
                    mutablePos.move(Direction.UP);
                    if (worldIn.getBlockState(mutablePos).isIn(BlockTags.BASE_STONE_OVERWORLD))
                    {
                        setBlockState(worldIn, mutablePos, Fluids.WATER.getDefaultState().getBlockState());
                        worldIn.getFluidTickScheduler().schedule(mutablePos, Fluids.WATER, 0);
                    }
                }
            }
            if (rand.nextFloat() < 0.02f)//cobwebs
            {
                mutablePos.set(pos, rand.nextInt(15) - rand.nextInt(15), 4 + rand.nextInt(7), rand.nextInt(15) - rand.nextInt(15));
                if (worldIn.getBlockState(mutablePos).isIn(BlockTags.BASE_STONE_OVERWORLD))
                {
                    mutablePos.move(Direction.DOWN);
                    if (worldIn.isAir(mutablePos))
                    {
                        setBlockState(worldIn, mutablePos, Blocks.COBWEB.getDefaultState());
                    }
                }
            }
        }
        return true;
    }

    @Nullable
    protected BlockState getStateToGenerate(BlockState stoneState, Random random, CaveVegetationConfig config)
    {
        return config.getStateToGenerate(stoneState, random);
    }
}
