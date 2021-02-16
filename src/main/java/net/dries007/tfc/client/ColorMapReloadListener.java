/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.io.IOException;
import java.util.function.Consumer;

import net.dries007.tfc.util.Helpers;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class ColorMapReloadListener extends SinglePreparationResourceReloadListener<int[]> implements IdentifiableResourceReloadListener
{
    private final Identifier textureLocation;
    private final Consumer<int[]> consumer;
    private final String name;

    public ColorMapReloadListener(Consumer<int[]> consumer, Identifier textureLocation, String name)
    {
        this.textureLocation = textureLocation;
        this.consumer = consumer;
        this.name = name;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected int[] prepare(ResourceManager resourceManagerIn, Profiler profilerIn)
    {
        try
        {
            return RawTextureDataLoader.loadRawTextureData(resourceManagerIn, textureLocation);
        }
        catch (IOException ioexception)
        {
            throw new IllegalStateException("Failed to load colormap", ioexception);
        }
    }

    @Override
    protected void apply(int[] objectIn, ResourceManager resourceManagerIn, Profiler profilerIn)
    {
        consumer.accept(objectIn);
    }

    @Override
    public Identifier getFabricId() {
        return Helpers.identifier(name);
    }
}