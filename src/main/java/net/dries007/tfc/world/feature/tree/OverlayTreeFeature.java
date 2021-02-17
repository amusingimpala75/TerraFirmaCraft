/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;

import net.dries007.tfc.util.Helpers;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import com.mojang.serialization.Codec;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OverlayTreeFeature extends TreeFeature<OverlayTreeConfig>
{
    public OverlayTreeFeature(Codec<OverlayTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess worldIn, ChunkGenerator generator, Random random, BlockPos pos, OverlayTreeConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().set(pos);
        final StructureManager manager = TreeHelpers.getTemplateManager(worldIn);
        final StructurePlacementData settings = TreeHelpers.getPlacementSettings(chunkPos, random);
        final Structure structureBase = TreeHelpers.getOrBlank(manager, config.base);
        final Structure structureOverlay = TreeHelpers.getOrBlank(manager, config.overlay);

        if (!isValidLocation(worldIn, mutablePos) || !isAreaClear(worldIn, mutablePos, config.radius, 3))
        {
            return false;
        }

        config.trunk.ifPresent(trunk -> {
            final int height = TreeHelpers.placeTrunk(worldIn, mutablePos, random, settings, trunk);
            mutablePos.move(0, height, 0);
        });

        TreeHelpers.placeTemplate(structureBase, settings, worldIn, mutablePos.subtract(TreeHelpers.transformCenter(structureBase.getSize(), settings)));
        settings.addProcessor(new BlockRotStructureProcessor(config.overlayIntegrity));
        TreeHelpers.placeTemplate(structureOverlay, settings, worldIn, mutablePos.subtract(TreeHelpers.transformCenter(structureOverlay.getSize(), settings)));
        return true;
    }
}