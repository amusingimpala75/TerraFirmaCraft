package net.dries007.tfc.mixin.fabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {
    @Invoker("getSpreadChance")
    int call$getSpreadChance(BlockState state);

    @Invoker("getBurnChance")
    int call$getBurnChance(BlockState state);
}
