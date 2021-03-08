/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.Gem;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockCategory;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.TFCItemGroup.FLORA;
import static net.dries007.tfc.common.TFCItemGroup.MISC;

/**
 * Collection of all TFC items.
 * Unused is as the registry object fields themselves may be unused but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCItems {
    // Ores

    public static final Map<Ore.Default, Item> ORES = Helpers.mapOfKeys(Ore.Default.class, ore -> !ore.isGraded(), type ->
        register("ore/" + type.name().toLowerCase(), TFCItemGroup.ORES)
    );
    public static final Map<Ore.Default, Map<Ore.Grade, Item>> GRADED_ORES = Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, ore ->
        Helpers.mapOfKeys(Ore.Grade.class, grade ->
            register(("ore/" + grade.name() + '_' + ore.name()).toLowerCase(), TFCItemGroup.ORES)
        )
    );

    public static final Map<Gem, Item> GEMS = Helpers.mapOfKeys(Gem.class, gem ->
        register(("gem/" + gem.name()).toLowerCase(), TFCItemGroup.ORES)
    );

    public static final Map<Metal.Default, Map<Metal.ItemType, Item>> METAL_ITEMS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.ItemType.class, type -> type.hasMetal(metal), type ->
            register(("metal/" + type.name() + "/" + metal.name()).toLowerCase(), () -> type.create(metal))
        )
    );

    // Flora

    public static final Map<Coral, Item> CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString().toLowerCase() + "_coral_fan", () -> new WallStandingBlockItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_FAN), TFCBlocks.CORAL.get(color).get(Coral.BlockType.CORAL_WALL_FAN), (new Item.Settings()).group(FLORA)))
    );

    public static final Map<Coral, Item> DEAD_CORAL_FANS = Helpers.mapOfKeys(Coral.class, color ->
        register("coral/" + color.toString().toLowerCase() + "_dead_coral_fan", () -> new WallStandingBlockItem(TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_FAN), TFCBlocks.CORAL.get(color).get(Coral.BlockType.DEAD_CORAL_WALL_FAN), (new Item.Settings()).group(FLORA)))
    );

    // Rock Stuff

    public static final Map<RockCategory, Map<RockCategory.ItemType, Item>> ROCK_TOOLS = Helpers.mapOfKeys(RockCategory.class, category ->
        Helpers.mapOfKeys(RockCategory.ItemType.class, type ->
            register(("stone/" + type.name() + "/" + category.name()).toLowerCase(), () -> type.create(category))
        )
    );

    public static final Map<Rock.Default, Item> BRICKS = Helpers.mapOfKeys(Rock.Default.class, type ->
        register("brick/" + type.name().toLowerCase(), MISC)
    );

    // Misc


    public static final Map<HideItemType, Map<HideItemType.Size, Item>> HIDES = Helpers.mapOfKeys(HideItemType.class, type ->
        Helpers.mapOfKeys(HideItemType.Size.class, size ->
            register((size.name() + '_' + type.name() + "_hide").toLowerCase(), () -> new Item(new Item.Settings().group(MISC)))
        )
    );

    public static final Map<Gem, Item> GEM_DUST = Helpers.mapOfKeys(Gem.class, gem ->
        register(("powder/" + gem.name()).toLowerCase(), MISC)
    );

    public static final Item MORTAR = register("mortar", MISC);
    public static final Item STRAW = register("straw", MISC);

    // Fluid Buckets

    public static final Map<Metal.Default, BucketItem> METAL_FLUID_BUCKETS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        (BucketItem) register("bucket/metal/" + metal.name().toLowerCase(), () -> new BucketItem(TFCFluids.METALS.get(metal).getSecond(), new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(MISC)))
    );

    public static final BucketItem SALT_WATER_BUCKET = (BucketItem) register("bucket/salt_water", () -> new BucketItem(TFCFluids.SALT_WATER.getSecond(), new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(MISC)));
    public static final BucketItem SPRING_WATER_BUCKET = (BucketItem) register("bucket/spring_water", () -> new BucketItem(TFCFluids.SPRING_WATER.getSecond(), new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(MISC)));


    public static Item register(String name, ItemGroup group) {
        return register(name, () -> new Item(new Item.Settings().group(group)));
    }

    public static Item register(String name, Supplier<Item> item) {
        return Registry.register(Registry.ITEM, Helpers.identifier(name), item.get());
    }

    public static void register() {}
}