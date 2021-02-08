/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.item.ItemStack;

/**
 * A RecipeWrapper for single item stacks.
 */
public class ItemStackRecipeWrapper implements IInventoryNoop
{
    protected ItemStack stack;

    public ItemStackRecipeWrapper(ItemStack stack)
    {
        this.stack = stack;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    public int size()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return stack.isEmpty();
    }

    @Override
    public ItemStack getStack(int index)
    {
        return stack;
    }

    @Override
    public ItemStack removeStack(int index, int count)
    {
        return stack.split(count);
    }

    @Override
    public ItemStack removeStack(int index)
    {
        return stack.split(stack.getCount());
    }

    @Override
    public void setStack(int index, ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void clear()
    {
        stack = ItemStack.EMPTY;
    }
}