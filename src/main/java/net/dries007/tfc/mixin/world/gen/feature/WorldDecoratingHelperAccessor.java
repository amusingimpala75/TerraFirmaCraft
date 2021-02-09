/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world.gen.feature;

import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.DecoratorContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Adds accessors for the internal fields on {@link DecoratorContext} as that class is needlessly limiting, and our climate decorator needs access to chunk data.
 */
@Mixin(DecoratorContext.class)
public interface WorldDecoratingHelperAccessor
{
    @Accessor(value = "world")
    StructureWorldAccess accessor$getLevel();

    @Accessor(value = "generator")
    ChunkGenerator accessor$getGenerator();
}
