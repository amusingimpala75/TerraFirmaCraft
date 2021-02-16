/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import net.dries007.tfc.forgereplacements.NotNullFunction;
import net.minecraft.block.*;

import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.FallenLeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.common.blocks.wood.TFCSaplingBlock;
import net.dries007.tfc.common.blocks.wood.ToolRackBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.feature.tree.TFCTree;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class Wood
{
    private static final Random rng = new Random();

    /**
     * Default wood types used for block registration calls
     * Not extensible
     *
     * todo: re-evaluate if there is any data driven behavior that needs a json driven ore
     *
     * @see Wood instead and register via json
     */
    public enum Default
    {
        ACACIA(false, MaterialColor.ORANGE_TERRACOTTA, MaterialColor.ORANGE_TERRACOTTA, MaterialColor.LIGHT_GRAY_TERRACOTTA, 0, 7),
        ASH(false, MaterialColor.PINK_TERRACOTTA, MaterialColor.PINK_TERRACOTTA, MaterialColor.ORANGE_TERRACOTTA, 0, 8),
        ASPEN(false, MaterialColor.GREEN_TERRACOTTA, MaterialColor.GREEN_TERRACOTTA, MaterialColor.WHITE_TERRACOTTA, 0, 7),
        BIRCH(false, MaterialColor.BROWN, MaterialColor.BROWN, MaterialColor.WHITE_TERRACOTTA, 0, 7),
        BLACKWOOD(false, MaterialColor.BLACK, MaterialColor.BLACK, MaterialColor.BROWN, 0, 7),
        CHESTNUT(false, MaterialColor.RED_TERRACOTTA, MaterialColor.RED_TERRACOTTA, MaterialColor.LIME, 0, 8),
        DOUGLAS_FIR(false, MaterialColor.YELLOW_TERRACOTTA, MaterialColor.YELLOW_TERRACOTTA, MaterialColor.BROWN_TERRACOTTA, 0, 7),
        HICKORY(false, MaterialColor.BROWN_TERRACOTTA, MaterialColor.BROWN_TERRACOTTA, MaterialColor.GRAY, 0, 7),
        KAPOK(true, MaterialColor.PINK, MaterialColor.PINK, MaterialColor.BROWN, 0, 7),
        MAPLE(false, MaterialColor.ORANGE, MaterialColor.ORANGE, MaterialColor.GRAY_TERRACOTTA, 0, 8),
        OAK(false, MaterialColor.WOOD, MaterialColor.WOOD, MaterialColor.BROWN, 0, 8),
        PALM(true, MaterialColor.ORANGE, MaterialColor.ORANGE, MaterialColor.BROWN, 0, 7),
        PINE(true, MaterialColor.GRAY_TERRACOTTA, MaterialColor.GRAY_TERRACOTTA, MaterialColor.GRAY, 0, 7),
        ROSEWOOD(false, MaterialColor.RED, MaterialColor.RED, MaterialColor.LIGHT_GRAY_TERRACOTTA, 0, 9),
        SEQUOIA(true, MaterialColor.RED_TERRACOTTA, MaterialColor.RED_TERRACOTTA, MaterialColor.RED_TERRACOTTA, 0, 7),
        SPRUCE(true, MaterialColor.PINK_TERRACOTTA, MaterialColor.PINK_TERRACOTTA, MaterialColor.BLACK_TERRACOTTA, 0, 7),
        SYCAMORE(false, MaterialColor.YELLOW, MaterialColor.YELLOW, MaterialColor.LIME_TERRACOTTA, 0, 7),
        WHITE_CEDAR(true, MaterialColor.WHITE_TERRACOTTA, MaterialColor.WHITE_TERRACOTTA, MaterialColor.LIGHT_GRAY_TERRACOTTA, 0, 7),
        WILLOW(false, MaterialColor.GREEN, MaterialColor.GREEN, MaterialColor.BROWN_TERRACOTTA, 0, 7);

        private final boolean conifer;
        private final MaterialColor mainColor;
        private final MaterialColor topColor;
        private final MaterialColor barkColor;
        private final TFCTree tree;
        private final int fallFoliageCoords;
        private final int maxDecayDistance;

        Default(boolean conifer, MaterialColor mainColor, MaterialColor topColor, MaterialColor barkColor, int fallFoliageCoords, int maxDecayDistance)
        {
            this.conifer = conifer;
            this.mainColor = mainColor;
            this.topColor = topColor;
            this.barkColor = barkColor;
            this.tree = new TFCTree(Helpers.identifier("tree/" + name().toLowerCase()), Helpers.identifier("tree/" + name().toLowerCase() + "_large"));
            this.fallFoliageCoords = rng.nextInt(256 * 256);
            this.maxDecayDistance = maxDecayDistance;
        }

        public boolean isConifer()
        {
            return conifer;
        }

        public TFCTree getTree()
        {
            return tree;
        }

        public int getFallFoliageCoords()
        {
            return fallFoliageCoords;
        }

        public int getMaxDecayDistance()
        {
            return maxDecayDistance;
        }

        public MaterialColor getMainColor()
        {
            return mainColor;
        }

        public MaterialColor getTopColor()
        {
            return topColor;
        }

        public MaterialColor getBarkColor()
        {
            return barkColor;
        }
    }

    public enum BlockType
    {
        // These two constructors were lifted from Blocks#log
        LOG(wood -> new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, stateIn -> stateIn.get(PillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sounds(BlockSoundGroup.WOOD)), false),
        STRIPPED_LOG(wood -> new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, stateIn -> stateIn.get(PillarBlock.AXIS) == Direction.Axis.Y ? wood.getTopColor() : wood.getBarkColor()).strength(2.0F).sounds(BlockSoundGroup.WOOD)), false),
        WOOD(wood -> new PillarBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sounds(BlockSoundGroup.WOOD)), false),
        STRIPPED_WOOD(wood -> new PillarBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sounds(BlockSoundGroup.WOOD)), false),
        LEAVES(wood -> TFCLeavesBlock.create(Block.Settings.of(Material.LEAVES, wood.getMainColor()).strength(0.5F).sounds(BlockSoundGroup.GRASS).ticksRandomly().nonOpaque(), wood.getMaxDecayDistance()), false),
        PLANKS(wood -> new Block(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), false),
        SAPLING(wood -> new TFCSaplingBlock(wood.getTree(), Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().strength(0).sounds(BlockSoundGroup.GRASS)), false),
        BOOKSHELF(wood -> new Block(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), true),
        DOOR(wood -> new DoorBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()) {}, true),
        TRAPDOOR(wood -> new TrapdoorBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()) {}, true),
        FENCE(wood -> new FenceBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), true),
        LOG_FENCE(wood -> new FenceBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), true),
        FENCE_GATE(wood -> new FenceGateBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), true),
        BUTTON(wood -> new WoodenButtonBlock(Block.Settings.of(Material.SUPPORTED).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)) {}, true),
        PRESSURE_PLATE(wood -> new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, Block.Settings.of(Material.WOOD, wood.getMainColor()).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)) {}, true),
        SLAB(wood -> new SlabBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), true),
        //STAIRS(wood -> new net.dries007.tfc.wrapper.StairsBlock(TFCBlocks.WOODS.get(wood).get(PLANKS).getDefaultState(), Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), true),
        STAIRS(wood -> new net.dries007.tfc.wrapper.StairsBlock(Registry.BLOCK.get(Helpers.identifier("wood/planks/"+wood.name().toLowerCase(Locale.ROOT))).getDefaultState(), Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), true),
        TOOL_RACK(wood -> new ToolRackBlock(Block.Settings.of(Material.WOOD, wood.getMainColor()).strength(2.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()) {}, true),
        TWIG(wood -> GroundcoverBlock.twig(Block.Settings.of(Material.SOLID_ORGANIC).strength(0.05F, 0.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()), false),
        FALLEN_LEAVES(wood -> new FallenLeavesBlock(Block.Settings.of(Material.SOLID_ORGANIC).strength(0.05F, 0.0F).nonOpaque().sounds(BlockSoundGroup.CROP)), false);

        public static final BlockType[] VALUES = values();

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : LOG;
        }

        private final NotNullFunction<Default, Block> blockFactory;
        private final boolean isPlanksVariant;

        BlockType(NotNullFunction<Default, Block> blockFactory, boolean isPlanksVariant)
        {
            this.blockFactory = blockFactory;
            this.isPlanksVariant = isPlanksVariant;
        }

        public Supplier<Block> create(Default wood)
        {
            return () -> blockFactory.run(wood);
        }

        public String nameFor(Default wood)
        {
            return (isPlanksVariant ? "wood/planks/" + wood.name() + "_" + name().toLowerCase() : "wood/" + name() + "/" + wood.name()).toLowerCase();
        }
    }
}