/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.server.management;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

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
@Mixin(ServerPlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin
{
    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), cancellable = true, require = 2)
    private void inject$useItemOn(ServerPlayerEntity playerIn, World worldIn, ItemStack stackIn, Hand handIn, BlockHitResult blockRaytraceResultIn, CallbackInfoReturnable<ActionResult> cir)
    {
        final int startCount = stackIn.getCount();
        final ItemUsageContext itemContext = new ItemUsageContext(playerIn, handIn, blockRaytraceResultIn);
        InteractionManager.onItemUse(stackIn, itemContext).ifPresent(result -> {
            if (playerIn.isCreative())
            {
                stackIn.setCount(startCount);
            }
            if (result.isAccepted())
            {
                Criteria.ITEM_USED_ON_BLOCK.test(playerIn, blockRaytraceResultIn.getBlockPos(), stackIn);
            }
            cir.setReturnValue(result);
        });
    }
}
