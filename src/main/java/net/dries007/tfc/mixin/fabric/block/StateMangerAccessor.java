package net.dries007.tfc.mixin.fabric.block;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StateManager.Builder.class)
public interface StateMangerAccessor {
    @Accessor("namedProperties")
    Map<String, Property<?>> accessor$getNamedProperties();
}
