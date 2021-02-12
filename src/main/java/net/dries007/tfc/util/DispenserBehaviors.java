/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.items.TFCItems;

public class DispenserBehaviors
{
    private static final DispenserBehavior DEFAULT = new ItemDispenserBehavior();

    private static final DispenserBehavior BUCKET_BEHAVIOR = new ItemDispenserBehavior()
    {
        @Override
        public ItemStack dispense(BlockPointer source, ItemStack stack)
        {
            BucketItem bucket = (BucketItem) stack.getItem();
            BlockPos pos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            World world = source.getWorld();
            if (bucket.placeFluid(null, world, pos, null))
            {
                bucket.onEmptied(world, stack, pos);
                return new ItemStack(Items.BUCKET);
            }
            else
            {
                return DEFAULT.dispense(source, stack);
            }
        }
    };

    /**
     * {@link DispenserBlock#registerBehavior(net.minecraft.item.ItemConvertible, DispenserBehavior)} is not thread safe
     */
    public static void syncSetup()
    {
        // Bucket emptying
        DispenserBlock.registerBehavior(TFCItems.SALT_WATER_BUCKET, BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(TFCItems.SPRING_WATER_BUCKET, BUCKET_BEHAVIOR);

        TFCItems.METAL_FLUID_BUCKETS.values().forEach(reg -> DispenserBlock.registerBehavior(reg, BUCKET_BEHAVIOR));
    }
}
