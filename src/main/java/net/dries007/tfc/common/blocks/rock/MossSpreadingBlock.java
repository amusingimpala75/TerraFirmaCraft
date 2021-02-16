/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.Random;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.fabric.duck.WorldDuck;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.config.TFCConfig;

public class MossSpreadingBlock extends Block
{
    public static void spreadMoss(World world, BlockPos pos, BlockState state, Random random)
    {
        if (((WorldDuck)world).inject$isAreaLoaded(pos, 5) && TerraFirmaCraft.getConfig().serverConfig.blocks.cobble.enableMossyRockSpreading && random.nextInt(TerraFirmaCraft.getConfig().serverConfig.blocks.cobble.mossyRockSpreadRate) == 0)
        {
            BlockPos targetPos = pos.add(random.nextInt(4) - random.nextInt(4), random.nextInt(4) - random.nextInt(4), random.nextInt(4) - random.nextInt(4));
            BlockState targetState = world.getBlockState(targetPos);
            if (targetState.getBlock() instanceof IMossGrowingBlock)
            {
                ((IMossGrowingBlock) targetState.getBlock()).convertToMossy(world, targetPos, targetState, true);
            }
        }
    }

    public MossSpreadingBlock(Settings properties)
    {
        super(properties.ticksRandomly());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        MossSpreadingBlock.spreadMoss(worldIn, pos, state, random);
    }
}
