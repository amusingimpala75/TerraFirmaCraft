/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.fabric.Networking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

import net.dries007.tfc.world.chunkdata.ChunkDataCache;

/**
 * Sent from server -> client, clears the client side chunk data cache when a chunk is unwatched
 */
public class ChunkUnwatchPacket
{
    private final int chunkX;
    private final int chunkZ;

    public ChunkUnwatchPacket(ChunkPos pos)
    {
        this.chunkX = pos.x;
        this.chunkZ = pos.z;
    }

    public ChunkUnwatchPacket(PacketByteBuf buffer)
    {
        this.chunkX = buffer.readVarInt();
        this.chunkZ = buffer.readVarInt();
    }

    void encode(PacketByteBuf buffer)
    {
        buffer.writeVarInt(chunkX);
        buffer.writeVarInt(chunkZ);
    }

    /*void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> ChunkDataCache.CLIENT.remove(new ChunkPos(chunkX, chunkZ)));
        context.get().setPacketHandled(true);
    }*/
    public void handle()
    {
        ChunkDataCache.CLIENT.remove(new ChunkPos(chunkX, chunkZ));
    }

    public void send(ServerPlayerEntity e)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        encode(buf);
        ServerPlayNetworking.send(e, Networking.UNWATCH_PACKET_ID, buf);
    }
}