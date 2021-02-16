/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.dries007.tfc.util.Helpers;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;

import net.dries007.tfc.util.data.DataManager;

public class MetalManager extends DataManager<Metal> implements IdentifiableResourceReloadListener
{
    public static final MetalManager INSTANCE = new MetalManager();

    private MetalManager()
    {
        super(new GsonBuilder().create(), "metals", "metal", false);
    }

    @Override
    protected Metal read(Identifier id, JsonObject obj)
    {
        return new Metal(id, obj);
    }

    @Override
    public Identifier getFabricId() {
        return Helpers.identifier("data_listener/metal_manager");
    }
}