/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

public abstract class TreeFeature<C extends FeatureConfig> extends Feature<C>
{
    protected TreeFeature(Codec<C> codec)
    {
        super(codec);
    }

    protected boolean isValidLocation(WorldAccess worldIn, BlockPos pos)
    {
        BlockState stateDown = worldIn.getBlockState(pos.down());
        if (!TFCTags.Blocks.TREE_GROWS_ON.contains(stateDown.getBlock()))
        {
            return false;
        }

        BlockState stateAt = worldIn.getBlockState(pos);
        //return stateAt.getBlock() instanceof SaplingBlock || stateAt.isAir(worldIn, pos);
        return stateAt.getBlock() instanceof SaplingBlock || stateAt.isAir();
    }

    @SuppressWarnings("deprecation")
    protected boolean isAreaClear(WorldAccess world, BlockPos pos, int radius, int height)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int y = 0; y < height; y++)
        {
            boolean passed = true;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.set(pos);
                    mutablePos.move(x, y, z);
                    BlockState stateAt = world.getBlockState(mutablePos);
                    if (!stateAt.isAir())
                    {
                        passed = false;
                        break;
                    }
                }
                if (!passed)
                {
                    break;
                }
            }
            if (passed)
            {
                return true;
            }
        }
        return false;
    }
}