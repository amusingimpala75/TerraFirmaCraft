/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class TopPlantBlock extends AbstractPlantStemBlock
{
    private final Supplier<? extends Block> bodyBlock;

    public TopPlantBlock(AbstractBlock.Settings properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape)
    {
        super(properties, direction, shape, false, 0);
        this.bodyBlock = bodyBlock;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (state.get(AGE) < 25 )//&& ForgeHooks.onCropsGrowPre(worldIn, pos.offset(growthDirection), worldIn.getBlockState(pos.offset(growthDirection)), random.nextDouble() < TFCConfig.SERVER.plantGrowthChance.get()))
        {
            BlockPos blockpos = pos.offset(growthDirection);
            if (chooseStemState(worldIn.getBlockState(blockpos)))
            {
                worldIn.setBlockState(blockpos, state.cycle(AGE));
                //ForgeHooks.onCropsGrowPost(worldIn, blockpos, worldIn.getBlockState(blockpos));
            }
        }
    }

    @Override // lifted from AbstractPlantBlock to add leaves to it
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.offset(growthDirection.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!canAttachTo(block))
        {
            return false;
        }
        else
        {
            return block == getPlant() || block == getStem() || blockstate.isIn(BlockTags.LEAVES) || blockstate.isSideSolidFullSquare(worldIn, blockpos, growthDirection);
        }
    }

    @Override
    protected int method_26376(Random rand)
    {
        return 0;
    }

    @Override
    public boolean isFertilizable(BlockView worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    protected Block getPlant()
    {
        return bodyBlock.get();
    }

    @Override
    protected boolean chooseStemState(BlockState state)
    {
        return VineLogic.isValidForWeepingStem(state);
    }
}
