/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

/**
 * Central point for all configuration options
 * - Common is used for options which need to be world-agnostic, or are independent of side
 * - Server is used for generic mechanics options, stuff which is synchronized but server priority, etc.
 * - Client is used for purely graphical or client side only options
 */
@Config(name = "TerraFirmaCraft")
public final class TFCConfig implements ConfigData
{
    /*public static final CommonConfig COMMON = register(ModConfig.Type.COMMON, CommonConfig::new);
    public static final ClientConfig CLIENT = register(ModConfig.Type.CLIENT, ClientConfig::new);
    public static final ServerConfig SERVER = register(ModConfig.Type.SERVER, ServerConfig::new);

    public static void init() {}

    public static void reload()
    {
        COMMON.reload();
        CLIENT.reload();
        SERVER.reload();
    }

    private static <C> C register(ModConfig.Type type, Function<ForgeConfigSpec.Builder, C> factory)
    {
        Pair<C, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(factory);
        ModLoadingContext.get().registerConfig(type, specPair.getRight());
        return specPair.getLeft();
    }*/

    @ConfigEntry.Gui.CollapsibleObject
    public Server serverConfig = new Server();
    @ConfigEntry.Gui.CollapsibleObject
    public Client clientConfig = new Client();
    @ConfigEntry.Gui.CollapsibleObject
    public Common commonConfig = new Common();

    public static class Server
    {

        @ConfigEntry.Gui.CollapsibleObject
        public General general = new General();
        @ConfigEntry.Gui.CollapsibleObject
        public Player player = new Player();
        @ConfigEntry.Gui.CollapsibleObject
        public Climate climate = new Climate();
        @ConfigEntry.Gui.CollapsibleObject
        public Blocks blocks = new Blocks();
        @ConfigEntry.Gui.CollapsibleObject
        public Mechanics mechanics = new Mechanics();

        public static class General
        {
            public boolean enableNetherPortals = false;
        }

        public static class Player
        {
            public boolean enableVanillaNaturalRegeneration = false;
        }

        public static class Climate
        {
            @ConfigEntry.BoundedDiscrete(min = 1000, max = 1000000)
            public int temperatureScale = 20000;

            @ConfigEntry.BoundedDiscrete(min = 1000, max = 1000000)
            public int rainfallScale = 20000;
        }

        public static class Blocks
        {
            @ConfigEntry.Gui.CollapsibleObject
            public Farmland farmland = new Farmland();
            @ConfigEntry.Gui.CollapsibleObject
            public GrassPath grassPath = new GrassPath();
            @ConfigEntry.Gui.CollapsibleObject
            public Snow snow = new Snow();
            @ConfigEntry.Gui.CollapsibleObject
            public Ice ice = new Ice();
            @ConfigEntry.Gui.CollapsibleObject
            public Plants plants = new Plants();
            @ConfigEntry.Gui.CollapsibleObject
            public Leaves leaves = new Leaves();
            @ConfigEntry.Gui.CollapsibleObject
            public Cobblestone cobble = new Cobblestone();

            public static class Farmland
            {
                public boolean enableFarmlandCreation = true;
            }

            public static class GrassPath
            {
                public boolean enableGrassPathCreation = true;
            }

            public static class Snow
            {
                public boolean enableSnowAffectedByTemperature = true;

                public boolean enableSnowSlowEntities = true;
            }

            public static class Ice
            {
                public boolean enableIceAffectedByTemperature = true;
            }

            public static class Plants
            {
                @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
                private int plantGrowthChance = 50;

                public double getPlantGrowthChance()
                {
                    return ((double)plantGrowthChance)/100;
                }
            }

            public static class Leaves
            {
                public boolean enableLeavesSlowEntities = true;
            }

            public static class Cobblestone
            {
                public boolean enableMossyRockSpreading = true;

                @ConfigEntry.BoundedDiscrete(min = 0, max = Integer.MAX_VALUE)
                public int mossyRockSpreadRate = 20;
            }
        }

        public static class Mechanics
        {
            @ConfigEntry.Gui.CollapsibleObject
            public Heat heat = new Heat();
            @ConfigEntry.Gui.CollapsibleObject
            public Collapses collapses = new Collapses();

            public static class Heat
            {
                public double itemHeatingModifier = 1;
            }

            public static class Collapses
            {
                public boolean enableBlockCollapsing = true;
                public boolean enableExplosionCollapsing = true;
                public boolean enableBlockLandslides = true;

                @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
                private int collapseTriggerChance = 10;
                @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
                private int collapsePropagateChance = 55;
                @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
                private int collapseExplosionPropagateChance = 30;
                @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
                public int collapseMinRadius = 3;
                @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
                public int collapseRadiusVariance = 16;

                public double getCollapseTriggerChance()
                {
                    return ((double) collapseTriggerChance)/100;
                }

                public double getCollapsePropagateChance()
                {
                    return ((double) collapsePropagateChance)/100;
                }

                public double getCollapseExplosionPropagateChance()
                {
                    return ((double) collapseExplosionPropagateChance)/100;
                }
            }
        }
    }

    public static class Client
    {
        public boolean ignoreExperimentalWorldGenWarning = true;
        public boolean assumeTFCWorld = true;
    }

    public static class Common
    {
        @ConfigEntry.Gui.CollapsibleObject
        public General general = new General();

        public static class General
        {
            @ConfigEntry.BoundedDiscrete(min = 1, max = Integer.MAX_VALUE)
            public int defaultMonthLength = 8;

            public boolean enableDevTweaks = true;
            public boolean setTFCWorldTypeAsDefault = true;
        }
    }
}