/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;

import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.config.TFCConfig;
import org.jetbrains.annotations.Nullable;

/**
 * Almost all methods in here are adapted from
 * {@link net.minecraft.block.ChorusFlowerBlock}
 */
public abstract class KelpTreeFlowerBlock extends Block implements IFluidLoggable
{
    public static final IntProperty AGE = Properties.AGE_5;
    private static final VoxelShape SHAPE = Block.createCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    private final Supplier<? extends Block> bodyBlock;

    public static KelpTreeFlowerBlock create(AbstractBlock.Settings builder, Supplier<? extends Block> plant, FluidProperty fluid)
    {
        return new KelpTreeFlowerBlock(builder, plant)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    protected KelpTreeFlowerBlock(AbstractBlock.Settings builder, Supplier<? extends Block> bodyBlock)
    {
        super(builder);
        this.bodyBlock = bodyBlock;
        setDefaultState(getStateManager().getDefaultState().with(AGE, 0).with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.canPlaceAt(worldIn, pos))
        {
            worldIn.breakBlock(pos, true);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        return state.get(AGE) < 5;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        KelpTreeBlock body = (KelpTreeBlock) getBodyBlock().get();
        Fluid fluid = state.get(getFluidProperty()).getFluid();

        BlockPos abovePos = pos.up();
        if (isEmptyWaterBlock(worldIn, abovePos) && abovePos.getY() < 256 && TerraFirmaCraft.getConfig().serverConfig.blocks.plants.getPlantGrowthChance() > random.nextDouble())
        {
            int i = state.get(AGE);
            if (i < 5 )//&& ForgeHooks.onCropsGrowPre(worldIn, abovePos, state, true))
            {
                boolean shouldPlaceNewBody = false;
                boolean foundGroundFurtherDown = false;
                BlockState belowState = worldIn.getBlockState(pos.down());
                Block belowBlock = belowState.getBlock();
                if (belowBlock.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                {
                    shouldPlaceNewBody = true;
                }
                else if (belowBlock == body)
                {
                    int j = 1;

                    for (int k = 0; k < 4; ++k)
                    {
                        Block belowBlockOffset = worldIn.getBlockState(pos.down(j + 1)).getBlock();
                        if (belowBlockOffset != body)
                        {
                            if (belowBlockOffset.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                            {
                                foundGroundFurtherDown = true;
                            }
                            break;
                        }

                        ++j;
                    }

                    if (j < 2 || j <= random.nextInt(foundGroundFurtherDown ? 5 : 4))
                    {
                        shouldPlaceNewBody = true;
                    }
                }
                else if (isEmptyWaterBlock(worldIn, pos.down()))
                {
                    shouldPlaceNewBody = true;
                }

                if (shouldPlaceNewBody && allNeighborsEmpty(worldIn, abovePos, null) && isEmptyWaterBlock(worldIn, pos.up(2)))
                {
                    setBodyBlockWithFluid(worldIn, pos, fluid);
                    this.placeGrownFlower(worldIn, abovePos, i);
                }
                else if (i < 4)
                {
                    int l = random.nextInt(4);
                    if (foundGroundFurtherDown)
                    {
                        ++l;
                    }
                    boolean foundValidGrowthSpace = false;
                    for (int i1 = 0; i1 < l; ++i1)
                    {
                        Direction direction = Direction.Type.HORIZONTAL.random(random);
                        BlockPos relativePos = pos.offset(direction);
                        if (isEmptyWaterBlock(worldIn, relativePos) && isEmptyWaterBlock(worldIn, relativePos.down()) && allNeighborsEmpty(worldIn, relativePos, direction.getOpposite()))
                        {
                            this.placeGrownFlower(worldIn, relativePos, i + 1);
                            foundValidGrowthSpace = true;
                        }
                    }
                    if (foundValidGrowthSpace)
                    {
                        setBodyBlockWithFluid(worldIn, pos, fluid);
                    }
                    else
                    {
                        this.placeDeadFlower(worldIn, pos);
                    }
                }
                else
                {
                    this.placeDeadFlower(worldIn, pos);
                }
                //ForgeHooks.onCropsGrowPost(worldIn, pos, state);
            }
        }
    }

    private void placeGrownFlower(World worldIn, BlockPos pos, int age)
    {
        Fluid fluid = worldIn.getFluidState(pos).getFluid();
        worldIn.setBlockState(pos, getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor(fluid)).with(AGE, age), 2);
        worldIn.syncWorldEvent(1033, pos, 0);
    }

    private void placeDeadFlower(World worldIn, BlockPos pos)
    {
        Fluid fluid = worldIn.getFluidState(pos).getFluid();
        worldIn.setBlockState(pos, getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor(fluid)).with(AGE, 5), 2);
        worldIn.syncWorldEvent(1034, pos, 0);
    }

    private static boolean allNeighborsEmpty(WorldView worldIn, BlockPos pos, @Nullable Direction excludingSide)
    {
        for (Direction direction : Direction.Type.HORIZONTAL)
        {
            if (direction != excludingSide && !isEmptyWaterBlock(worldIn, pos.offset(direction)))
            {
                return false;
            }
        }
        return true;
    }


    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        final Fluid containedFluid = stateIn.get(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            worldIn.getFluidTickScheduler().schedule(currentPos, containedFluid, containedFluid.getTickRate(worldIn));
        }
        if (facing != Direction.UP && !stateIn.canPlaceAt(worldIn, currentPos))
        {
            worldIn.getBlockTickScheduler().schedule(currentPos, this, 1);
            return Blocks.AIR.getDefaultState();
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        KelpTreeBlock body = (KelpTreeBlock) getBodyBlock().get();

        BlockState blockstate = worldIn.getBlockState(pos.down());
        if (blockstate.getBlock() != body && !blockstate.isIn(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
        {
            if (!isEmptyWaterBlock(worldIn, pos.down()))
            {
                return false;
            }
            else
            {
                boolean isValid = false;
                for (Direction direction : Direction.Type.HORIZONTAL)
                {
                    BlockState relativeState = worldIn.getBlockState(pos.offset(direction));
                    if (relativeState.isOf(body))
                    {
                        if (isValid)
                        {
                            return false;
                        }

                        isValid = true;
                    }
                    else if (!isEmptyWaterBlock(worldIn, pos.offset(direction)))
                    {
                        return false;
                    }
                }

                return isValid;
            }
        }
        else
        {
            return true;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(getFluidProperty(), AGE);
    }

    public void generatePlant(WorldAccess worldIn, BlockPos pos, Random rand, int maxHorizontalDistance, Fluid fluid)
    {
        if (!getFluidProperty().canContain(fluid))
            return;
        setBodyBlockWithFluid(worldIn, pos, fluid);
        growTreeRecursive(worldIn, pos, rand, pos, maxHorizontalDistance, 0, fluid);
    }

    public void growTreeRecursive(WorldAccess worldIn, BlockPos branchPos, Random rand, BlockPos originalBranchPos, int maxHorizontalDistance, int iterations, Fluid fluid)
    {
        int i = rand.nextInt(5) + 1;
        if (iterations == 0)
        {
            ++i;
        }
        for (int j = 0; j < i; ++j)
        {
            BlockPos blockpos = branchPos.up(j + 1);
            if (!allNeighborsEmpty(worldIn, blockpos, null))
            {
                return;
            }
            setBodyBlockWithFluid(worldIn, blockpos, fluid);
            setBodyBlockWithFluid(worldIn, blockpos.down(), fluid);
        }

        boolean willContinue = false;
        if (iterations < 4)
        {
            int branchAttempts = rand.nextInt(4);
            if (iterations == 0)
            {
                ++branchAttempts;
            }

            for (int k = 0; k < branchAttempts; ++k)
            {
                Direction direction = Direction.Type.HORIZONTAL.random(rand);
                BlockPos aboveRelativePos = branchPos.up(i).offset(direction);
                if (Math.abs(aboveRelativePos.getX() - originalBranchPos.getX()) < maxHorizontalDistance && Math.abs(aboveRelativePos.getZ() - originalBranchPos.getZ()) < maxHorizontalDistance && isEmptyWaterBlock(worldIn, aboveRelativePos) && isEmptyWaterBlock(worldIn, aboveRelativePos.down()) && allNeighborsEmpty(worldIn, aboveRelativePos, direction.getOpposite()))
                {
                    willContinue = true;
                    setBodyBlockWithFluid(worldIn, aboveRelativePos, fluid);
                    setBodyBlockWithFluid(worldIn, aboveRelativePos.offset(direction.getOpposite()), fluid);
                    growTreeRecursive(worldIn, aboveRelativePos, rand, originalBranchPos, maxHorizontalDistance, iterations + 1, fluid);
                }
            }
        }
        if (!willContinue)
        {
            worldIn.setBlockState(branchPos.up(i), getDefaultState().with(AGE, rand.nextInt(10) == 1 ? 3 : 5).with(getFluidProperty(), getFluidProperty().keyFor(fluid)), 2);
        }
    }

    public static boolean isEmptyWaterBlock(WorldView worldIn, BlockPos pos)
    {
        return worldIn.isWater(pos) && !(worldIn.getBlockState(pos).getBlock() instanceof IFluidLoggable);
    }

    private void setBodyBlockWithFluid(WorldAccess worldIn, BlockPos pos, Fluid fluid)
    {
        BlockState state = getBodyStateWithFluid(worldIn, pos, fluid);
        worldIn.setBlockState(pos, state, 2);
    }

    private BlockState getBodyStateWithFluid(WorldAccess worldIn, BlockPos pos, Fluid fluid)
    {
        KelpTreeBlock plant = (KelpTreeBlock) getBodyBlock().get();
        return plant.getStateForPlacement(worldIn, pos).with(getFluidProperty(), getFluidProperty().keyFor(fluid));
    }

    private Supplier<? extends Block> getBodyBlock()
    {
        return bodyBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }
}
