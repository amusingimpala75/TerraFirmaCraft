/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.GroundcoverBlockType;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.ThatchBedBlock;
import net.dries007.tfc.util.collections.IndirectHashCollection;

/**
 * This exists due to problems in handling right click events
 * Forge provides a right click block event. This works for intercepting would-be calls to {@link net.minecraft.block.BlockState#onUse(World, PlayerEntity, Hand, BlockHitResult)}
 * However, this cannot be used (maintaining vanilla behavior) for item usages, or calls to @link net.minecraft.item.ItemStack#onItemUse(ItemUseContext, Function), as the priority of those two behaviors are very different (blocks take priority, cancelling the event with an item behavior forces the item to take priority
 *
 * This is in lieu of a system such as https://github.com/MinecraftForge/MinecraftForge/pull/6615
 */
public final class InteractionManager
{
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final List<Entry> ACTIONS = new ArrayList<>();
    private static final IndirectHashCollection<Item, Entry> CACHE = new IndirectHashCollection<>(wrapper -> wrapper.keyExtractor.get());

    public static void setup()
    {
        register(TFCTags.Items.THATCH_BED_HIDES, (stack, context) -> {
            final World world = context.getWorld();
            final PlayerEntity player = context.getPlayer();
            if (!world.isClient() && player != null)
            {
                final BlockPos basePos = context.getBlockPos();
                final Direction facing = context.getPlayerFacing();
                final BlockState bed = TFCBlocks.THATCH_BED.getDefaultState();
                for (Direction direction : new Direction[] {facing, facing.rotateYClockwise(), facing.getOpposite(), facing.rotateYCounterclockwise()})
                {
                    final BlockPos headPos = basePos.offset(direction, 1);
                    if (world.getBlockState(basePos).isIn(TFCTags.Blocks.THATCH_BED_THATCH) && world.getBlockState(headPos).isIn(TFCTags.Blocks.THATCH_BED_THATCH))
                    {
                        final BlockPos playerPos = player.getBlockPos();
                        if (playerPos != headPos && playerPos != basePos)
                        {
                            world.setBlockState(basePos, bed.with(ThatchBedBlock.PART, BedPart.FOOT).with(ThatchBedBlock.FACING, direction), 18);
                            world.setBlockState(headPos, bed.with(ThatchBedBlock.PART, BedPart.HEAD).with(ThatchBedBlock.FACING, direction.getOpposite()), 18);
                            stack.decrement(1);
                            return ActionResult.SUCCESS;
                        }

                    }
                }
            }
            return ActionResult.FAIL;
        });

        register(Items.SNOW, (stack, context) -> {
            PlayerEntity player = context.getPlayer();
            if (player != null && !player.abilities.allowModifyWorld)
            {
                return ActionResult.PASS;
            }
            else
            {
                final ItemPlacementContext blockContext = new ItemPlacementContext(context);
                final World world = context.getWorld();
                final BlockPos pos = context.getBlockPos();
                final BlockState stateAt = world.getBlockState(blockContext.getBlockPos());
                if (stateAt.isIn(TFCTags.Blocks.CAN_BE_SNOW_PILED))
                {
                    SnowPileBlock.convertToPile(world, pos, stateAt);
                    BlockState placedState = world.getBlockState(pos);
                    BlockSoundGroup placementSound = placedState.getSoundGroup(/*world, pos, player*/);
                    world.playSound(player, pos, placedState.getSoundGroup(/*world, pos, player*/).getPlaceSound(), SoundCategory.BLOCKS, (placementSound.getVolume() + 1.0F) / 2.0F, placementSound.getPitch() * 0.8F);
                    if (player == null || !player.abilities.creativeMode)
                    {
                        stack.decrement(1);
                    }

                    ActionResult result = ActionResult.success(world.isClient);
                    if (player != null && result.isAccepted())
                    {
                        player.incrementStat(Stats.USED.getOrCreateStat(Items.SNOW));
                    }
                    return result;
                }
                // Default behavior
                Item snow = Items.SNOW;
                if (snow instanceof BlockItem)
                {
                    return ((BlockItem) snow).place(blockContext);
                }
                return ActionResult.FAIL;
            }
        });

        // BlockItem mechanics for vanilla items that match groundcover types
        for (GroundcoverBlockType type : GroundcoverBlockType.values())
        {
            if (type.getVanillaItem() != null)
            {
                register(new BlockItemPlacement(type.getVanillaItem(), () -> TFCBlocks.GROUNDCOVER.get(type)));
            }
        }

        // todo: hide tag right click -> generic scraping recipe
        // todo: knapping tags
        // todo: log piles
        // todo: charcoal piles
    }

    public static void register(BlockItemPlacement wrapper)
    {
        ACTIONS.add(new Entry(wrapper, stack -> stack.getItem() == wrapper.getItem(), () -> Collections.singleton(wrapper.getItem())));
    }

    public static void register(Item item, OnItemUseAction action)
    {
        ACTIONS.add(new Entry(action, stack -> stack.getItem() == item, () -> Collections.singleton(item)));
    }

    public static void register(Tag<Item> tag, OnItemUseAction action)
    {
        ACTIONS.add(new Entry(action, stack -> stack.getItem().isIn(tag), tag::values));
    }

    public static Optional<ActionResult> onItemUse(ItemStack stack, ItemUsageContext context)
    {
        if (!ACTIVE.get())
        {
            for (Entry entry : CACHE.getAll(stack.getItem()))
            {
                if (entry.test.test(stack))
                {
                    ActionResult result;
                    ACTIVE.set(true);
                    try
                    {
                        result = entry.action.onItemUse(stack, context);
                    }
                    finally
                    {
                        ACTIVE.set(false);
                    }
                    return Optional.of(result);
                }
            }
        }
        return Optional.empty();
    }

    public static void reload()
    {
        CACHE.reload(ACTIONS);
    }

    @FunctionalInterface
    public interface OnItemUseAction
    {
        ActionResult onItemUse(ItemStack stack, ItemUsageContext context);
    }

    private static class Entry
    {
        private final OnItemUseAction action;
        private final Predicate<ItemStack> test;
        private final Supplier<Iterable<Item>> keyExtractor;

        private Entry(OnItemUseAction action, Predicate<ItemStack> test, Supplier<Iterable<Item>> keyExtractor)
        {
            this.action = action;
            this.test = test;
            this.keyExtractor = keyExtractor;
        }
    }
}
