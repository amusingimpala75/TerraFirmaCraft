/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.world;

import net.dries007.tfc.util.calendar.Calendars;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
        throw new IllegalStateException("Mixin inject failed! This should have never been called!!");
    }

    @Inject(method = "wakeSleepingPlayers", at=@At("TAIL"))
    public void inject$calendarUpdate(CallbackInfo ci)
    {
        long currentDayTime = this.getTimeOfDay();
        if (Calendars.SERVER.getCalendarDayTime() != currentDayTime)
        {
            long jump = Calendars.SERVER.setTimeFromDayTime(currentDayTime);
            /* todo: requires food overrides
            // Consume food/water on all online players accordingly (EXHAUSTION_MULTIPLIER is here to de-compensate)
            event.getEntity().getEntityWorld().getPlayers()
                 .forEach(player -> player.addExhaustion(FoodStatsTFC.PASSIVE_EXHAUSTION * jump / FoodStatsTFC.EXHAUSTION_MULTIPLIER * (float) ConfigTFC.GENERAL.foodPassiveExhaustionMultiplier));
            */
        }
    }
}
