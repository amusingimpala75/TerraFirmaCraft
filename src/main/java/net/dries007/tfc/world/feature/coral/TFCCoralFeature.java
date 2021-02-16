/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Random;

import net.dries007.tfc.common.blocks.FluidBlockStateProprties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.coral.TFCDeadCoralWallFanBlock;
import net.dries007.tfc.common.fluids.TFCFluids;

public abstract class TFCCoralFeature extends Feature<DefaultFeatureConfig>
{
    public TFCCoralFeature(Codec<DefaultFeatureConfig> codec_)
    {
        super(codec_);
    }

    @Override
    public boolean generate(StructureWorldAccess reader, ChunkGenerator generator, Random rand, BlockPos pos, DefaultFeatureConfig config)
    {
        BlockState coralBlockState = BlockTags.CORAL_BLOCKS.getRandom(rand).getDefaultState();
        return placeFeature(reader, rand, pos, coralBlockState);
    }

    protected abstract boolean placeFeature(WorldAccess world, Random rand, BlockPos pos, BlockState state);

    protected boolean placeCoralBlock(WorldAccess world, Random rand, BlockPos pos, BlockState coralBlockState)
    {
        BlockPos abovePos = pos.up();
        BlockState blockstate = world.getBlockState(pos);
        if ((blockstate.isOf(TFCBlocks.SALT_WATER) || blockstate.isIn(TFCTags.Blocks.CORALS)) && world.getBlockState(abovePos).isOf(TFCBlocks.SALT_WATER))
        {
            world.setBlockState(pos, coralBlockState, 3);
            if (rand.nextFloat() < 0.25F)
            {
                world.setBlockState(abovePos, salty(TFCTags.Blocks.CORALS.getRandom(rand).getDefaultState()), 2);
            }
            else if (rand.nextFloat() < 0.05F)
            {
                world.setBlockState(abovePos, salty(TFCBlocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, rand.nextInt(4) + 1)), 2);
            }

            for (Direction direction : Direction.Type.HORIZONTAL)
            {
                if (rand.nextFloat() < 0.2F)
                {
                    BlockPos relativePos = pos.offset(direction);
                    if (world.getBlockState(relativePos).isOf(Blocks.WATER))
                    {
                        BlockState wallCoralState = salty(TFCTags.Blocks.WALL_CORALS.getRandom(rand).getDefaultState()).with(TFCDeadCoralWallFanBlock.FACING, direction);
                        world.setBlockState(relativePos, wallCoralState, 2);
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private BlockState salty(BlockState state)
    {
        return state.with(FluidBlockStateProprties.SALT_WATER, FluidBlockStateProprties.SALT_WATER.keyFor(TFCFluids.SALT_WATER.getSource()));
    }
}
