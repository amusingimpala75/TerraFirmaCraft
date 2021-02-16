/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.fabric.Networking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import net.dries007.tfc.common.container.TFCContainerProviders;

public class SwitchInventoryTabPacket
{
    private final Type type;

    public SwitchInventoryTabPacket(Type type)
    {
        this.type = type;
    }

    public SwitchInventoryTabPacket(PacketByteBuf buffer)
    {
        this.type = Type.VALUES[buffer.readByte()];
    }

    void encode(PacketByteBuf buffer)
    {
        buffer.writeByte(type.ordinal());
    }

    //void handle(Supplier<NetworkEvent.Context> context)
    public void handle(ServerPlayerEntity target)
    {
        //context.get().setPacketHandled(true);
        //context.get().enqueueWork(() -> {
        if (target != null)
            {
                target.closeScreenHandler();
                if (type == Type.INVENTORY)
                {
                    target.currentScreenHandler = target.playerScreenHandler;
                }
                else if (type == Type.CALENDAR)
                {
                    target.openHandledScreen(TFCContainerProviders.CALENDAR);
                }
                else if (type == Type.NUTRITION)
                {
                    target.openHandledScreen(TFCContainerProviders.NUTRITION);
                }
                else if (type == Type.CLIMATE)
                {
                    target.openHandledScreen(TFCContainerProviders.CLIMATE);
                }
                else
                {
                    throw new IllegalStateException("Unknown type?");
                }
            }
        //});
    }

    public void send()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        encode(buf);
        ClientPlayNetworking.send(Networking.SWITCH_TAB_PACKET_ID, buf);
    }

    public enum Type
    {
        INVENTORY, CALENDAR, NUTRITION, CLIMATE;

        private static final Type[] VALUES = values();
    }
}