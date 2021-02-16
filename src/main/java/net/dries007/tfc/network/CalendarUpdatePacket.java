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

import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;

public class CalendarUpdatePacket
{
    private final Calendar instance;

    public CalendarUpdatePacket(Calendar instance)
    {
        this.instance = instance;
    }

    public CalendarUpdatePacket(PacketByteBuf buffer)
    {
        instance = new Calendar();
        instance.read(buffer);
    }

    void encode(PacketByteBuf buffer)
    {
        instance.write(buffer);
    }

    //void handle(Supplier<NetworkEvent.Context> context)
    public void handle()
    {
        //context.get().enqueueWork(() -> Calendars.CLIENT.reset(instance));
        //context.get().setPacketHandled(true);
        Calendars.CLIENT.reset(instance);
    }

    public void send(ServerPlayerEntity e)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        encode(buf);
        ServerPlayNetworking.send(e, Networking.CALENDAR_UPDATE_PACKET_ID, buf);
    }
}