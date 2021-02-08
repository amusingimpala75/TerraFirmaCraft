/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public interface IDelegatingRecipe<C extends Inventory> extends Recipe<C>
{
    Recipe<C> getInternal();

    @Override
    default boolean matches(C inv, World worldIn)
    {
        return getInternal().matches(inv, worldIn);
    }

    @Override
    default ItemStack craft(C inv)
    {
        return getInternal().craft(inv);
    }

    @Override
    default boolean fits(int width, int height)
    {
        return getInternal().fits(width, height);
    }

    @Override
    default ItemStack getOutput()
    {
        return getInternal().getOutput();
    }

    @Override
    default DefaultedList<ItemStack> getRemainingStacks(C inv)
    {
        return getInternal().getRemainingStacks(inv);
    }

    @Override
    default DefaultedList<Ingredient> getPreviewInputs()
    {
        return getInternal().getPreviewInputs();
    }

    @Override
    default boolean isIgnoredInRecipeBook()
    {
        return getInternal().isIgnoredInRecipeBook();
    }

    @Override
    default String getGroup()
    {
        return getInternal().getGroup();
    }

    @Override
    default ItemStack getRecipeKindIcon()
    {
        return getInternal().getRecipeKindIcon();
    }
}
