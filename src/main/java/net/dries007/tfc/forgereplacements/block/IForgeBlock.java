package net.dries007.tfc.forgereplacements.block;

import net.dries007.tfc.mixin.fabric.block.FireBlockAccessor;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public interface IForgeBlock {

    default Block getBlock()
    {
        return (Block) this;
    }

    default boolean hasTileEntity(BlockState state) {
        return this instanceof BlockEntityProvider;
    }

    @Nullable
    default BlockEntity createTileEntity(BlockState state, BlockView world)
    {
        if (getBlock() instanceof BlockEntityProvider)
            return ((BlockEntityProvider)getBlock()).createBlockEntity(world);
        return null;
    }

    default int getFlammability(BlockState state, BlockView world, BlockPos pos, Direction face)
    {
        return ((FireBlockAccessor) Blocks.FIRE).call$getSpreadChance(state);
    }

    default int getFireSpreadSpeed(BlockState state, BlockView world, BlockPos pos, Direction face)
    {
        return ((FireBlockAccessor)Blocks.FIRE).call$getBurnChance(state);
    }

    default boolean isBed(BlockState state, BlockView world, BlockPos pos, @Nullable Entity player)
    {
        return this.getBlock() instanceof BedBlock;
    }
}
