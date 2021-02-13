/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collections;
import java.util.function.Supplier;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * This is a fake {@link BlockItem} copy pasta for a vanilla item that we want to behave like a block item for a specific block.
 */
public class BlockItemPlacement implements InteractionManager.OnItemUseAction
{
    /**
     * Copy pasta from @link BlockItem#with(BlockState, Property, String)
     */
    private static <T extends Comparable<T>> BlockState updateState(BlockState state, Property<T> property, String value)
    {
        return property.parse(value).map(valueIn -> state.with(property, valueIn)).orElse(state);
    }

    private final Supplier<? extends Item> item;
    private final Supplier<? extends Block> block;

    public BlockItemPlacement(Supplier<? extends Item> item, Supplier<? extends Block> block)
    {
        this.item = item;
        this.block = block;
    }

    public Iterable<Item> getItems()
    {
        return Collections.singleton(item.get());
    }

    public Item getItem()
    {
        return item.get();
    }

    /**
     * Copy paste from @link ItemStack#useOn(ItemUseContext)
     */
    @Override
    public ActionResult onItemUse(ItemStack stack, ItemUsageContext context)
    {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.abilities.allowModifyWorld)
        {
            return ActionResult.PASS;
        }
        else
        {
            Item item = getItem();
            ActionResult result = place(new ItemPlacementContext(context));
            if (player != null && result.shouldSwingHand())
            {
                player.incrementStat(Stats.USED.getOrCreateStat(item));
            }
            return result;
        }
    }

    /**
     * Copy pasta from {@link net.minecraft.item.BlockItem#place(ItemPlacementContext)}
     */
    public ActionResult place(ItemPlacementContext context)
    {
        if (!context.canPlace())
        {
            return ActionResult.FAIL;
        }
        else
        {
            BlockState placementState = getPlacementState(context);
            if (placementState == null)
            {
                return ActionResult.FAIL;
            }
            else if (!this.placeBlock(context, placementState))
            {
                return ActionResult.FAIL;
            }
            else
            {
                BlockPos pos = context.getBlockPos();
                World world = context.getWorld();
                PlayerEntity player = context.getPlayer();
                ItemStack stack = context.getStack();
                BlockState placedState = world.getBlockState(pos);
                Block placedBlock = placedState.getBlock();
                if (placedBlock == placementState.getBlock())
                {
                    placedState = updateBlockStateFromTag(pos, world, stack, placedState);
                    BlockItem.writeTagToBlockEntity(world, player, pos, stack);
                    placedBlock.onPlaced(world, pos, placedState, player, stack);
                    if (player instanceof ServerPlayerEntity)
                    {
                        Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
                    }
                }

                BlockSoundGroup placementSound = placedState.getSoundGroup(/*world, pos, player*/);
                world.playSound(player, pos, placedState.getSoundGroup(/*world, pos, player*/).getPlaceSound(), SoundCategory.BLOCKS, (placementSound.getVolume() + 1.0F) / 2.0F, placementSound.getPitch() * 0.8F);
                if (player == null || !player.abilities.creativeMode)
                {
                    stack.decrement(1);
                }

                return ActionResult.success(world.isClient);
            }
        }
    }

    @Nullable
    protected BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState placementState = block.get().getPlacementState(context);
        return placementState != null && canPlace(context, placementState) ? placementState : null;
    }

    protected boolean placeBlock(ItemPlacementContext context, BlockState state)
    {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 11);
    }

    protected boolean canPlace(ItemPlacementContext context, BlockState stateToPlace)
    {
        PlayerEntity player = context.getPlayer();
        ShapeContext selectionContext = player == null ? ShapeContext.absent() : ShapeContext.of(player);
        return (stateToPlace.canPlaceAt(context.getWorld(), context.getBlockPos())) && context.getWorld().canPlace(stateToPlace, context.getBlockPos(), selectionContext);
    }

    /**
     * Copy pasta from {@link BlockItem#updateBlockStateFromTag(BlockPos, World, ItemStack, BlockState)}
     */
    @SuppressWarnings("ALL")
    private BlockState updateBlockStateFromTag(BlockPos pos, World world, ItemStack stack, BlockState state)
    {
        BlockState newState = state;
        CompoundTag nbt = stack.getTag();
        if (nbt != null)
        {
            CompoundTag blockStateNbt = nbt.getCompound("BlockStateTag");
            StateManager<Block, BlockState> container = state.getBlock().getStateManager();

            for (String propertyKey : blockStateNbt.getKeys())
            {
                Property<?> property = container.getProperty(propertyKey);
                if (property != null)
                {
                    String s1 = blockStateNbt.get(propertyKey).asString();
                    newState = updateState(newState, property, s1);
                }
            }
        }

        if (newState != state)
        {
            world.setBlockState(pos, newState, 2);
        }
        return newState;
    }
}
