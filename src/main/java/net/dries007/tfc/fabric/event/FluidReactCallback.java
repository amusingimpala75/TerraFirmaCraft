/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public interface FluidReactCallback {

    Event<FluidReactCallback> EVENT = EventFactory.createArrayBacked(FluidReactCallback.class, listeners -> originalState -> {
        AtomicReference<BlockState> state = new AtomicReference<>(originalState);
        for (FluidReactCallback listener : listeners)
        {
            listener.modifyBlockState(state.get()).ifPresent(state::set);
        }
        return Optional.of(state.get());
    });

    Optional<BlockState> modifyBlockState(BlockState original);
}
