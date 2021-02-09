/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.coral.TFCSeaPickleBlock;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.tileentity.SnowPileTileEntity;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.forgereplacements.fluid.FluidBlock;

import static net.dries007.tfc.common.TFCItemGroup.*;


/**
 * Collection of all TFC blocks.
 * Unused is as the registry object fields themselves may be unused but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCBlocks {
    // Earth

    public static final Map<SoilBlockType, Map<SoilBlockType.Variant, Block>> SOIL = Helpers.mapOfKeys(SoilBlockType.class, type ->
        Helpers.mapOfKeys(SoilBlockType.Variant.class, variant ->
            register((type.name() + "/" + variant.name()).toLowerCase(), () -> type.create(variant), EARTH)
        )
    );

    public static final Block PEAT = register("peat", () -> new Block(FabricBlockSettings.of(Material.SOIL, MaterialColor.BLACK_TERRACOTTA).breakByTool(FabricToolTags.SHOVELS, 0).sounds(BlockSoundGroup.GRAVEL)), EARTH);
    public static final Block PEAT_GRASS = register("peat_grass", () -> new ConnectedGrassBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS).breakByTool(FabricToolTags.SHOVELS, 0), () -> PEAT, null, null), EARTH);

    public static final Map<SandBlockType, Block> SAND = Helpers.mapOfKeys(SandBlockType.class, type ->
        register(("sand/" + type.name()).toLowerCase(), type::create, EARTH)
    );

    public static final Map<GroundcoverBlockType, Block> GROUNDCOVER = Helpers.mapOfKeys(GroundcoverBlockType.class, type ->
        register(("groundcover/" + type.name()).toLowerCase(), () -> new GroundcoverBlock(type), block -> new BlockItem(block, new Item.Settings().group(EARTH)), type.shouldCreateBlockItem())
    );

    public static final Block SEA_ICE = register("sea_ice", () -> new SeaIceBlock(AbstractBlock.Settings.of(Material.ICE).slipperiness(0.98f).ticksRandomly().strength(0.5f).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning(TFCBlocks::onlyPolarBears)), EARTH);
    public static final SnowPileBlock SNOW_PILE = (SnowPileBlock) register("snow_pile", () -> new SnowPileBlock(new ForgeBlockProperties(FabricBlockSettings.copyOf(FabricBlockSettings.copy(Blocks.SNOW)).breakByTool(FabricToolTags.SHOVELS, 0)).tileEntity(SnowPileTileEntity::new)), EARTH);
    public static final ThinSpikeBlock ICICLE = (ThinSpikeBlock) register("icicle", () -> new ThinSpikeBlock(AbstractBlock.Settings.of(Material.ICE).dropsNothing().strength(0.4f).sounds(BlockSoundGroup.GLASS).nonOpaque()));

    public static final ThinSpikeBlock CALCITE = (ThinSpikeBlock) register("calcite", () -> new ThinSpikeBlock(AbstractBlock.Settings.of(Material.GLASS).dropsNothing().strength(0.2f).sounds(BlockSoundGroup.BONE)));

    // Ores

    public static final Map<Rock.Default, Map<Ore.Default, Block>> ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, ore -> !ore.isGraded(), ore ->
            register(("ore/" + ore.name() + "/" + rock.name()).toLowerCase(), () -> new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(3, 10).breakByTool(FabricToolTags.PICKAXES, 0)), TFCItemGroup.ORES)
        )
    );
    public static final Map<Rock.Default, Map<Ore.Default, Map<Ore.Grade, Block>>> GRADED_ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, ore ->
            Helpers.mapOfKeys(Ore.Grade.class, grade ->
                register(("ore/" + grade.name() + "_" + ore.name() + "/" + rock.name()).toLowerCase(), () -> new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(3, 10).breakByTool(FabricToolTags.PICKAXES, 0)), TFCItemGroup.ORES)
            )
        )
    );
    public static final Map<Ore.Default, Block> SMALL_ORES = Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, type ->
        register(("ore/small_" + type.name()).toLowerCase(), () -> GroundcoverBlock.looseOre(AbstractBlock.Settings.of(Material.PLANT).strength(0.05F, 0.0F).sounds(BlockSoundGroup.NETHER_ORE).nonOpaque()), TFCItemGroup.ORES)
    );

    // Rock Stuff

    public static final Map<Rock.Default, Map<Rock.BlockType, Block>> ROCK_BLOCKS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase(), () -> type.create(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, SlabBlock>> ROCK_SLABS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            (SlabBlock) register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_slab", () -> type.createSlab(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, StairsBlock>> ROCK_STAIRS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            (StairsBlock) register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_stairs", () -> type.createStairs(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, Block>> ROCK_WALLS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_wall", () -> type.createWall(rock), ROCK_STUFFS)
        )
    );

    // Metals

    public static final Map<Metal.Default, Map<Metal.BlockType, Block>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.BlockType.class, type -> type.hasMetal(metal), type ->
            register(("metal/" + type.name() + "/" + metal.name()).toLowerCase(), type.create(metal), METAL)
        )
    );

    // Wood

    public static final Map<Wood.Default, Map<Wood.BlockType, Block>> WOODS = Helpers.mapOfKeys(Wood.Default.class, wood ->
        Helpers.mapOfKeys(Wood.BlockType.class, type ->
            register(type.nameFor(wood), type.create(wood), WOOD)
        )
    );

    // Flora

    public static final Map<Plant, Block> PLANTS = Helpers.mapOfKeys(Plant.class, plant ->
        register(("plant/" + plant.name()).toLowerCase(), plant::create, block -> plant.createBlockItem(block, new Item.Settings().group(FLORA)), plant.needsItem())
    );

    public static final Map<Coral.Color, Map<Coral.BlockType, Block>> CORAL = Helpers.mapOfKeys(Coral.Color.class, color ->
        Helpers.mapOfKeys(Coral.BlockType.class, type ->
            register("coral/" + color.toString().toLowerCase() + "_" + type.toString().toLowerCase(), type.create(color), block -> type.createBlockItem(block, new Item.Settings().group(FLORA)), type.needsItem())
        )
    );

    public static final Block SEA_PICKLE = register("sea_pickle", () -> new TFCSeaPickleBlock(AbstractBlock.Settings.of(Material.UNDERWATER_PLANT, MaterialColor.GREEN)
        .luminance((state) -> TFCSeaPickleBlock.isDead(state) ? 0 : 3 + 3 * state.get(SeaPickleBlock.PICKLES)).sounds(BlockSoundGroup.SLIME).nonOpaque()), FLORA);

    // Misc
    public static final Block THATCH = register("thatch", () -> new ThatchBlock(new ForgeBlockProperties(FabricBlockSettings.of(Material.PLANT).strength(0.6F, 0.4F).nonOpaque().sounds(BlockSoundGroup.GRASS)).flammable(50, 100)), TFCItemGroup.MISC);
    public static final Block THATCH_BED = register("thatch_bed", () -> new ThatchBedBlock(Block.Settings.of(Material.REPLACEABLE_PLANT).strength(0.6F, 0.4F)), TFCItemGroup.MISC);

    // Fluids

    public static final Map<Metal.Default, FluidBlock> METAL_FLUIDS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        (FluidBlock) register("fluid/metal/" + metal.name().toLowerCase(), () -> new FluidBlock(TFCFluids.METALS.get(metal).getSecond(), Block.Settings.of(TFCMaterials.MOLTEN_METAL).noCollision().strength(100f).dropsNothing()))
    );

    public static final FluidBlock SALT_WATER = (FluidBlock) register("fluid/salt_water", () -> new FluidBlock(TFCFluids.SALT_WATER.getSecond(), Block.Settings.of(TFCMaterials.SALT_WATER).noCollision().strength(100f).dropsNothing()));
    public static final FluidBlock SPRING_WATER = (FluidBlock) register("fluid/spring_water", () -> new FluidBlock(TFCFluids.SPRING_WATER.getSecond(), Block.Settings.of(TFCMaterials.SPRING_WATER).noCollision().strength(100f).dropsNothing()));

    public static boolean always(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    public static boolean never(BlockState state, BlockView world, BlockPos pos)
    {
        return false;
    }

    public static boolean onlyPolarBears(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return type == EntityType.POLAR_BEAR; // todo: does this need to be expanded?
    }

    private static Block register(String name, Supplier<Block> blockSupplier) {
        return register(name, blockSupplier, block -> null, false);
    }

    private static Block register(String name, Supplier<Block> blockSupplier, ItemGroup group) {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Settings().group(group)), true);
    }

    private static Block register(String name, Supplier<Block> blockSupplier, Item.Settings blockItemProperties) {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties), true);
    }

    private static Block register(String name, Supplier<Block> blockSupplier, Function<Block, ? extends BlockItem> blockItemFactory, boolean hasItemBlock) {
        Block block = Registry.register(Registry.BLOCK, Helpers.identifier(name), blockSupplier.get());
        if (hasItemBlock) {
            TFCItems.register(name, () -> blockItemFactory.apply(block));
        }
        return block;
    }

    public static void register() {}
}