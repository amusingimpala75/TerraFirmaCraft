/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * This is a definition (reloaded via {@link HeatManager}) of a heat that is applied to an item stack.
 */
public class HeatDefinition
{
    private final Identifier id;
    private final Ingredient ingredient;
    public final float heatCapactiy;
    public final float forgingTemp;
    public final float weldingTemp;

    public HeatDefinition(Identifier id, JsonObject obj)
    {
        this.id = id;
        this.heatCapactiy = JsonHelper.getFloat(obj, "heat_capacity");
        this.forgingTemp = JsonHelper.getFloat(obj, "forging_temperature", 0);
        this.weldingTemp = JsonHelper.getFloat(obj, "welding_temperature", 0);
        this.ingredient = Ingredient.fromJson(JsonHelper.getObject(obj, "ingredient"));
    }

    public Identifier getId()
    {
        return id;
    }

    /**
     * Creates a new instance of the capability defined by this object.
     */
    public boolean isValid(ItemStack stack)
    {
        return ingredient.test(stack);
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getMatchingStacksClient()).map(ItemStack::getItem).collect(Collectors.toSet());
    }
}
