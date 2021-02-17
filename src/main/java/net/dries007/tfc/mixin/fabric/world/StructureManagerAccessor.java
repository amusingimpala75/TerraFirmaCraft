package net.dries007.tfc.mixin.fabric.world;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StructureManager.class)
public interface StructureManagerAccessor {
    @Accessor("structures")
    Map<Identifier, Structure> accessor$getStructures();
}
