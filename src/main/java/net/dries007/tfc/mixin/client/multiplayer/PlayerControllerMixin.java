/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.multiplayer;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import net.dries007.tfc.util.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This is a fake event injection as Forge's RightClickBlock does not fire in the correct place
 *
 * @see InteractionManager
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class PlayerControllerMixin
{
    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), require = 2, cancellable = true)
    private void inject$useItemOn(ClientPlayerEntity player, ClientWorld worldIn, Hand handIn, BlockHitResult resultIn, CallbackInfoReturnable<ActionResult> cir)
    {
        final ItemStack stack = player.getStackInHand(handIn);
        final int startCount = stack.getCount();
        final ItemUsageContext itemContext = new ItemUsageContext(player, handIn, resultIn);
        InteractionManager.onItemUse(stack, itemContext).ifPresent(result -> {
            if (player.isCreative())
            {
                stack.setCount(startCount);
            }
            cir.setReturnValue(result);
        });
    }
}
