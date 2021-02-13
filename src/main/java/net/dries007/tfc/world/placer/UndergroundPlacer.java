/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class UndergroundPlacer extends BlockPlacer
{
    public static final Codec<UndergroundPlacer> CODEC = Codec.unit(new UndergroundPlacer());

    @Override
    public void generate(WorldAccess worldIn, BlockPos pos, BlockState state, Random random)
    {
        if (worldIn.getBlockState(pos).getBlock() == Blocks.CAVE_AIR && worldIn.getTopY(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()) > pos.getY())
        {
            worldIn.setBlockState(pos, state, 3);
        }
    }

    @Override
    protected BlockPlacerType<?> getType()
    {
        return TFCBlockPlacers.UNDERGROUND;
    }
}
