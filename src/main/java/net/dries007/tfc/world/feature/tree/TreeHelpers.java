/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;

import net.dries007.tfc.mixin.world.gen.feature.template.TemplateAccessor;

/**
 * Helpers class for working with tree generation
 * Includes utilities for managing rotations, mirrors, and templates
 */
public final class TreeHelpers
{
    private static final BlockRotation[] ROTATION_VALUES = BlockRotation.values();
    private static final BlockMirror[] MIRROR_VALUES = BlockMirror.values();

    /**
     * A variant of {@link net.minecraft.structure.Structure#place(ServerWorldAccess, BlockPos, StructurePlacementData, Random)} that is much simpler and faster for use in tree generation
     * Allows replacing leaves and air blocks
     */
    public static void placeTemplate(Structure template, StructurePlacementData placementIn, WorldAccess worldIn, BlockPos pos)
    {
        List<Structure.StructureBlockInfo> transformedBlockInfos = placementIn.getRandomBlockInfos(((TemplateAccessor) template).accessor$getPalettes(), pos).getAll();
        BlockBox boundingBox = placementIn.getBoundingBox();
        for (Structure.StructureBlockInfo blockInfo : Structure.process(worldIn, pos, pos, placementIn, transformedBlockInfos))
        {
            BlockPos posAt = blockInfo.pos;
            if (boundingBox == null || boundingBox.contains(posAt))
            {
                BlockState stateAt = worldIn.getBlockState(posAt);
                //if (stateAt.isAir(worldIn, posAt) || BlockTags.LEAVES.contains(stateAt.getBlock()))
                if (stateAt.isAir() || BlockTags.LEAVES.contains(stateAt.getBlock()))
                {
                    // No world, can't rotate with world context
                    BlockState stateReplace = blockInfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
                    worldIn.setBlockState(posAt, stateReplace, 2);
                }
            }
        }
    }

    /**
     * Place a trunk from a trunk config
     *
     * @param pos The center position of the trunk
     * @return The height of the trunk placed
     */
    public static int placeTrunk(StructureWorldAccess world, BlockPos pos, Random random, StructurePlacementData settings, TrunkConfig trunk)
    {
        final int height = trunk.getHeight(random);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = (1 - trunk.width) / 2; x <= trunk.width / 2; x++)
        {
            for (int z = (1 - trunk.width) / 2; z <= trunk.width / 2; z++)
            {
                for (int y = 0; y < height; y++)
                {
                    mutablePos.set(x, y, z);
                    transformMutable(mutablePos, settings.getMirror(), settings.getRotation());
                    mutablePos.move(pos);
                    world.setBlockState(mutablePos, trunk.state, 3);
                }
            }
        }
        return height;
    }

    public static StructureManager getTemplateManager(StructureWorldAccess worldIn)
    {
        return worldIn.toServerWorld().getServer().getStructureManager();
    }

    /**
     * Constructs a placement settings instance useful for tree generation
     * Applies a random rotation and mirror
     * Has a bounding box constrained by the given chunk and surrounding chunks to not cause cascading chunk loading
     */
    public static StructurePlacementData getPlacementSettings(ChunkPos chunkPos, Random random)
    {
        return new StructurePlacementData()
            .setBoundingBox(new BlockBox(chunkPos.getStartX() - 16, 0, chunkPos.getStartZ() - 16, chunkPos.getEndX() + 16, 256, chunkPos.getStartZ() + 16))
            .setRandom(random)
            .addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS)
            .setRotation(randomRotation(random))
            .setMirror(randomMirror(random));
    }

    /**
     * Given a width of a specific parity, return the transformation of the chosen center position.
     */
    public static BlockPos transformCenter(BlockPos size, StructurePlacementData settings)
    {
        return transform(new BlockPos((size.getX() - 1) / 2, 0, (size.getZ() - 1) / 2), settings.getMirror(), settings.getRotation());
    }

    /**
     * {@link Structure#transformAround(Vec3d, BlockMirror, BlockRotation, BlockPos)} but simplified
     */
    public static BlockPos transform(BlockPos pos, BlockMirror mirrorIn, BlockRotation rotationIn)
    {
        int posX = pos.getX();
        int posZ = pos.getZ();
        boolean mirror = true;
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                posZ = -posZ;
                break;
            case FRONT_BACK:
                posX = -posX;
                break;
            default:
                mirror = false;
        }
        switch (rotationIn)
        {
            case COUNTERCLOCKWISE_90:
                return new BlockPos(posZ, pos.getY(), -posX);
            case CLOCKWISE_90:
                return new BlockPos(-posZ, pos.getY(), posX);
            case CLOCKWISE_180:
                return new BlockPos(-posX, pos.getY(), -posZ);
            default:
                return mirror ? new BlockPos(posX, pos.getY(), posZ) : pos;
        }
    }

    /**
     * {@link Structure#transformAround(Vec3d, BlockMirror, BlockRotation, BlockPos)} but simplified, and works with mutable positions
     */
    public static void transformMutable(BlockPos.Mutable pos, BlockMirror mirrorIn, BlockRotation rotationIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                pos.setZ(-pos.getZ());
                break;
            case FRONT_BACK:
                pos.setX(-pos.getX());
                break;
        }
        switch (rotationIn)
        {
            case COUNTERCLOCKWISE_90:
                pos.set(pos.getZ(), pos.getY(), -pos.getX());
                break;
            case CLOCKWISE_90:
                pos.set(-pos.getZ(), pos.getY(), pos.getX());
                break;
            case CLOCKWISE_180:
                pos.set(-pos.getX(), pos.getY(), -pos.getZ());
                break;
        }
    }

    private static BlockRotation randomRotation(Random random)
    {
        return ROTATION_VALUES[random.nextInt(ROTATION_VALUES.length)];
    }

    private static BlockMirror randomMirror(Random random)
    {
        return MIRROR_VALUES[random.nextInt(MIRROR_VALUES.length)];
    }
}
