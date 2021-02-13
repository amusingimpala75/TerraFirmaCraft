/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import net.dries007.tfc.wrapper.StairsBlock;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCMaterials;
import net.dries007.tfc.common.blocks.rock.*;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class Rock
{
    private final SandBlockType desertSandColor, beachSandColor;
    private final RockCategory category;
    private final boolean naturallyGenerating;
    private final Map<BlockType, Block> blockVariants;
    private final Identifier id;

    public Rock(Identifier id, JsonObject json)
    {
        this.id = id;
        String rockCategoryName = JsonHelper.getString(json, "category");
        this.category = Helpers.mapSafeOptional(() -> RockCategory.valueOf(rockCategoryName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown rock category: " + rockCategoryName));
        String desertSandColorName = JsonHelper.getString(json, "desert_sand_color");
        this.desertSandColor = Helpers.mapSafeOptional(() -> SandBlockType.valueOf(desertSandColorName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown sand color: " + desertSandColorName));
        String beachSandColorName = JsonHelper.getString(json, "beach_sand_color");
        this.beachSandColor = Helpers.mapSafeOptional(() -> SandBlockType.valueOf(beachSandColorName.toUpperCase())).orElseThrow(() -> new JsonParseException("Unknown beach sand color: " + beachSandColorName));
        this.naturallyGenerating = JsonHelper.getBoolean(json, "naturally_generated", true);

        this.blockVariants = Helpers.findRegistryObjects(json, "blocks", Registry.BLOCK, Arrays.asList(Rock.BlockType.values()), type -> type.name().toLowerCase());
    }

    public Identifier getId()
    {
        return id;
    }

    public Block getBlock(BlockType type)
    {
        return blockVariants.get(type);
    }

    public RockCategory getCategory()
    {
        return category;
    }

    public SandBlockType getDesertSandColor()
    {
        return desertSandColor;
    }

    public SandBlockType getBeachSandColor()
    {
        return beachSandColor;
    }

    public boolean isNaturallyGenerating()
    {
        return naturallyGenerating;
    }

    /**
     * Default rocks that are used for block registration calls.
     * Not extensible.
     *
     * @see Rock instead and register via json
     */
    public enum Default
    {
        GRANITE,
        DIORITE,
        GABBRO,
        SHALE,
        CLAYSTONE,
        LIMESTONE,
        CONGLOMERATE,
        DOLOMITE,
        CHERT,
        CHALK,
        RHYOLITE,
        BASALT,
        ANDESITE,
        DACITE,
        QUARTZITE,
        SLATE,
        PHYLLITE,
        SCHIST,
        GNEISS,
        MARBLE,
    }

    public enum BlockType implements StringIdentifiable
    {
        RAW((rock, self) -> new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(2, 10).breakByTool(FabricToolTags.PICKAXES, 0)), true),
        HARDENED((rock, self) -> new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(2, 10).breakByTool(FabricToolTags.PICKAXES, 0)), false),
        SMOOTH((rock, self) -> new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0)), true),
        COBBLE((rock, self) -> new MossGrowingBlock(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0), () -> TFCBlocks.ROCK_BLOCKS.get(rock).get(self.mossy())), true),
        BRICKS((rock, self) -> new MossGrowingBlock(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(2.0f, 10).breakByTool(FabricToolTags.PICKAXES, 0), () -> TFCBlocks.ROCK_BLOCKS.get(rock).get(self.mossy())), true),
        GRAVEL((rock, self) -> new Block(FabricBlockSettings.of(Material.AGGREGATE, MaterialColor.STONE).sounds(BlockSoundGroup.STONE).strength(0.8f).breakByTool(FabricToolTags.SHOVELS, 0)), false),
        SPIKE((rock, self) -> new RockSpikeBlock(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.4f, 10).breakByTool(FabricToolTags.PICKAXES, 0)), false),
        CRACKED_BRICKS((rock, self) -> new MossSpreadingBlock(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0)), true),
        MOSSY_BRICKS((rock, self) -> new MossSpreadingBlock(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0)), true),
        MOSSY_COBBLE((rock, self) -> new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0)), true),
        CHISELED((rock, self) -> new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0)), false),
        LOOSE((rock, self) -> new LooseRockBlock(Block.Settings.of(TFCMaterials.NON_SOLID_STONE).strength(0.05f, 0.0f).sounds(BlockSoundGroup.STONE).nonOpaque()), false);

        public static final BlockType[] VALUES = values();
        public static final Map<String, BlockType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(k -> k.name().toLowerCase(), v -> v));
        public static final Codec<BlockType> CODEC = StringIdentifiable.createCodec(BlockType::values, BlockType::byName);

        public static BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : RAW;
        }

        public static BlockType byName(String id)
        {
            return BY_NAME.get(id);
        }

        private final boolean variants;
        private final BiFunction<Default, BlockType, Block> blockFactory;
        private final String serializedName;

        BlockType(BiFunction<Default, BlockType, Block> blockFactory, boolean variants)
        {
            this.blockFactory = blockFactory;
            this.variants = variants;
            this.serializedName = name().toLowerCase();
        }

        /**
         * @return if this block type should be given slab, stair and wall variants
         */
        public boolean hasVariants()
        {
            return variants;
        }

        public Block create(Default rock)
        {
            return blockFactory.apply(rock, this);
        }

        public SlabBlock createSlab(Default rock)
        {
            AbstractBlock.Settings properties = FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0);
            if (mossy() == this)
            {
                return new MossSpreadingSlabBlock(properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingSlabBlock(properties, () -> TFCBlocks.ROCK_SLABS.get(rock).get(mossy()));
            }
            return new SlabBlock(properties);
        }

        public net.minecraft.block.StairsBlock createStairs(Default rock)
        {
            Supplier<BlockState> state = () -> TFCBlocks.ROCK_BLOCKS.get(rock).get(this).getDefaultState();
            AbstractBlock.Settings properties = FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0);
            if (mossy() == this)
            {
                return new MossSpreadingStairBlock(state, properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingStairsBlock(state, properties, () -> TFCBlocks.ROCK_STAIRS.get(rock).get(mossy()));
            }
            return new StairsBlock(state.get(), properties);
        }

        public WallBlock createWall(Default rock)
        {
            AbstractBlock.Settings properties = FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(1.5f, 10).breakByTool(FabricToolTags.PICKAXES, 0);
            if (mossy() == this)
            {
                return new MossSpreadingWallBlock(properties);
            }
            else if (mossy() != null)
            {
                return new MossGrowingWallBlock(properties, () -> TFCBlocks.ROCK_WALLS.get(rock).get(mossy()));
            }
            return new WallBlock(properties);
        }

        @Override
        public String asString()
        {
            return serializedName;
        }

        @Nullable
        private BlockType mossy()
        {
            switch (this)
            {
                case COBBLE:
                case MOSSY_COBBLE:
                    return MOSSY_COBBLE;
                case BRICKS:
                case MOSSY_BRICKS:
                    return MOSSY_BRICKS;
                default:
                    return null;
            }
        }
    }
}