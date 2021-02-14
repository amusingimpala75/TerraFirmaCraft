/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class FloatingWaterPlantBlock extends PlantBlock
{
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);
    private final Supplier<? extends Fluid> fluid;

    public static FloatingWaterPlantBlock create(IPlant plant, Supplier<? extends Fluid> fluid, Settings properties)
    {
        return new FloatingWaterPlantBlock(properties, fluid)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected FloatingWaterPlantBlock(Settings properties, Supplier<? extends Fluid> fluid)
    {
        super(properties);
        this.fluid = fluid;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.down());
        return (belowState.getFluidState() != Fluids.EMPTY.getDefaultState() && isValidFluid(belowState.getFluidState().getFluid()));
    }

    /**
     * {@link LilyPadBlock#onEntityCollision}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        if (worldIn instanceof ServerWorld && entityIn instanceof BoatEntity)
        {
            worldIn.breakBlock(new BlockPos(pos), true, entityIn);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    private boolean isValidFluid(Fluid fluidIn)
    {
        return fluidIn.matchesType(fluid.get());
    }
}
