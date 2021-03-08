/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public enum Coral {
    TUBE(MaterialColor.BLUE),
    BRAIN(MaterialColor.PINK),
    BUBBLE(MaterialColor.PURPLE),
    FIRE(MaterialColor.RED),
    HORN(MaterialColor.YELLOW);

    private final MaterialColor material;

    Coral(MaterialColor material)
    {
        this.material = material;
    }

    public enum BlockType {
        /*DEAD_CORAL((color, type) -> new TFCDeadCoralPlantBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())),
        //CORAL((color, type) -> new TFCCoralPlantBlock(() -> TFCBlocks.CORAL.get(color).get(DEAD_CORAL), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))),
        CORAL((color, type) -> new TFCCoralPlantBlock(() -> Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + DEAD_CORAL.toString().toLowerCase())), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))),
        DEAD_CORAL_FAN((color, type) -> new TFCCoralFanBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())),
        //CORAL_FAN((color, type) -> new LivingCoralPlantBlock(() -> TFCBlocks.CORAL.get(color).get(DEAD_CORAL_FAN), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))),
        CORAL_FAN((color, type) -> new LivingCoralPlantBlock(() -> Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + DEAD_CORAL_FAN.toString().toLowerCase())), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))),
        DEAD_CORAL_WALL_FAN((color, type) -> new CoralWallFanBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + DEAD_CORAL_FAN.toString().toLowerCase()))))),
        //CORAL_WALL_FAN((color, type) -> new LivingCoralWallFanBlock(() -> TFCBlocks.CORAL.get(color).get(DEAD_CORAL_WALL_FAN), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).dropsLike(TFCBlocks.CORAL.get(color).get(CORAL_FAN))));
        CORAL_WALL_FAN((color, type) -> new LivingCoralWallFanBlock(() -> Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + DEAD_CORAL_WALL_FAN.toString().toLowerCase())), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).dropsLike(Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + CORAL_FAN.toString().toLowerCase())))));
*/
        DEAD_CORAL((color, type) -> new TFCCoralPlantBlock(TFCCoralPlantBlock.BIG_SHAPE, AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())),
        CORAL((color, type) -> new LivingCoralPlantBlock(TFCCoralPlantBlock.BIG_SHAPE, () -> Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + DEAD_CORAL.toString().toLowerCase())), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))),
        DEAD_CORAL_FAN((color, type) -> new TFCCoralPlantBlock(TFCCoralPlantBlock.SMALL_SHAPE, AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly())),
        CORAL_FAN((color, type) -> new LivingCoralPlantBlock(TFCCoralPlantBlock.SMALL_SHAPE, () -> Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + DEAD_CORAL_FAN.toString().toLowerCase())), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS))),
        DEAD_CORAL_WALL_FAN((color, type) -> new CoralWallFanBlock(AbstractBlock.Settings.of(Material.STONE, MaterialColor.GRAY).requiresTool().noCollision().breakInstantly().dropsLike(Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + DEAD_CORAL_FAN.toString().toLowerCase()))))),
        CORAL_WALL_FAN((color, type) -> new LivingCoralWallFanBlock(() -> Registry.BLOCK.get(Helpers.identifier("coral/" + color.toString().toLowerCase() + "_" + DEAD_CORAL_WALL_FAN.toString().toLowerCase())), AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, color.material).noCollision().breakInstantly().sounds(BlockSoundGroup.WET_GRASS).dropsLike(TFCBlocks.CORAL.get(color).get(CORAL_FAN))));

        private final BiFunction<Coral, Coral.BlockType, ? extends Block> factory;
        private final BiFunction<Block, Item.Settings, ? extends BlockItem> blockItemFactory;

        BlockType(BiFunction<Coral, Coral.BlockType, ? extends Block> factory)
        {
            this(factory, BlockItem::new);
        }

        BlockType(BiFunction<Coral, Coral.BlockType, ? extends Block> factory, BiFunction<Block, Item.Settings, ? extends BlockItem> blockItemFactory) {
            this.factory = factory;
            this.blockItemFactory = blockItemFactory;
        }

        public boolean needsItem()
        {
            return this == DEAD_CORAL || this == CORAL;
        }

        public Supplier<Block> create(Coral color)
        {
            return () -> factory.apply(color, this);
        }

        public Function<Block, BlockItem> createBlockItem(Item.Settings properties)
        {
            return block -> blockItemFactory.apply(block, properties);
        }
    }
}
