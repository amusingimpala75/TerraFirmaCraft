/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

public class RivuletFeature extends Feature<SingleStateFeatureConfig>
{
    public RivuletFeature(Codec<SingleStateFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, SingleStateFeatureConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockBox box = new BlockBox(chunkPos.getStartX() - 14, chunkPos.getStartZ() - 14, chunkPos.getStartX() + 14, chunkPos.getStartZ() + 14); // Leeway so we can check outside this box

        // Basic pathfinding down the slope
        final Set<BlockPos> chosen = new HashSet<>();
        final LinkedList<BlockPos> branches = new LinkedList<>();

        final BlockPos startPos = new BlockPos(pos.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()), pos.getZ());
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        branches.add(startPos);

        boolean mainBranch = true;

        while (!branches.isEmpty())
        {
            // Explore this branch
            BlockPos branchStartPos = branches.removeFirst();
            int maxLength = 5 + rand.nextInt(8);
            if (mainBranch)
            {
                mainBranch = false;
                maxLength += 12;
            }

            BlockPos lastPos = branchStartPos;

            for (int i = 0; i < maxLength; i++)
            {
                chosen.add(branchStartPos);

                Direction chosenDirection = null;
                int chosenHeight = 0;
                int possibleDirections = 0;
                for (Direction direction : Direction.Type.HORIZONTAL)
                {
                    // Check positions in each direction
                    mutablePos.set(lastPos, direction);
                    if (!box.contains(mutablePos))
                    {
                        continue; // Outside of the bounding box, skip!
                    }
                    final int height = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ());
                    if (height <= mutablePos.getY())
                    {
                        // Check that the block below is solid
                        mutablePos.setY(height - 1);
                        if (world.getBlockState(mutablePos).getMaterial().isSolid())
                        {
                            // Valid next position
                            possibleDirections++;
                            if (chosenDirection == null || rand.nextInt(possibleDirections) == 0)
                            {
                                chosenHeight = height;
                                chosenDirection = direction;
                            }
                        }
                    }
                }

                if (possibleDirections == 0)
                {
                    // Nowhere to go from here
                    break;
                }
                else
                {
                    if (possibleDirections > 1 && rand.nextInt(3) == 0)
                    {
                        // Opportunity for a new branch
                        branches.add(lastPos);
                    }

                    // One direction, so proceed
                    mutablePos.set(lastPos, chosenDirection).setY(chosenHeight);
                    if (!chosen.contains(mutablePos))
                    {
                        lastPos = mutablePos.toImmutable();
                        chosen.add(lastPos);
                    }
                    else
                    {
                        // Already reached this position, skip
                        break;
                    }
                }
            }
        }

        if (!chosen.isEmpty())
        {
            // We have found a path and can generate a magma rivulet
            final BlockState air = Blocks.AIR.getDefaultState();
            for (BlockPos chosenPos : chosen)
            {
                // At each position, break the top block, and replace two blocks underneath with magma
                // The recorded positions are one above the topmost block due to how getHeight works
                mutablePos.set(chosenPos, Direction.DOWN);
                setBlockState(world, mutablePos, air);
                mutablePos.move(Direction.DOWN);
                setBlockState(world, mutablePos, config.state);
                mutablePos.move(Direction.DOWN);
                setBlockState(world, mutablePos, config.state);
            }
            return true;
        }
        return false;
    }
}
