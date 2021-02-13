/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.TFCTallGrassBlock;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;

public class TallPlantPlacer extends BlockPlacer
{
    public static final Codec<TallPlantPlacer> CODEC = Codec.unit(new TallPlantPlacer());

    @Override
    public void generate(WorldAccess worldIn, BlockPos pos, BlockState state, Random random)
    {
        ((TFCTallGrassBlock)state.getBlock()).placeTwoHalves(worldIn, pos, 2, random);
    }

    protected BlockPlacerType<?> getType() {
        return TFCBlockPlacers.TALL_PLANT;
    }
}
