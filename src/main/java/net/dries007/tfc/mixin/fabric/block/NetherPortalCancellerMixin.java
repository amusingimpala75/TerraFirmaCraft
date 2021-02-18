/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.block;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.fabric.world.ClientWorldAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.AreaHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaHelper.class)
public class NetherPortalCancellerMixin {
    @Shadow
    @Final
    private WorldAccess world;

    @Inject(method = "createPortal", at=@At("HEAD"), cancellable = true)
    public void inject$possiblyPreventActivation(CallbackInfo ci)
    {
        if (!TerraFirmaCraft.getConfig().serverConfig.general.enableNetherPortals)
        {
            ci.cancel();
            if (this.world instanceof ClientWorld)
            {
                ClientPlayerEntity player = ((ClientWorldAccessor)world).accessor$getClient().player;
                if (player != null)
                {
                    player.sendMessage(new TranslatableText("tfc.nether_portal.disabled"), false);
                }
            }
        }
    }
}
