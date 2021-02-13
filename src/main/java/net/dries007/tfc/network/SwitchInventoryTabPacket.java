/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import net.dries007.tfc.common.container.TFCContainerProviders;

public class SwitchInventoryTabPacket
{
    private final Type type;

    public SwitchInventoryTabPacket(Type type)
    {
        this.type = type;
    }

    SwitchInventoryTabPacket(PacketByteBuf buffer)
    {
        this.type = Type.VALUES[buffer.readByte()];
    }

    void encode(PacketByteBuf buffer)
    {
        buffer.writeByte(type.ordinal());
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().setPacketHandled(true);
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null)
            {
                player.closeScreenHandler();
                if (type == Type.INVENTORY)
                {
                    player.currentScreenHandler = player.playerScreenHandler;
                }
                else if (type == Type.CALENDAR)
                {
                    NetworkHooks.openGui(player, TFCContainerProviders.CALENDAR);
                }
                else if (type == Type.NUTRITION)
                {
                    NetworkHooks.openGui(player, TFCContainerProviders.NUTRITION);
                }
                else if (type == Type.CLIMATE)
                {
                    NetworkHooks.openGui(player, TFCContainerProviders.CLIMATE);
                }
                else
                {
                    throw new IllegalStateException("Unknown type?");
                }
            }
        });
    }

    public enum Type
    {
        INVENTORY, CALENDAR, NUTRITION, CLIMATE;

        private static final Type[] VALUES = values();
    }
}