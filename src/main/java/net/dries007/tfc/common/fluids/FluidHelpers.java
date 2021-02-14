/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraftforge.event.ForgeEventFactory;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.mixin.fluid.FlowingFluidAccessor;

public class FluidHelpers
{
    public static boolean isSame(FluidState state, Fluid expected)
    {
        return state.getFluid().matchesType(expected);
    }

    public static boolean canMixFluids(Fluid left, Fluid right)
    {
        return canMixFluids(left) && canMixFluids(right);
    }

    /**
     * If the two fluids are allowed to be considered for mixing
     * This is more lenient than {@link FlowableFluid#matchesType(Fluid)} but must assume a few things:
     * - only works with fluids which are an instance of {@link FlowableFluid} (should be all fluids)
     * - assumes that fluid source / flowing handling works like vanilla
     * - fluids must be added to the {@link net.dries007.tfc.common.TFCTags.Fluids#MIXABLE} tag
     *
     * @param fluid A fluid
     * @return true if the fluid should use fluid mixing mechanics
     */
    public static boolean canMixFluids(Fluid fluid)
    {
        return fluid instanceof FlowableFluid && TFCTags.Fluids.MIXABLE.contains(fluid);
    }

    /**
     * This is the main logic from @link FlowableFluid#getUpdatedState(WorldView, BlockPos, BlockState), but modified to support fluid mixing, and extracted into a static helper method to allow other {@link FlowableFluid} classes (namely, vanilla water) to be modified.
     *
     * @param self               The fluid instance this would've been called upon
     * @param worldIn            The world
     * @param pos                A position
     * @param blockStateIn       The current block state at that position
     * @param canConvertToSource The result of {@code self.canConvertToSource()} as it's protected
     * @param dropOff            The result of {@code self.getDropOff(worldIn)} as it's protected
     * @return The fluid state that should exist at that position
     * see FlowableFluid#getUpdatedState(WorldView, BlockPos, BlockState)
     */
    public static FluidState getNewFluidWithMixing(FlowableFluid self, WorldView worldIn, BlockPos pos, BlockState blockStateIn, boolean canConvertToSource, int dropOff)
    {
        int maxAdjacentFluidAmount = 0; // The maximum height of fluids flowing into this block from the sides
        FlowableFluid maxAdjacentFluid = self;

        int adjacentSourceBlocks = 0; // How many adjacent source blocks that could convert this into a source block
        Object2IntArrayMap<FlowableFluid> adjacentSourceBlocksByFluid = new Object2IntArrayMap<>(2);

        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            BlockPos offsetPos = pos.offset(direction);
            BlockState offsetState = worldIn.getBlockState(offsetPos);
            FluidState offsetFluid = offsetState.getFluidState();

            // Look for adjacent fluids that are the same, for purposes of flow into this fluid
            // canPassThroughWall detects if a fluid state has a barrier - e.g. via a stair edge - that would prevent it from connecting to the current block.
            if (offsetFluid.getFluid() instanceof FlowableFluid && ((FlowingFluidAccessor) self).invoke$canPassThroughWall(direction, worldIn, pos, blockStateIn, offsetPos, offsetState))
            {
                if (offsetFluid.isStill() && ForgeEventFactory.canCreateFluidSource(worldIn, offsetPos, offsetState, canConvertToSource))
                {
                    adjacentSourceBlocks++;
                    adjacentSourceBlocksByFluid.mergeInt((FlowableFluid) offsetFluid.getFluid(), 1, Integer::sum);
                }
                // Also record the maximum adjacent fluid, breaking ties with the current fluid
                if (offsetFluid.getLevel() > maxAdjacentFluidAmount || (offsetFluid.getLevel() == maxAdjacentFluidAmount && self.matchesType(offsetFluid.getFluid())))
                {
                    maxAdjacentFluidAmount = offsetFluid.getLevel();
                    maxAdjacentFluid = (FlowableFluid) offsetFluid.getFluid();
                }
            }
        }

