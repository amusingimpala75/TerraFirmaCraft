/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.forgereplacements.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemUtils {
    public static void giveItemToPlayer(PlayerEntity player, @NotNull ItemStack stack, int preferredSlot)
    {
        if (stack.isEmpty()) return;

        //IItemHandler inventory = new PlayerMainInvWrapper(player.inventory);
        PlayerInventory inventory = player.inventory;
        World world = player.world;

        ItemStack remainder = stack;
        if (preferredSlot >= 0 && preferredSlot < inventory.size())
        {
            inventory.insertStack(preferredSlot, stack);
        }
        if (!remainder.isEmpty())
        {
            inventory.insertStack(remainder);
        }

        if (remainder.isEmpty() || remainder.getCount() != stack.getCount())
        {
            world.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }

        if (!remainder.isEmpty() && !world.isClient)
        {
            ItemEntity entityitem = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
            entityitem.setPickupDelay(40);
            entityitem.setVelocity(entityitem.getVelocity().multiply(0, 1, 0));

            world.spawnEntity(entityitem);
        }
    }
}
