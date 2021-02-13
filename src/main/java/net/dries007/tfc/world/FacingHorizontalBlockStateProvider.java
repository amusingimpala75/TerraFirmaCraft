/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class FacingHorizontalBlockStateProvider extends BlockStateProvider
{
    public static final Codec<FacingHorizontalBlockStateProvider> CODEC = Codecs.LENIENT_BLOCKSTATE.fieldOf("state").xmap(FacingHorizontalBlockStateProvider::new, provider -> provider.state).codec();

    private final BlockState state;

    public FacingHorizontalBlockStateProvider(BlockState state)
    {
        this.state = state;
    }

    @Override
    protected BlockStateProviderType<?> getType()
    {
        return TFCBlockStateProviderTypes.FACING_PROVIDER;
    }

    @Override
    public BlockState getBlockState(Random random, BlockPos pos)
    {
        Direction facing = Direction.Type.HORIZONTAL.random(random);
        return this.state.with(HorizontalFacingBlock.FACING, facing);
    }
}
