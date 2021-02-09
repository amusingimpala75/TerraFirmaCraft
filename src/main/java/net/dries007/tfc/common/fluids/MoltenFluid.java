/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.Random;

import net.dries007.tfc.forgereplacements.fluid.FlowableFluid;
import net.dries007.tfc.forgereplacements.fluid.FluidProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class MoltenFluid extends FlowableFluid
{
    private final LavaFluid lava;

    protected MoltenFluid(FluidProperties properties)
    {
        super(properties);
        this.lava = (LavaFluid) Fluids.LAVA;
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void randomDisplayTick(World worldIn, BlockPos pos, FluidState state, Random random)
    {
        lava.randomDisplayTick(worldIn, pos, state, random);
    }

    @Override
    protected void onRandomTick(World worldIn, BlockPos pos, FluidState state, Random random)
    {
        lava.onRandomTick(worldIn, pos, state, random);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected ParticleEffect getParticle()
    {
        return lava.getParticle();
    }

    @Override
    protected boolean hasRandomTicks()
    {
        return true;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess worldIn, BlockPos pos, BlockState state)
    {
        worldIn.syncWorldEvent(1501, pos, 0);
    }

    @Override
    protected int getFlowSpeed(WorldView worldIn)
    {
        return lava.getFlowSpeed(worldIn);
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView worldIn)
    {
        return lava.getLevelDecreasePerBlock(worldIn);
    }

    @Override
    public int getTickRate(WorldView world)
    {
        return lava.getTickRate(world);
    }

    @Override
    protected int getNextTickDelay(World worldIn, BlockPos pos, FluidState fluidState_, FluidState fluidState1_)
    {
        return lava.getNextTickDelay(worldIn, pos, fluidState_, fluidState1_);
    }

    public static class Flowing extends MoltenFluid
    {
        public Flowing(FluidProperties properties)
        {
            super(properties);
        }

        public boolean isStill(FluidState state)
        {
            return false;
        }

        public int getLevel(FluidState state)
        {
            return state.get(LEVEL);
        }

        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder)
        {
            super.appendProperties(builder.add(LEVEL));
        }
    }

    public static class Source extends MoltenFluid
    {
        public Source(FluidProperties properties)
        {
            super(properties);
        }

        public boolean isStill(FluidState state)
        {
            return true;
        }

        public int getLevel(FluidState state)
        {
            return 8;
        }
    }
}
