/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.MinecraftClient;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftMixin extends ReentrantThreadExecutor<Runnable>
{
    private MinecraftMixin(String name)
    {
        super(name);
    }

    /**
     * This is a hack to remove the "Experimental Settings" screen which will pop up every time you generate or load a TFC world.
     *
     * Fixed by https://github.com/MinecraftForge/MinecraftForge/pull/7275
     */
    /*@ModifyVariable(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient$WorldLoadAction;NONE:Lnet/minecraft/client/MinecraftClient$WorldLoadAction;", ordinal = 0), ordinal = 3, index = 13, name = "bl2")
    private boolean modify$doLoadLevel$flag1(boolean flag1)
    {
        if (TerraFirmaCraft.getConfig().clientConfig.ignoreExperimentalWorldGenWarning)
        {
            TerraFirmaCraft.LOGGER.warn("Experimental world gen... dragons or some such.. blah blah.");
            return false;
        }
        return flag1;
    }*/

    @ModifyVariable(
        method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V",
        at=@At(value = "STORE"),
        ordinal = 2
    )
    public boolean modify$theseAreNotTheDragonsYoureLookingFor(boolean bl2)
    {
        if (TerraFirmaCraft.getConfig().clientConfig.ignoreExperimentalWorldGenWarning)
        {
            TerraFirmaCraft.LOGGER.warn("Experimental world gen... dragons or some such.. blah blah.");
            return false;
        }
        return bl2;
    }
}
