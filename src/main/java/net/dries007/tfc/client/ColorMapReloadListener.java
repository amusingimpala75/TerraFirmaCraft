/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.io.IOException;
import java.util.function.Consumer;

import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class ColorMapReloadListener extends SinglePreparationResourceReloadListener<int[]>
{
    private final Identifier textureLocation;
    private final Consumer<int[]> consumer;

    public ColorMapReloadListener(Consumer<int[]> consumer, Identifier textureLocation)
    {
        this.textureLocation = textureLocation;
        this.consumer = consumer;
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
}