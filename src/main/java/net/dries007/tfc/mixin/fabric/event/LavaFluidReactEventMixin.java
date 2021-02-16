/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.event;

import net.dries007.tfc.fabric.event.FluidReactCallback;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LavaFluid.class)
public class LavaFluidReactEventMixin {

    /*@ModifyArg(
        method = "flow",
        at=@At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/ModifiableWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
        ),
        index = 1
    )
    private BlockState inject$modifyReaction(BlockState original)
    {
        Optional<BlockState> op = FluidReactCallback.EVENT.invoker().modifyBlockState(original);
        return op.orElse(original);
    }*/

    @Inject(method = "flow", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", shift = At.Shift.AFTER))
    public void inject$modifyReaction(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci)
    {
        Optional<BlockState> op = FluidReactCallback.EVENT.invoker().modifyBlockState(world.getBlockState(pos));
        op.ifPresent(blockState -> world.setBlockState(pos, blockState, 3));
    }
}
