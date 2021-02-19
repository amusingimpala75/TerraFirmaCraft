/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.dries007.tfc.fabric.cca.ChunkDataChunkComponent;
import net.dries007.tfc.fabric.cca.Components;
import net.dries007.tfc.fabric.event.DebugRenderCallback;
import net.dries007.tfc.mixin.fabric.client.gui.HandledScreenAccessor;
import net.dries007.tfc.mixin.fabric.client.gui.ScreenAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ColorResolver;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.client.world.ClientWorldAccessor;
import net.dries007.tfc.mixin.client.world.DimensionRenderInfoAccessor;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.minecraft.util.Formatting.*;

//@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
//TODO FIX
public class ClientFabricEventHandler
{
    /*@SubscribeEvent
    public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        List<String> list = event.getRight();
        if (mc.world != null && mc.options.debugEnabled) // todo: config
        {
            //noinspection ConstantConditions
            BlockPos pos = new BlockPos(mc.getCameraEntity().getX(), mc.getCameraEntity().getBoundingBox().minY, mc.getCameraEntity().getZ());
            if (mc.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4))
            {
                list.add("");
                list.add(AQUA + TerraFirmaCraft.MOD_NAME);

                // Always add calendar info
                list.add(I18n.translate("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getString());
                list.add(I18n.translate("tfc.tooltip.debug_times", Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), mc.getCameraEntity().world.getTimeOfDay() % ICalendar.TICKS_IN_DAY));

                ChunkDataChunkComponent data = ChunkDataChunkComponent.get(mc.world, pos);
                if (data.getStatus().isAtLeast(ChunkDataChunkComponent.Status.CLIENT))
                {
                    list.add(GRAY + I18n.translate("tfc.tooltip.f3_average_temperature", WHITE + String.format("%.1f", data.getAverageTemp(pos))));
                    list.add(GRAY + I18n.translate("tfc.tooltip.f3_temperature", WHITE + String.format("%.1f", Climate.calculateTemperature(pos, data.getAverageTemp(pos), Calendars.CLIENT))));
                    list.add(GRAY + I18n.translate("tfc.tooltip.f3_rainfall", WHITE + String.format("%.1f", data.getRainfall(pos))));
                    list.add(GRAY + I18n.translate("tfc.tooltip.f3_forest_type") + WHITE + I18n.translate(Helpers.getEnumTranslationKey(data.getForestType())));
                    list.add(GRAY + I18n.translate("tfc.tooltip.f3_forest_properties",
                        WHITE + String.format("%.1f%%", 100 * data.getForestDensity()) + GRAY,
                        WHITE + String.format("%.1f%%", 100 * data.getForestWeirdness()) + GRAY));
                }
                else
                {
                    list.add(GRAY + I18n.translate("tfc.tooltip.f3_invalid_chunk_data"));
                }
            }
        }
    }*/

