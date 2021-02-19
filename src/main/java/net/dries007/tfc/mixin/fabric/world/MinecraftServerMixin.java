/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.world;

import net.dries007.tfc.FabricEventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "setupSpawn", at=@At("HEAD"), cancellable = true)
    private static void inject$modifySpawn(ServerWorld world, ServerWorldProperties serverWorldProperties, boolean bonusChest, boolean debugWorld, boolean bl, CallbackInfo ci)
    {
        FabricEventHandler.modifySpawn(world, serverWorldProperties, ci);
    }
}
