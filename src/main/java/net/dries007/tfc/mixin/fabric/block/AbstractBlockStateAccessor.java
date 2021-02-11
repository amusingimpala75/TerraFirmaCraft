package net.dries007.tfc.mixin.fabric.block;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.AbstractBlockState.class)
public interface AbstractBlockStateAccessor {
    @Accessor("isAir")
    boolean accessor$getIsAir();
}
