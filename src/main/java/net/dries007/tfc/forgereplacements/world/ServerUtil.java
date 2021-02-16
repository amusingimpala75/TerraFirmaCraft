/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.forgereplacements.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerUtil {

    private static MinecraftServer currentServer;

    public static void registerCacher()
    {
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> currentServer = null);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
    }

    public static MinecraftServer getCurrentServer()
    {
        return currentServer;
    }
}