        if (adjacentSourceBlocks >= 2)
        {
            // There are two adjacent source blocks (although potentially of different kinds) - check if the below block is also a source, or if it's a solid block
            // If true, then this block should be converted to a source block as well
            BlockState belowState = worldIn.getBlockState(pos.down());
            FluidState belowFluid = belowState.getFluidState();

            if (belowFluid.isStill() && belowFluid.getFluid() instanceof FlowableFluid && adjacentSourceBlocksByFluid.getInt(belowFluid.getFluid()) >= 2)
            {
                // Try and create a source block of the same type as the below
                return ((FlowableFluid) belowFluid.getFluid()).getStill(false);
            }
            else if (belowState.getMaterial().isSolid())
            {
                // This could potentially form fluid blocks from multiple blocks. It can only override the current source if there's three adjacent equal sources, or form a source if this is the same as three adjacent sources
                FlowableFluid maximumAdjacentSourceFluid = self;
                int maximumAdjacentSourceBlocks = 0;
                for (Object2IntMap.Entry<FlowableFluid> entry : adjacentSourceBlocksByFluid.object2IntEntrySet())
                {
                    if (entry.getIntValue() > maximumAdjacentSourceBlocks || entry.getKey() == self)
                    {
                        maximumAdjacentSourceBlocks = entry.getIntValue();
                        maximumAdjacentSourceFluid = entry.getKey();
                    }
                }

                // Three adjacent (if not same), or two (if same)
                if (maximumAdjacentSourceBlocks >= 3 || (maximumAdjacentSourceBlocks >= 2 && self.matchesType(maximumAdjacentSourceFluid)))
                {
                    return maximumAdjacentSourceFluid.getStill(false);
                }
            }
        }

        // At this point, we haven't been able to convert into a source block
        // Check the block above to see if that is flowing downwards into this one (creating a level 8, falling, flowing block)
        // A fluid above, flowing down, will always replace an existing fluid block
        BlockPos abovePos = pos.up();
        BlockState aboveState = worldIn.getBlockState(abovePos);
        FluidState aboveFluid = aboveState.getFluidState();
        if (!aboveFluid.isEmpty() && aboveFluid.getFluid() instanceof FlowableFluid && ((FlowingFluidAccessor) self).invoke$canPassThroughWall(Direction.UP, worldIn, pos, blockStateIn, abovePos, aboveState))
        {
            return ((FlowableFluid) aboveFluid.getFluid()).getFlowing(8, true);
        }
        else
        {
            // Nothing above that can flow downwards, so use the highest adjacent fluid amount, after subtracting the drop off (1 for water, 2 for lava)
            int selfFluidAmount = maxAdjacentFluidAmount - dropOff;
            if (selfFluidAmount <= 0)
            {
                // No flow amount into this block
                return Fluids.EMPTY.getDefaultState();
            }
            // Cause the maximum adjacent fluid to flow into this block
            return maxAdjacentFluid.getFlowing(selfFluidAmount, false);
        }
    }

    /**
     * Copy pasta from @link net.minecraft.block.FluidBlock#receiveNeighborFluids(World, BlockPos, BlockState)
     * Used for lava-like or lava-logged blocks that need to share the same behavior.
     * This uses vanilla mechanics to determine the block as TFC will modify them via event later
     *
     * @return true if the block was modified
     */
    public static boolean reactLavaWithNeighbors(World worldIn, BlockPos pos, FluidState fluidStateAt)
    {
        boolean soulSoilBelow = worldIn.getBlockState(pos.down()).isOf(Blocks.SOUL_SOIL);
        for (Direction direction : Direction.values())
        {
            if (direction != Direction.DOWN)
            {
                BlockPos adjacentPos = pos.offset(direction);
                if (worldIn.getFluidState(adjacentPos).isIn(FluidTags.WATER))
                {
                    worldIn.setBlockState(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, Blocks.OBSIDIAN.getDefaultState()));
                    worldIn.syncWorldEvent(1501, pos, 0);
                    return true;
                }

                if (soulSoilBelow && worldIn.getBlockState(adjacentPos).isOf(Blocks.BLUE_ICE))
                {
                    worldIn.setBlockState(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, Blocks.BASALT.getDefaultState()));
                    worldIn.syncWorldEvent(1501, pos, 0);
                    return true;
                }
            }
        }
        return false;
    }
}
