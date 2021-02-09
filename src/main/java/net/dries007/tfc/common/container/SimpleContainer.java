/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

/**
 * A very simple container implementation.
 * Used for gui's that have no internal inventory, or no TE they need to access
 */
public class SimpleContainer extends ScreenHandler
{
    public SimpleContainer(ScreenHandlerType<?> type, int windowId)
    {
        super(type, windowId);
    }

    public SimpleContainer(ScreenHandlerType<?> type, int windowId, PlayerInventory playerInv)
    {
        super(type, windowId);
        addPlayerInventorySlots(playerInv);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index)
    {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack())
        {
            ItemStack stack = slot.getStack();
            stackCopy = stack.copy();

            if (index < 27)
            {
                if (!this.insertItem(stack, 27, 36, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if (!this.insertItem(stack, 0, 27, false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty())
            {
                slot.setStack(ItemStack.EMPTY);
            }
            else
            {
                slot.markDirty();
            }

            if (stack.getCount() == stackCopy.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack stackTake = slot.onTakeItem(player, stack);
            if (index == 0)
            {
                player.dropItem(stackTake, false);
            }
        }

        return stackCopy;
    }

    @Override
    public boolean canUse(PlayerEntity playerIn)
    {
        return true;
    }

    protected void addPlayerInventorySlots(PlayerInventory playerInv)
    {
        addPlayerInventorySlots(playerInv, 0);
    }

    protected void addPlayerInventorySlots(PlayerInventory playerInv, int yOffset)
    {
        // Add Player Inventory Slots
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yOffset));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlot(new Slot(playerInv, k, 8 + k * 18, 142 + yOffset));
        }
    }
}