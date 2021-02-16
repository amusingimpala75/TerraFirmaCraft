/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.event;

import net.dries007.tfc.fabric.event.DebugRenderCallback;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class DebugHudEventMixin extends DrawableHelper {

    @Inject(method = "getLeftText", at = @At("RETURN"))
    public void inject$addCustomLeftText(CallbackInfoReturnable<List<String>> ci)
    {
        DebugRenderCallback.LEFT.invoker().addStrings(ci.getReturnValue());
    }

    @Inject(method = "getRightText", at=@At("RETURN"))
    public void inject$addCustomRightText(CallbackInfoReturnable<List<String>> ci)
    {
        DebugRenderCallback.RIGHT.invoker().addStrings(ci.getReturnValue());
    }
}
