/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world.gen.feature.template;

import java.util.List;

import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.world.feature.tree.TreeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Structure.class)
public interface TemplateAccessor
{
    /**
     * Used by @link TreeFeature#placeTemplateInWorld(Structure, PlacementSettings, IWorld, BlockPos) for a optimized implementation
     */
    @Accessor("blockInfoLists")
    List<Structure.PalettedBlockInfoList> accessor$getPalettes();
}
