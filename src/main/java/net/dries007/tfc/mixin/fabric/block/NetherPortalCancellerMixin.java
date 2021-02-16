/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.block;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import net.minecraft.world.dimension.AreaHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaHelper.class)
public class NetherPortalCancellerMixin {
    @Inject(method = "createPortal", at=@At("HEAD"), cancellable = true)
    public void inject$possiblyPreventActivation(CallbackInfo ci)
    {
        if (!TerraFirmaCraft.getConfig().serverConfig.general.enableNetherPortals)
        {
            ci.cancel();
        }
    }
}
