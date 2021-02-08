/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.item.crafting;

import java.util.Map;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor
{
    /**
     * For more performant querying of recipes, this gets all recipes of a type. Used by recipe caches.
     */
    @Invoker("getAllOfType")
    <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> call$byType(RecipeType<T> recipeTypeIn);
}
