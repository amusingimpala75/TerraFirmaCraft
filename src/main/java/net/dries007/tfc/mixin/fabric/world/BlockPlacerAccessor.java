/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.world;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockPlacerType.class)
public interface BlockPlacerAccessor {
    @Invoker("register")
    static <P extends BlockPlacer> BlockPlacerType<P> call$register(String id, Codec<P> codec) {
        throw new IllegalStateException("Neva called!");
    }
}
