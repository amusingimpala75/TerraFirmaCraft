package net.dries007.tfc.mixin.fabric;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerManager.class)
public interface PlayerMangerAccessor {
    @Accessor("server")
    MinecraftServer accessor$getServer();
}