    public static void registerDebugEntries()
    {
        DebugRenderCallback.RIGHT.register(entries -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world != null && mc.options.debugEnabled) // todo: config
            {
                BlockPos pos = new BlockPos(mc.getCameraEntity().getX(), mc.getCameraEntity().getBoundingBox().minY, mc.getCameraEntity().getZ());
                if (mc.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4))
                {
                    entries.add("");
                    entries.add(AQUA + TerraFirmaCraft.MOD_NAME);

                    // Always add calendar info
                    entries.add(I18n.translate("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getString());
                    entries.add(I18n.translate("tfc.tooltip.debug_times", Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), mc.getCameraEntity().world.getTimeOfDay() % ICalendar.TICKS_IN_DAY));

                    ChunkDataChunkComponent data = ChunkDataChunkComponent.get(mc.world, pos);
                    if (data.getStatus().isAtLeast(ChunkDataChunkComponent.Status.CLIENT))
                    {
                        entries.add(GRAY + I18n.translate("tfc.tooltip.f3_average_temperature", WHITE + String.format("%.1f", data.getAverageTemp(pos))));
                        entries.add(GRAY + I18n.translate("tfc.tooltip.f3_temperature", WHITE + String.format("%.1f", Climate.calculateTemperature(pos, data.getAverageTemp(pos), Calendars.CLIENT))));
                        entries.add(GRAY + I18n.translate("tfc.tooltip.f3_rainfall", WHITE + String.format("%.1f", data.getRainfall(pos))));
                        entries.add(GRAY + I18n.translate("tfc.tooltip.f3_forest_type") + WHITE + I18n.translate(Helpers.getEnumTranslationKey(data.getForestType())));
                        entries.add(GRAY + I18n.translate("tfc.tooltip.f3_forest_properties",
                            WHITE + String.format("%.1f%%", 100 * data.getForestDensity()) + GRAY,
                            WHITE + String.format("%.1f%%", 100 * data.getForestWeirdness()) + GRAY));
                    }
                    else
                    {
                        entries.add(GRAY + I18n.translate("tfc.tooltip.f3_invalid_chunk_data"));
                    }
                }
            }
        });
    }

    /*@SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        PlayerEntity player = event.getPlayer();
        List<Text> text = event.getToolTip();
        if (!stack.isEmpty() && player != null)
        {
            MetalItemManager.addTooltipInfo(stack, text);
            //stack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.addHeatInfo(stack, text));
            Components.HEAT_COMPONENT.maybeGet(stack).ifPresent((comp) -> comp.addHeatInfo(stack, text));
        }
    }*/

    public static void registerItemTooltip()
    {
        ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
            if (!stack.isEmpty())
            {
                MetalItemManager.addTooltipInfo(stack, tooltip);
                Components.HEAT_COMPONENT.maybeGet(stack).ifPresent((comp) -> comp.addHeatInfo(stack, tooltip));
            }
        });
    }

    /*@SubscribeEvent
    public static void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (event.getGui() instanceof InventoryScreen && player != null && !player.isCreative())
        {
            InventoryScreen screen = (InventoryScreen) event.getGui();
            int guiLeft = ((InventoryScreen) event.getGui()).getGuiLeft();
            int guiTop = ((InventoryScreen) event.getGui()).getGuiTop();

            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 4, 20 + 3, 22, 128 + 20, 0, 1, 3, 0, 0, button -> {}).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE).setRecipeBookCallback(screen));
        }
    }*/

    public static void registerInventoryScreenAdditions()
    {
        ScreenEvents.AFTER_INIT.register((client, screen, i, i1) -> {
            if (screen instanceof InventoryScreen && MinecraftClient.getInstance().player != null && !MinecraftClient.getInstance().player.isCreative())
            {
                int guiLeft = ((HandledScreenAccessor) screen).accessor$getX();
                int guiTop = ((HandledScreenAccessor) screen).accessor$getY();

                ((ScreenAccessor)screen).call$addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 4, 20 + 3, 22, 128 + 20, 0, 1, 3, 0, 0, button -> {}).setRecipeBookCallback((InventoryScreen) screen));
                ((ScreenAccessor) screen).call$addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR).setRecipeBookCallback((InventoryScreen) screen));
                ((ScreenAccessor) screen).call$addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION).setRecipeBookCallback((InventoryScreen) screen));
                ((ScreenAccessor) screen).call$addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE).setRecipeBookCallback((InventoryScreen) screen));
            }
        });
    }

    //@SubscribeEvent
    public static void onClientWorldLoad(World world2)
    {
        if (world2 instanceof ClientWorld)
        {
            final ClientWorld world = (ClientWorld) world2;

            // Add our custom tints to the color resolver caches
            final Object2ObjectArrayMap<ColorResolver, BiomeColorCache> colorCaches = ((ClientWorldAccessor) world).getTintCaches();

            colorCaches.putIfAbsent(TFCColors.FRESH_WATER, new BiomeColorCache());
            colorCaches.putIfAbsent(TFCColors.SALT_WATER, new BiomeColorCache());

            // Update cloud height
            final float cloudHeight = TerraFirmaCraft.getConfig().clientConfig.assumeTFCWorld ? 210 : 160;
            ((DimensionRenderInfoAccessor) DimensionRenderInfoAccessor.accessor$Effects().get(DimensionType.OVERWORLD_ID)).accessor$setCloudLevel(cloudHeight);
        }
    }

    /*@SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        World world = MinecraftClient.getInstance().world;
        if (event.phase == TickEvent.Phase.END && world != null && !MinecraftClient.getInstance().isPaused())
        {
            Calendars.CLIENT.onClientTick();
            ClimateRenderCache.INSTANCE.onClientTick();
        }
    }*/

    public static void registerClientTick()
    {
        ClientTickEvents.END_CLIENT_TICK.register(t -> {
            if (t.world != null && !MinecraftClient.getInstance().isPaused())
            {
                Calendars.CLIENT.onClientTick();
                ClimateRenderCache.INSTANCE.onClientTick();
            }
        });
    }

    public static void registerClientEvents()
    {
        registerItemTooltip();
        registerClientTick();
        registerDebugEntries();
        registerInventoryScreenAdditions();
    }
}