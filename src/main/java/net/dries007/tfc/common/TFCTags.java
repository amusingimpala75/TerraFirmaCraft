/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

import net.dries007.tfc.util.Helpers;
import net.minecraft.util.Identifier;

public class TFCTags
{
    public static class Blocks
    {
        public static final Tag.Identified<Block> CAN_TRIGGER_COLLAPSE = create("can_trigger_collapse");
        public static final Tag.Identified<Block> CAN_START_COLLAPSE = create("can_start_collapse");
        public static final Tag.Identified<Block> CAN_COLLAPSE = create("can_collapse");
        public static final Tag.Identified<Block> CAN_LANDSLIDE = create("can_landslide");
        public static final Tag.Identified<Block> SUPPORTS_LANDSLIDE = create("supports_landslide"); // Non-full blocks that count as full blocks for the purposes of landslide side support check
        public static final Tag.Identified<Block> GRASS = create("grass"); // Used for connected textures on grass blocks, different from the vanilla/forge tag
        public static final Tag.Identified<Block> TREE_GROWS_ON = create("tree_grows_on"); // Used for tree growth
        public static final Tag.Identified<Block> BUSH_PLANTABLE_ON = create("bush_plantable_on"); // Used for plant placement
        public static final Tag.Identified<Block> PLANT = create("plant"); // for some decoration placement
        public static final Tag.Identified<Block> SEA_BUSH_PLANTABLE_ON = create("sea_bush_plantable_on"); // Used for sea plant placement
        public static final Tag.Identified<Block> CREEPING_PLANTABLE_ON = create("creeping_plantable_on");
        public static final Tag.Identified<Block> KELP_TREE = create("kelp_tree");
        public static final Tag.Identified<Block> KELP_FLOWER = create("kelp_flower");
        public static final Tag.Identified<Block> KELP_BRANCH = create("kelp_branch");
        public static final Tag.Identified<Block> WALL_CORALS = create("wall_corals");
        public static final Tag.Identified<Block> CORALS = create("corals");

        public static final Tag.Identified<Block> THATCH_BED_THATCH = create("thatch_bed_thatch");

        public static final Tag.Identified<Block> SNOW = create("snow"); // Blocks that cover grass with snow.
        public static final Tag.Identified<Block> CAN_BE_SNOW_PILED = create("can_be_snow_piled"); // Blocks that can be replaced with snow piles

        public static final Tag.Identified<Block> BREAKS_WHEN_ISOLATED = create("breaks_when_isolated"); // When surrounded on all six sides by air, this block will break and drop itself

        private static Tag.Identified<Block> create(String id)
        {
            return (Tag.Identified<Block>) TagRegistry.block(Helpers.identifier(id));
        }
    }

    public static class Fluids
    {
        public static final Tag.Identified<Fluid> MIXABLE = create("mixable");

        private static Tag.Identified<Fluid> create(String id)
        {
            return (Tag.Identified<Fluid>) TagRegistry.fluid(Helpers.identifier(id));
        }
    }

    public static class Items
    {
        public static final Tag.Identified<Item> THATCH_BED_HIDES = create("thatch_bed_hides");

        //ToolTypes
        public static final Tag.Identified<Item> HAMMER = create("hammer");
        public static final Tag.Identified<Item> CHISEL = create("chisel");
        public static final Tag.Identified<Item> KNIFE = create("knife");

        private static Tag.Identified<Item> create(String id)
        {
            return (Tag.Identified<Item>) TagRegistry.item(Helpers.identifier(id));
        }
    }

    public static class Common
    {
        public static final Tag.Identified<Block> SAND = block("sand");

        private static Tag.Identified<Block> block(String id)
        {
            return (Tag.Identified<Block>) TagRegistry.block(new Identifier("c", id));
        }
    }
}