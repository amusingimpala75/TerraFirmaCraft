/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.function.Supplier;

import net.dries007.tfc.util.Helpers;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.minecraft.util.Lazy;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public abstract class TFCItemGroup extends ItemGroup
{
    // todo: replace this eventually with actual items
    public static final Supplier<ItemStack> MISSING_ITEM = () -> new ItemStack(Items.JACK_O_LANTERN);

    public static final ItemGroup EARTH = FabricItemGroupBuilder.build(Helpers.identifier("earth"), () -> new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.QUARTZITE).get(Rock.BlockType.RAW)));
    public static final ItemGroup ORES = FabricItemGroupBuilder.build(Helpers.identifier("ores"), MISSING_ITEM);
    public static final ItemGroup ROCK_STUFFS = FabricItemGroupBuilder.build(Helpers.identifier("rock"), MISSING_ITEM);
    public static final ItemGroup METAL = FabricItemGroupBuilder.build(Helpers.identifier("metals"), () -> new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT)));
    public static final ItemGroup WOOD = FabricItemGroupBuilder.build(Helpers.identifier("wood"), () -> new ItemStack(TFCBlocks.WOODS.get(Wood.Default.DOUGLAS_FIR).get(Wood.BlockType.LOG)));
    public static final ItemGroup FOOD = FabricItemGroupBuilder.build(Helpers.identifier("food"), MISSING_ITEM);
    public static final ItemGroup FLORA = FabricItemGroupBuilder.build(Helpers.identifier("flora"), () -> new ItemStack(TFCBlocks.PLANTS.get(Plant.GOLDENROD)));
    public static final ItemGroup MISC = FabricItemGroupBuilder.build(Helpers.identifier("misc"), MISSING_ITEM);

    public TFCItemGroup(int index, String id) {
        super(index, id);
    }
}