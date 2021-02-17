package net.dries007.tfc.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerConnectionEvents {

    Event<PlayerConnectionEvents> JOIN = EventFactory.createArrayBacked(PlayerConnectionEvents.class, listeners -> (manager, player) -> {
        for (PlayerConnectionEvents listener : listeners)
        {
            listener.onPlayerStatusChange(manager, player);
        }
    });

    Event<PlayerConnectionEvents> LEAVE = EventFactory.createArrayBacked(PlayerConnectionEvents.class, listeners -> (manager, player) -> {
        for (PlayerConnectionEvents listener : listeners)
        {
            listener.onPlayerStatusChange(manager, player);
        }
    });

    void onPlayerStatusChange(PlayerManager manager, ServerPlayerEntity player);
}
