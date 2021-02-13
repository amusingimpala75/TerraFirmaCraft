/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;

public class MetalItem
{
    private final Identifier id;
    private final Ingredient ingredient;
    private final Metal metal;
    private final int amount;

    public MetalItem(Identifier id, JsonObject json)
    {
        this.id = id;
        ingredient = CraftingHelper.getIngredient(JsonHelper.getObject(json, "ingredient"));
        Identifier metalId = new Identifier(JsonHelper.getString(json, "metal"));
        metal = MetalManager.INSTANCE.get(metalId);
        if (metal == null)
        {
            throw new JsonSyntaxException("Invalid metal specified: " + metalId.toString());
        }
        amount = JsonHelper.getInt(json, "amount");
    }

    public Identifier getId()
    {
        return id;
    }

    public Metal getMetal()
    {
        return metal;
    }

    public int getAmount()
    {
        return amount;
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getMatchingStacksClient()).map(ItemStack::getItem).collect(Collectors.toSet());
    }

    public boolean isValid(ItemStack stack)
    {
        return this.ingredient.test(stack);
    }
}