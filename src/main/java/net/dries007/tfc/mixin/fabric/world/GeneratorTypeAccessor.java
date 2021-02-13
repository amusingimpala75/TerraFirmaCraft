package net.dries007.tfc.mixin.fabric.world;

import net.minecraft.client.world.GeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GeneratorType.class)
public interface GeneratorTypeAccessor {
    @Accessor("VALUES")
    static List<GeneratorType> accessor$getVALUES()
    {
        throw new IllegalStateException("Neva called!");
    }
}
