package net.dries007.tfc.mixin.fabric.event;

import net.dries007.tfc.fabric.event.PlayerConnectionEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at=@At("TAIL"))
    public void inject$join(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci)
    {
        PlayerConnectionEvents.JOIN.invoker().onPlayerStatusChange((PlayerManager) (Object) this, player);
    }

    @Inject(method = "remove", at=@At("TAIL"))
    public void inject$leave(ServerPlayerEntity player, CallbackInfo ci)
    {
        PlayerConnectionEvents.LEAVE.invoker().onPlayerStatusChange((PlayerManager) (Object) this, player);
    }
}
