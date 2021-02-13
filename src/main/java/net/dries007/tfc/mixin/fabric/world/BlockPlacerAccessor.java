package net.dries007.tfc.mixin.fabric.world;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.BlockPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockPlacerType.class)
public interface BlockPlacerAccessor {
    @Invoker("register")
    static <P extends BlockPlacer> BlockPlacerType<P> call$register(String id, Codec<P> codec) {
        throw new IllegalStateException("Neva called!");
    }
}
