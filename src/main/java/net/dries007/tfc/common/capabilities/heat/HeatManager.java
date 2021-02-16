/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.dries007.tfc.util.Helpers;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import org.jetbrains.annotations.Nullable;

public class HeatManager extends DataManager<HeatDefinition> implements IdentifiableResourceReloadListener
{
    public static final IndirectHashCollection<Item, HeatDefinition> CACHE = new IndirectHashCollection<>(HeatDefinition::getValidItems);
    public static final HeatManager INSTANCE = new HeatManager();

    @Nullable
    public static HeatDefinition get(ItemStack stack)
    {
        for (HeatDefinition def : CACHE.getAll(stack.getItem()))
        {
            if (def.isValid(stack))
            {
                return def;
            }
        }
        return null;
    }

    public static void reload()
    {
        CACHE.reload(INSTANCE.getValues());
    }

    private HeatManager()
    {
        super(new GsonBuilder().create(), "item_heats", "item heat", true);
    }

    @Override
    protected HeatDefinition read(Identifier id, JsonObject obj)
    {
        return new HeatDefinition(id, obj);
    }

    @Override
    public Identifier getFabricId() {
        return Helpers.identifier("data_listener/heat_manager");
    }
}
