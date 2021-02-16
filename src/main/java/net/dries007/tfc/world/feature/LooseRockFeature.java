/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.dries007.tfc.fabric.cca.ChunkDataChunkComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Places a single loose rock at the target position
 */
public class LooseRockFeature extends Feature<DefaultFeatureConfig>
{
    public LooseRockFeature(Codec<DefaultFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess worldIn, ChunkGenerator generator, Random rand, BlockPos pos, DefaultFeatureConfig config)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(generator);
        final ChunkDataChunkComponent data = provider.get(pos, ChunkDataChunkComponent.Status.ROCKS);
        final Rock rock = data.getRockData().getRock(pos.getX(), pos.getY(), pos.getZ());
        final BlockState stateAt = worldIn.getBlockState(pos);
        final BlockState state = getStateToPlace(rock.getBlock(Rock.BlockType.LOOSE).getDefaultState(), stateAt);

        if (state != null && state.canPlaceAt(worldIn, pos))
        {
            setBlockState(worldIn, pos, state.with(TFCBlockStateProperties.COUNT_1_3, 1 + rand.nextInt(2)).with(HorizontalFacingBlock.FACING, Direction.Type.HORIZONTAL.random(rand)));
            return true;
        }
        return false;
    }

    @Nullable
    private BlockState getStateToPlace(BlockState state, BlockState stateAt)
    {
        if (stateAt.isAir())
        {
            return state;
        }
        if (state.getBlock() instanceof IFluidLoggable)
        {
            final FluidProperty property = ((IFluidLoggable) state.getBlock()).getFluidProperty();
            final Fluid fluid = stateAt.getFluidState().getFluid();
            if (property.canContain(fluid) && fluid.matchesType(Fluids.EMPTY))
            {
                return state.with(property, property.keyFor(fluid));
            }
        }
        return null;
    }
}
