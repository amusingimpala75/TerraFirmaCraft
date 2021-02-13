package net.dries007.tfc.mixin.fabric.world;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockStateProviderType.class)
public interface BlockStateProviderAccessor {
    @Invoker("register")
    static <P extends BlockStateProvider> BlockStateProviderType<P> call$register(String id, Codec<P> codec) {
        throw new IllegalStateException("Neva called!");
    }
}
