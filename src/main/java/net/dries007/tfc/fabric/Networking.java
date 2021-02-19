/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.fabric;

import net.dries007.tfc.network.CalendarUpdatePacket;
import net.dries007.tfc.network.ChunkUnwatchPacket;
import net.dries007.tfc.network.ChunkWatchPacket;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class Networking {

    public static final Identifier WATCH_PACKET_ID = Helpers.identifier("networking/chunk_watch_packet");
    public static final Identifier UNWATCH_PACKET_ID = Helpers.identifier("networking/chunk_unwatch_packet");
    public static final Identifier SWITCH_TAB_PACKET_ID = Helpers.identifier("networking/switch_tab_packet");
    public static final Identifier CALENDAR_UPDATE_PACKET_ID = Helpers.identifier("networking/calendar_update_packet");

    public static void register() {
        ServerPlayConnectionEvents.INIT.register((handlerMain, serverMain) ->
            //Switch tab packet
            ServerPlayNetworking.registerGlobalReceiver(SWITCH_TAB_PACKET_ID, (server, player, handler, packet, sender) -> {
                SwitchInventoryTabPacket switchTab = new SwitchInventoryTabPacket(packet);
                server.execute(() -> switchTab.handle(player));
            })
        );

    }

    public static void clientRegister() {
        ClientPlayConnectionEvents.INIT.register((handler1, client1) -> {
            //Watch chunk packet
            ClientPlayNetworking.registerReceiver(WATCH_PACKET_ID, (client, handler, packet, sender) -> {
                ChunkWatchPacket watch = (new ChunkWatchPacket(packet));
                client.execute(watch::handle);
            });

            //Unwatch chunk packet
            ClientPlayNetworking.registerReceiver(UNWATCH_PACKET_ID, (client, handler, packet, sender) -> {
                ChunkUnwatchPacket unwatch = (new ChunkUnwatchPacket(packet));
                client.execute(unwatch::handle);
            });

            //Update Calendar
            ClientPlayNetworking.registerReceiver(CALENDAR_UPDATE_PACKET_ID, (client, handler, packet, sender) -> {
                CalendarUpdatePacket update = (new CalendarUpdatePacket(packet));
                client.execute(update::handle);
            });
        });
    }

}
