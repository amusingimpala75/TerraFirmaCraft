/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.world;

import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.client.world.ClientWorld;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.world.TFCChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.Properties.class)
public class ClientWorldInfoMixin
{
    @Shadow @Final private boolean flatWorld;

    /**
     * Override horizon height (where the fog color changes from sky to black/dark, as in vanilla it's hardcoded to the sea level
     */
    @Inject(method = "getSkyDarknessHeight", at = @At("HEAD"), cancellable = true)
    private void inject$getHorizonHeight(CallbackInfoReturnable<Double> cir)
    {
        if (TerraFirmaCraft.getConfig().clientConfig.assumeTFCWorld)
        {
            cir.setReturnValue(this.flatWorld ? 0 : (double) TFCChunkGenerator.SEA_LEVEL);
        }
    }
}
