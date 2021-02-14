/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SeaIceBlock extends IceBlock
{
    public SeaIceBlock(Settings properties)
    {
        super(properties);
    }

    /**
     * Override to change a reference to water to salt water
     */
    @Override
    public void afterBreak(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack)
    {
        super.afterBreak(worldIn, player, pos, state, te, stack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0)
        {
            if (worldIn.getDimension().isUltrawarm())
            {
                worldIn.removeBlock(pos, false);
                return;
            }

            Material material = worldIn.getBlockState(pos.down()).getMaterial();
            if (material.blocksMovement() || material.isLiquid())
            {
                worldIn.setBlockState(pos, TFCBlocks.SALT_WATER.getDefaultState());
            }
        }
    }

    @Override
    protected void melt(BlockState state, World worldIn, BlockPos pos)
    {
        if (worldIn.getDimension().isUltrawarm())
        {
            worldIn.removeBlock(pos, false);
        }
        else
        {
            // Use salt water here
            worldIn.setBlockState(pos, TFCBlocks.SALT_WATER.getDefaultState());
            worldIn.updateNeighbor(pos, TFCBlocks.SALT_WATER, pos);
        }
    }
}
