/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.MixingFluid;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This modifies vanilla fluid behavior (enabled via the "mixing" fluid tag), in order to play nicely with {@link MixingFluid}.
 */
@Mixin(FlowableFluid.class)
public abstract class FlowingFluidMixin extends Fluid
{
    @Shadow
    protected abstract boolean isInfinite();

    @Shadow
    protected abstract int getLevelDecreasePerBlock(WorldView worldIn);

    @Inject(method = "getUpdatedState", at = @At("HEAD"), cancellable = true)
    private void inject$getNewLiquid(WorldView worldIn, BlockPos pos, BlockState blockStateIn, CallbackInfoReturnable<FluidState> cir)
    {
        if (FluidHelpers.canMixFluids(this))
        {
            cir.setReturnValue(FluidHelpers.getNewFluidWithMixing((FlowableFluid) (Object) this, worldIn, pos, blockStateIn, isInfinite(), getLevelDecreasePerBlock(worldIn)));
        }
    }
}
