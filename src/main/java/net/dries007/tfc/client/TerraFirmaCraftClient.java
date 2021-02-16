/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.dries007.tfc.client.screen.CalendarScreen;
import net.dries007.tfc.client.screen.ClimateScreen;
import net.dries007.tfc.client.screen.NutritionScreen;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.fabric.Networking;
import net.dries007.tfc.mixin.world.biome.BiomeColorsAccessor;
import net.dries007.tfc.util.Helpers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class TerraFirmaCraftClient implements ClientModInitializer {
    private static final int ALPHA_MASK = 0xFF000000;
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {

        for (Map.Entry<Metal.Default, TFCFluids.FluidPair<net.dries007.tfc.forgereplacements.fluid.FlowableFluid>> set : TFCFluids.METALS.entrySet()) {
            setupFluidRendering(set.getValue().getSource(), set.getValue().getFlowing(), ALPHA_MASK | set.getKey().getColor());
        }
        setupFluidRendering(TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getFlowing(), ALPHA_MASK | 0x3F76E4);
        setupFluidRendering(TFCFluids.SPRING_WATER.getSource(), TFCFluids.SPRING_WATER.getFlowing(), ALPHA_MASK | 0x4ECBD7);


        LOGGER.debug("Client Setup");

        // Screens
        ScreenRegistry.register(TFCContainerTypes.CALENDAR, CalendarScreen::new);
        ScreenRegistry.register(TFCContainerTypes.NUTRITION, NutritionScreen::new);
        ScreenRegistry.register(TFCContainerTypes.CLIMATE, ClimateScreen::new);

        // Render Types
        final RenderLayer cutout = RenderLayer.getCutout();
        final RenderLayer cutoutMipped = RenderLayer.getCutoutMipped();
        final RenderLayer translucent = RenderLayer.getTranslucent();

        // Rock blocks
        TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.SPIKE)).forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutout));
        TFCBlocks.ORES.values().forEach(map -> map.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutout)));
        TFCBlocks.GRADED_ORES.values().forEach(map -> map.values().forEach(inner -> inner.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutout))));

        // Wood blocks
        Stream.of(Wood.BlockType.SAPLING, Wood.BlockType.DOOR, Wood.BlockType.TRAPDOOR, Wood.BlockType.FENCE, Wood.BlockType.FENCE_GATE, Wood.BlockType.BUTTON, Wood.BlockType.PRESSURE_PLATE, Wood.BlockType.SLAB, Wood.BlockType.STAIRS).forEach(type -> TFCBlocks.WOODS.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg.get(type), cutout)));
        Stream.of(Wood.BlockType.LEAVES, Wood.BlockType.FALLEN_LEAVES, Wood.BlockType.TWIG).forEach(type -> TFCBlocks.WOODS.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg.get(type), cutoutMipped)));

        // Grass
        TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutoutMipped));
        TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutoutMipped));
        BlockRenderLayerMap.INSTANCE.putBlock(TFCBlocks.PEAT_GRASS, cutoutMipped);

        // Metal blocks
        TFCBlocks.METALS.values().forEach(map -> map.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutout)));

        // Groundcover
        TFCBlocks.GROUNDCOVER.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutout));
        TFCBlocks.SMALL_ORES.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutout));
        BlockRenderLayerMap.INSTANCE.putBlock(TFCBlocks.CALCITE, cutout);

        BlockRenderLayerMap.INSTANCE.putBlock(TFCBlocks.ICICLE, translucent);
        BlockRenderLayerMap.INSTANCE.putBlock(TFCBlocks.SEA_ICE, cutout);

        // Plants
        TFCBlocks.PLANTS.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutout));
        TFCBlocks.CORAL.values().forEach(map -> map.values().forEach(reg -> BlockRenderLayerMap.INSTANCE.putBlock(reg, cutout)));

        // Fluids
        BlockRenderLayerMap.INSTANCE.putFluid(TFCFluids.SALT_WATER.getFlowing(), translucent);
        BlockRenderLayerMap.INSTANCE.putFluid(TFCFluids.SALT_WATER.getSource(), translucent);
        BlockRenderLayerMap.INSTANCE.putFluid(TFCFluids.SPRING_WATER.getFlowing(), translucent);
        BlockRenderLayerMap.INSTANCE.putFluid(TFCFluids.SPRING_WATER.getSource(), translucent);

        // Entity Rendering
        EntityRendererRegistry.INSTANCE.register(TFCEntities.FALLING_BLOCK, (dispatcher, context) -> new FallingBlockEntityRenderer(dispatcher));

        // Misc
        BiomeColorsAccessor.accessor$setWaterColorResolver(TFCColors.FRESH_WATER);

        for (IdentifiableResourceReloadListener listener : registerResourceReloadListeners()) {
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(listener);
        }

        registerColorHandlerItems();
        registerColorHandlerBlocks();

        Networking.clientRegister();

        ClientForgeEventHandler.registerClientEvents();
    }


    public static void setupFluidRendering(final Fluid still, final Fluid flowing, final int color) {
        final Identifier stillSpriteId = Helpers.identifier("block/fluid_still");
        final Identifier flowingSpriteId = Helpers.identifier("block/fluid_flow");

        // If they're not already present, add the sprites to the block atlas
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) ->
        {
            registry.register(stillSpriteId);
            registry.register(flowingSpriteId);
        });

        final Identifier fluidId = Registry.FLUID.getId(still);
        final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        final Sprite[] fluidSprites = {null, null};

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return listenerId;
            }

            /**
             * Get the sprites from the block atlas when resources are reloaded
             */
            @Override
            public void apply(ResourceManager resourceManager) {
                final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                fluidSprites[0] = atlas.apply(stillSpriteId);
                fluidSprites[1] = atlas.apply(flowingSpriteId);
            }
        });

        // The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
        final FluidRenderHandler renderHandler = new FluidRenderHandler() {
            @Override
            public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
                return fluidSprites;
            }

            @Override
            public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
                return color;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
    }

    //@SubscribeEvent
    public static void registerColorHandlerBlocks()
    {
        LOGGER.debug("Registering Color Handler Blocks");
        //final BlockColors registry = event.getBlockColors();
        final int nope = -1;

        ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().toArray(new Block[0]));
        ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), TFCBlocks.SOIL.get(SoilBlockType.CLAY_GRASS).values().toArray(new Block[0]));
        ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), TFCBlocks.PEAT_GRASS);
        // Plants
        Block[] leafyPlants = Stream.of(Plant.values()).filter(Plant::isLeafColored).map(TFCBlocks.PLANTS::get).toArray(Block[]::new);
        Block[] grassyPlants = Stream.of(Plant.values()).filter(p -> !p.isLeafColored()).map(TFCBlocks.PLANTS::get).toArray(Block[]::new);
        ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(state, pos, tintIndex, Plant.BlockType.VINE.getFallFoliageCoords()), leafyPlants);
        ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> TFCColors.getGrassColor(pos, tintIndex), grassyPlants);

        TFCBlocks.WOODS.forEach((key, value) -> {
            Block leaves = value.get(Wood.BlockType.LEAVES);
            Block fallenLeaves = value.get(Wood.BlockType.FALLEN_LEAVES);
            if (key.isConifer())
            {
                ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> TFCColors.getFoliageColor(pos, tintIndex), leaves, fallenLeaves);
            }
            else
            {
                ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> TFCColors.getSeasonalFoliageColor(state, pos, tintIndex, key.getFallFoliageCoords()), leaves, fallenLeaves);
            }
        });

        ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> pos != null ? TFCColors.getWaterColor(pos) : nope, TFCBlocks.SALT_WATER, TFCBlocks.SEA_ICE);
        ColorProviderRegistry.BLOCK.register((state, worldIn, pos, tintIndex) -> pos != null ? TFCColors.getSpringWaterColor(pos) : nope, TFCBlocks.SPRING_WATER);
    }

    //@SubscribeEvent
    public static void registerColorHandlerItems()
    {
        LOGGER.debug("Registering Color Handler Blocks");
        //final ItemColors registry = event.getItemColors();

        Item[] leafyPlants = Stream.of(Plant.values()).filter(Plant::isLeafColored).map(p -> TFCBlocks.PLANTS.get(p).asItem()).toArray(Item[]::new);
        Item[] grassyPlants = Stream.of(Plant.values()).filter(Plant::needsItemColor).map(p -> TFCBlocks.PLANTS.get(p).asItem()).toArray(Item[]::new);
        ColorProviderRegistry.ITEM.register((itemStack, tintIndex) -> TFCColors.getGrassColor(new BlockPos(0, 96, 0), tintIndex), grassyPlants);
        ColorProviderRegistry.ITEM.register((itemStack, tintIndex) -> TFCColors.getFoliageColor(new BlockPos(0, 96, 0), tintIndex), leafyPlants);
    }

    //@SubscribeEvent
    public static List<IdentifiableResourceReloadListener> registerResourceReloadListeners()
    {
        // Add client reload listeners here, as it's closest to the location where they are added in vanilla
        //ReloadableResourceManager resourceManager = (ReloadableResourceManager) MinecraftClient.getInstance().getResourceManager();

        // Color maps
        // We maintain a series of color maps independent and beyond the vanilla color maps
        // Sky, Fog, Water and Water Fog color to replace hardcoded per-biome water colors
        // Grass and foliage (which we replace vanilla's anyway, but use our own for better indexing)
        // Foliage winter and fall (for deciduous trees which have leaves which change color during those seasons)
        List<IdentifiableResourceReloadListener> listeners = new ArrayList<>();
        listeners.add(new ColorMapReloadListener(TFCColors::setSkyColors, TFCColors.SKY_COLORS_LOCATION, "colormap/sky_colors"));
        listeners.add(new ColorMapReloadListener(TFCColors::setFogColors, TFCColors.FOG_COLORS_LOCATION, "colormap/fog_colors"));
        listeners.add(new ColorMapReloadListener(TFCColors::setWaterColors, TFCColors.WATER_COLORS_LOCATION, "colormap/water_colors"));
        listeners.add(new ColorMapReloadListener(TFCColors::setWaterFogColors, TFCColors.WATER_FOG_COLORS_LOCATION, "colormap/watr_fog_colors"));
        listeners.add(new ColorMapReloadListener(TFCColors::setGrassColors, TFCColors.GRASS_COLORS_LOCATION, "colormap/grass_colors"));
        listeners.add(new ColorMapReloadListener(TFCColors::setFoliageColors, TFCColors.FOLIAGE_COLORS_LOCATION, "colormap/foliage_colors"));
        listeners.add(new ColorMapReloadListener(TFCColors::setFoliageFallColors, TFCColors.FOLIAGE_FALL_COLORS_LOCATION, "colormap/fall_foliage_colors"));
        listeners.add(new ColorMapReloadListener(TFCColors::setFoliageWinterColors, TFCColors.FOLIAGE_WINTER_COLORS_LOCATION, "colormap/winter_foliage_colors"));

        return listeners;
    }
}
