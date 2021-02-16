/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;

public interface DebugRenderCallback
{
    Event<DebugEntry> LEFT = EventFactory.createArrayBacked(DebugEntry.class, listeners -> entries -> {
        for (DebugEntry listener : listeners)
        {
            listener.addStrings(entries);
        }
    });

    Event<DebugEntry> RIGHT = EventFactory.createArrayBacked(DebugEntry.class, listeners -> entries -> {
        for (DebugEntry listener : listeners)
        {
            listener.addStrings(entries);
        }
    });

    @FunctionalInterface
    interface DebugEntry
    {
        void addStrings(List<String> entries);
    }
}
