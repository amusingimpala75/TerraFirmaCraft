/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.dries007.tfc.fabric.cca.Components;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.rock.IFallableBlock;
import net.dries007.tfc.mixin.entity.item.FallingBlockEntityAccessor;

/**
 * A falling block entity that has a bit more oomph - it destroys blocks underneath it rather than hovering or popping off.
 */
@SuppressWarnings("EntityConstructor")
public class TFCFallingBlockEntity extends FallingBlockEntity
{
    public static boolean canFallThrough(BlockView world, BlockPos pos)
    {
        return canFallThrough(world, pos, world.getBlockState(pos));
    }

    public static boolean canFallThrough(BlockView world, BlockPos pos, BlockState state)
    {
        return !state.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    private final boolean dontSetBlock;
    private boolean failedBreakCheck;

    public TFCFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world)
    {
        super(entityType, world);

        failedBreakCheck = false;
        dontSetBlock = false;
    }

    public TFCFallingBlockEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState)
    {
        super(worldIn, x, y, z, fallingBlockState);

        failedBreakCheck = false;
        dontSetBlock = false;
    }

    @Override
    public void tick()
    {
        final BlockState fallingBlockState = getBlockState();
        if (fallingBlockState.isAir())
        {
            remove();
        }
        else
        {
            Block block = fallingBlockState.getBlock();
            if (timeFalling++ == 0)
            {
                // First tick, replace the existing block
                BlockPos blockpos = getBlockPos();
                if (world.getBlockState(blockpos).getBlock().is(block))
                {
                    world.removeBlock(blockpos, false);
                }
                else if (!world.isClient)
                {
                    remove();
                    return;
                }
            }

            if (!hasNoGravity())
            {
                setVelocity(getVelocity().add(0.0D, -0.04D, 0.0D));
            }

            move(MovementType.SELF, getVelocity());

            if (!world.isClient)
            {
                BlockPos posAt = getBlockPos();
                if (!onGround)
                {
                    failedBreakCheck = false;
                    if ((timeFalling > 100 && (posAt.getY() < 1 || posAt.getY() > 256)) || timeFalling > 600)
                    {
                        if (dropItem && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                        {
                            dropItem(block);
                        }
                        remove();
                    }
                }
                else
                {
                    // On ground
                    if (!failedBreakCheck)
                    {
                        if (!world.isAir(posAt) && canFallThrough(world, posAt, world.getBlockState(posAt)))
                        {
                            world.breakBlock(posAt, true);
                            failedBreakCheck = true;
                            return;
                        }
                        else if (!world.isAir(posAt.down()) && canFallThrough(world, posAt.down(), world.getBlockState(posAt.down())))
                        {
                            world.breakBlock(posAt.down(), true);
                            failedBreakCheck = true;
                            return;
                        }
                    }

                    BlockState hitBlockState = world.getBlockState(posAt);
                    setVelocity(getVelocity().multiply(0.7D, -0.5D, 0.7D));

                    if (hitBlockState.getBlock() != Blocks.MOVING_PISTON)
                    {
                        remove();
                        if (!dontSetBlock)
                        {
                            if (hitBlockState.canReplace(new AutomaticItemPlacementContext(this.world, posAt, Direction.DOWN, ItemStack.EMPTY, Direction.UP)) && fallingBlockState.canPlaceAt(this.world, posAt) && !FallingBlock.canFallThrough(this.world.getBlockState(posAt.down())))
                            {
                                if (fallingBlockState.contains(Properties.WATERLOGGED) && this.world.getFluidState(posAt).getFluid() == Fluids.WATER)
                                {
                                    ((FallingBlockEntityAccessor) this).accessor$setBlockState(fallingBlockState.with(Properties.WATERLOGGED, Boolean.TRUE));
                                }

                                if (world.setBlockState(posAt, fallingBlockState))
                                {
                                    if (block instanceof FallingBlock)
                                    {
                                        ((FallingBlock) block).onLanding(this.world, posAt, fallingBlockState, hitBlockState, this);
                                    }

                                    if (TFCTags.Blocks.CAN_LANDSLIDE.contains(fallingBlockState.getBlock()))
                                    {
                                        Components.WORLD_TRACKING.maybeGet(world).get().addLandslidePos(posAt);//.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(posAt));
                                    }

                                    // Sets the tile entity if it exists
                                    if (blockEntityData != null && fallingBlockState.getBlock().hasBlockEntity())
                                    {
                                        BlockEntity tileEntity = world.getBlockEntity(posAt);
                                        if (tileEntity != null)
                                        {
                                            CompoundTag tileEntityData = tileEntity.toTag(new CompoundTag());
                                            for (String dataKey : tileEntityData.getKeys())
                                            {
                                                Tag dataElement = tileEntityData.get(dataKey);
                                                if (!"x".equals(dataKey) && !"y".equals(dataKey) && !"z".equals(dataKey) && dataElement != null)
                                                {
                                                    tileEntityData.put(dataKey, dataElement.copy());
                                                }
                                            }
                                            tileEntity.fromTag(fallingBlockState, tileEntityData);
                                            tileEntity.markDirty();
                                        }
                                    }
                                }
                                else if (dropItem && world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                                {
                                    dropItem(block);
                                }
                            }
                            else if (dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                            {
                                dropItem(block);
                            }
                        }

                        if (block instanceof IFallableBlock)
                        {
                            ((IFallableBlock) block).onceFinishedFalling(this.world, posAt, this);
                        }
                    }
                }
            }

            setVelocity(getVelocity().multiply(0.98D));
        }
    }
}