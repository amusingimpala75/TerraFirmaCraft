/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.dries007.tfc.fabric.Networking;
import net.dries007.tfc.forgereplacements.world.ServerUtil;
import net.dries007.tfc.util.calendar.CalendarEventHandler;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.registry.Registry;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.common.tileentity.TFCTileEntities;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.DispenserBehaviors;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.InteractionManager;
import net.dries007.tfc.util.calendar.ServerCalendar;
import net.dries007.tfc.util.loot.TFCLoot;
import net.dries007.tfc.world.placer.TFCBlockPlacers;
import net.dries007.tfc.world.TFCBlockStateProviderTypes;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.TFCWorldType;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.carver.TFCCarvers;
import net.dries007.tfc.world.decorator.TFCDecorators;
import net.dries007.tfc.world.feature.TFCFeatures;
import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

//@Mod(TerraFirmaCraft.MOD_ID)
public final class TerraFirmaCraft implements ModInitializer {
    public static final String MOD_ID = "tfc";
    public static final String MOD_NAME = "TerraFirmaCraft";

    public static final Logger LOGGER = LogManager.getLogger();

    //@SubscribeEvent
    public void setup() {
        LOGGER.info("TFC Common Setup");

        // Setup methods
        ServerCalendar.setup();
        InteractionManager.setup();
        TFCWorldType.setup();
        TFCLoot.setup();

        DispenserBehaviors.syncSetup();
        //event.enqueueWork(DispenserBehaviors::syncSetup);

        // World gen registry objects
        Registry.register(Registry.CHUNK_GENERATOR, Helpers.identifier("overworld"), TFCChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_SOURCE, Helpers.identifier("overworld"), TFCBiomeProvider.CODEC);
    }

    //TODO: Implement Config
    /*@SubscribeEvent
    public void onConfigReloading(ModConfig.Reloading event)
    {
        TFCConfig.reload();
    }

    @SubscribeEvent
    public void onConfigLoading(ModConfig.Loading event)
    {
        TFCConfig.reload();
    }*/

    @Override
    public void onInitialize() {
        LOGGER.info("TFC Constructor");
        LOGGER.debug("Debug Logging Enabled");

        AutoConfig.register(TFCConfig.class, GsonConfigSerializer::new);

        // Event bus subscribers
        //IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //modEventBus.register(this);
        TFCFluids.register();

        TFCBlocks.register();
        TFCItems.register();
        TFCContainerTypes.register();
        TFCEntities.register();

        TFCRecipeSerializers.register();
        TFCSounds.register();
        TFCTileEntities.register();

        TFCBiomes.register();
        TFCFeatures.register();
        TFCDecorators.register();
        TFCSurfaceBuilders.register();
        TFCCarvers.register();
        TFCBlockStateProviderTypes.register();
        TFCBlockPlacers.register();
        TFCWorldType.register();

        // Init methods
        //TFCConfig.init();
        Networking.register();

        FabricEventHandler.registerEvents();

        CalendarEventHandler.registerCalendarEvents();

        ServerUtil.registerCacher();

        setup();
    }

    public static TFCConfig getConfig()
    {
        return AutoConfig.getConfigHolder(TFCConfig.class).getConfig();
    }
}