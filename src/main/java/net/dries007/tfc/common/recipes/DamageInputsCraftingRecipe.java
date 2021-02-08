/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;

import net.dries007.tfc.util.Helpers;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class DamageInputsCraftingRecipe extends DelegatingRecipe<CraftingInventory> implements CraftingRecipe
{
    protected DamageInputsCraftingRecipe(Identifier id, Recipe<CraftingInventory> recipe)
    {
        super(id, recipe);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); ++i)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.isDamageableItem())
            {
                Helpers.damageCraftingItem(stack, 1);
            }
            else if (stack.hasContainerItem())
            {
                items.set(i, stack.getContainerItem());
            }
        }
        return items;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.DAMAGE_INPUTS_CRAFTING;
    }
}
