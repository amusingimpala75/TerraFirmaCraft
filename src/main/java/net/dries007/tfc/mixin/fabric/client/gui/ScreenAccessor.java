package net.dries007.tfc.mixin.fabric.client.gui;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface ScreenAccessor {
    @Accessor("x")
    int accessor$x();

    @Accessor("y")
    int accessor$y();
}
