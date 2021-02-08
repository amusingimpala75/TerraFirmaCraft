/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.dries007.tfc.util.Helpers;

public class ThatchBlock extends Block implements IForgeBlockProperties
{
    private final ForgeBlockProperties properties;

    public ThatchBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());

        this.properties = properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        Helpers.slowEntityInBlock(entityIn, 0.3f, 5);
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
