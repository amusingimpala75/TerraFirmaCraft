/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.world;

import net.dries007.tfc.fabric.duck.WorldDuck;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(WorldView.class)
public interface WorldViewMixin extends WorldDuck {

    @Shadow
    @Deprecated
    boolean isRegionLoaded(BlockPos min, BlockPos max);

    @Override
    default boolean inject$isAreaLoaded(BlockPos pos, int radius) {
        return this.isRegionLoaded(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));
    }
}
