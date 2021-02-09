/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Client side methods for proxy use
 */
public class ClientHelpers
{
    @Nullable
    public static World getWorld()
    {
        return MinecraftClient.getInstance().world;
    }
}