/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.function.BiFunction;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.recipe.RecipeSerializer;


import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

/**
 * A recipe that delegates to an internal recipe, held in the "recipe" field.
 * The internal recipe must obviously be compatible but we have no way of assuring that, so we rely on users to not screw this up
 * This is more powerful than creating a recipe type for every combination of modifier (e.g. damage inputs, apply food, etc.)
 */
public abstract class DelegatingRecipe<C extends Inventory> implements IDelegatingRecipe<C>
{
    public static final Identifier DELEGATE = Helpers.identifier("delegate");

    private final Identifier id;
    private final Recipe<C> recipe;

    protected DelegatingRecipe(Identifier id, Recipe<C> recipe)
    {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public Recipe<C> getInternal()
    {
        return recipe;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    protected static class Serializer<C extends Inventory, R extends DelegatingRecipe<C>> implements RecipeSerializer<R>
    {
        private final BiFunction<Identifier, Recipe<C>, R> factory;

        protected Serializer(BiFunction<Identifier, Recipe<C>, R> factory)
        {
            this.factory = factory;
        }

        @Override
        @SuppressWarnings("unchecked")
        public R read(Identifier recipeId, JsonObject json)
        {
            Recipe<?> internal = RecipeManager.deserialize(DELEGATE, JsonHelper.getObject(json, "recipe"));
            return factory.apply(recipeId, (Recipe<C>) internal);
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public R read(Identifier recipeId, PacketByteBuf buffer)
        {
            Recipe<?> internal = SynchronizeRecipesS2CPacket.readRecipe(buffer);
            return factory.apply(recipeId, (Recipe<C>) internal);
        }

        @Override
        public void write(PacketByteBuf buffer, R recipe)
        {
            SynchronizeRecipesS2CPacket.writeRecipe(recipe.getInternal(), buffer);
        }
    }
}
