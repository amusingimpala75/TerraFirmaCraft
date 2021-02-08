/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.dries007.tfc.util.Helpers;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class TFCRecipeTypes
{
    public static final RecipeType<CollapseRecipe> COLLAPSE = register("collapse");
    public static final RecipeType<LandslideRecipe> LANDSLIDE = register("landslide");

    static <T extends Recipe<?>> RecipeType<T> register(final String string) {
        return Registry.register(Registry.RECIPE_TYPE, Helpers.identifier(string), new RecipeType<T>() {
            public String toString() {
                return string;
            }
        });
    }
}