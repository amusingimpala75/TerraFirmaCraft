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
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BodyPlantBlock extends AbstractPlantBlock
{
    private final Supplier<? extends Block> headBlock;

    public BodyPlantBlock(AbstractBlock.Settings properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties, direction, shape, false);
        this.headBlock = headBlock;
    }

    @Override
    protected AbstractPlantStemBlock getStem()
    {
        return (AbstractPlantStemBlock) headBlock.get();
    }

    @Override
    public boolean isFertilizable(BlockView worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public boolean canGrow(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return false;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state)
    {

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
            return block == getStem() || block == getPlant() || blockstate.isIn(BlockTags.LEAVES) || blockstate.isSideSolidFullSquare(worldIn, blockpos, growthDirection);
        }
    }
}
