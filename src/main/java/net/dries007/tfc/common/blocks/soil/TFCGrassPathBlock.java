/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.common.blocks.TFCBlocks;


public class TFCGrassPathBlock extends GrassPathBlock implements ISoilBlock
{
    private final Supplier<Block> dirtBlock;

    public TFCGrassPathBlock(Settings builder, SoilBlockType soil, SoilBlockType.Variant variant)
    {
        this(builder, () -> TFCBlocks.SOIL.get(soil).get(variant));
    }

    protected TFCGrassPathBlock(Settings builder, Supplier<Block> dirtBlock)
    {
        super(builder);

        this.dirtBlock = dirtBlock;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState state = getDefaultState();
        if (!state.canPlaceAt(context.getWorld(), context.getBlockPos()))
        {
            return Block.pushEntitiesUpBeforeBlockChange(state, getDirt(), context.getWorld(), context.getBlockPos());
        }
        return super.getPlacementState(context);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        worldIn.setBlockState(pos, Block.pushEntitiesUpBeforeBlockChange(state, getDirt(), worldIn, pos));
    }

    @Override
    public BlockState getDirt()
    {
        return dirtBlock.get().getDefaultState();
    }
}