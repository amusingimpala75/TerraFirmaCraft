/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.Supplier;

import net.dries007.tfc.util.Helpers;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.registry.Registry;
import net.minecraft.recipe.RecipeSerializer;

@SuppressWarnings("unchecked")
public class TFCRecipeSerializers
{
    // Block Recipes

    public static final SimpleBlockRecipe.Serializer<CollapseRecipe> COLLAPSE = (SimpleBlockRecipe.Serializer<CollapseRecipe>) register("collapse", () -> new SimpleBlockRecipe.Serializer<CollapseRecipe>(CollapseRecipe::new));
    public static final SimpleBlockRecipe.Serializer<LandslideRecipe> LANDSLIDE = (SimpleBlockRecipe.Serializer<LandslideRecipe>) register("landslide", () -> new SimpleBlockRecipe.Serializer<LandslideRecipe>(LandslideRecipe::new));

    // Delegate Recipe Types

    public static final DelegatingRecipe.Serializer<CraftingInventory, DamageInputsCraftingRecipe> DAMAGE_INPUTS_CRAFTING = (DelegatingRecipe.Serializer<CraftingInventory, DamageInputsCraftingRecipe>) register("damage_inputs_crafting", () -> new DelegatingRecipe.Serializer<>(DamageInputsCraftingRecipe::new));

    private static RecipeSerializer<?> register(String name, Supplier<RecipeSerializer<?>> factory)
    {
        return Registry.register(Registry.RECIPE_SERIALIZER, Helpers.identifier(name), factory.get());
    }

    public static void register() {}
}