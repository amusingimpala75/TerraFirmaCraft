package net.dries007.tfc.fabric;

import net.dries007.tfc.network.ChunkUnwatchPacket;
import net.dries007.tfc.network.ChunkWatchPacket;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class Networking {

    public static final Identifier CHANNEL_ID = Helpers.identifier("network_channel");

    public static void register() {
        //Switch tab packet
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_ID, (server, player, handler, packet, sender) -> new SwitchInventoryTabPacket(packet).handle(player));
    }

    public static void clientRegister() {
        //Watch chunk packet
        ClientPlayNetworking.registerReceiver(CHANNEL_ID, (client, handler, packet, sender) -> (new ChunkWatchPacket(packet)).handle());

        //Unwatch chunk packet
        ClientPlayNetworking.registerReceiver(CHANNEL_ID, (client, handler, packet, sender) -> (new ChunkUnwatchPacket(packet)).handle());
    }

}
