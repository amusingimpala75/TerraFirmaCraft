/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.dries007.tfc.forgereplacements.block.IForgeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

/**
 * This implements some of the more annoying methods in {@link IForgeBlock} which would otherwise require implementing across all manner of vanilla subclasses.
 * Since forge has made the decision that blocks should have behavioral control rather than add entries to {@link net.minecraft.block.AbstractBlock.Properties}, we mimic the same structure here.
 */
public interface IForgeBlockProperties extends IForgeBlock
{
    ForgeBlockProperties getForgeProperties();

    @Override
    default boolean hasTileEntity(BlockState state)
    {
        return getForgeProperties().hasTileEntity();
    }

    @Nullable
    @Override
    default BlockEntity createTileEntity(BlockState state, BlockView world)
    {
        return getForgeProperties().createTileEntity();
    }

    @Override
    default int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face)
    {
        return getForgeProperties().getFlammability();
    }

    @Override
    default int getFireSpreadSpeed(BlockState state, BlockView world, BlockPos pos, Direction face)
    {
        return getForgeProperties().getFireSpreadSpeed();
    }
}
