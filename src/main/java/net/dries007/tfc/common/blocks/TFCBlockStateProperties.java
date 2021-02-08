/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.stream.Stream;

import net.minecraft.fluid.Fluids;

import net.dries007.tfc.common.blocks.plant.ITallPlant;
import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.calendar.Season;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

/**
 * @see net.minecraft.state.property.Properties
 */
public class TFCBlockStateProperties
{
    public static final BooleanProperty SUPPORTED = BooleanProperty.of("supported");

    public static final EnumProperty<Season> SEASON = EnumProperty.of("season", Season.class);
    public static final EnumProperty<Season> SEASON_NO_SPRING = EnumProperty.of("season", Season.class, Season.SUMMER, Season.FALL, Season.WINTER);

    public static final IntProperty DISTANCE_7 = Properties.DISTANCE_1_7;
    public static final IntProperty DISTANCE_8 = IntProperty.of("distance", 1, 8);
    public static final IntProperty DISTANCE_9 = IntProperty.of("distance", 1, 9);
    public static final IntProperty DISTANCE_10 = IntProperty.of("distance", 1, 10);

    public static final IntProperty[] DISTANCES = {DISTANCE_7, DISTANCE_8, DISTANCE_9, DISTANCE_10};

    public static final FluidProperty WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER));
    public static final FluidProperty WATER_AND_LAVA = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER, Fluids.LAVA));
    public static final FluidProperty SALT_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, TFCFluids.SALT_WATER));
    public static final FluidProperty FRESH_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER));

    public static final IntProperty COUNT_1_3 = IntProperty.of("count", 1, 3);

    public static final IntProperty STAGE_1 = Properties.STAGE;
    public static final IntProperty STAGE_2 = IntProperty.of("stage", 0, 2);
    public static final IntProperty STAGE_3 = IntProperty.of("stage", 0, 3);
    public static final IntProperty STAGE_4 = IntProperty.of("stage", 0, 4);
    public static final IntProperty STAGE_5 = IntProperty.of("stage", 0, 5);
    public static final IntProperty STAGE_6 = IntProperty.of("stage", 0, 6);
    public static final IntProperty STAGE_7 = IntProperty.of("stage", 0, 7);
    public static final IntProperty STAGE_8 = IntProperty.of("stage", 0, 8);
    public static final IntProperty STAGE_9 = IntProperty.of("stage", 0, 9);
    public static final IntProperty STAGE_10 = IntProperty.of("stage", 0, 10);
    public static final IntProperty STAGE_11 = IntProperty.of("stage", 0, 11);
    public static final IntProperty STAGE_12 = IntProperty.of("stage", 0, 12);

    public static final IntProperty[] STAGES = {STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5, STAGE_6, STAGE_7, STAGE_8, STAGE_9, STAGE_10, STAGE_11, STAGE_12};

    public static final IntProperty AGE_3 = IntProperty.of("age", 0, 3);

    public static final EnumProperty<ITallPlant.Part> TALL_PLANT_PART = EnumProperty.of("part", ITallPlant.Part.class);
    public static final EnumProperty<RockSpikeBlock.Part> ROCK_SPIKE_PART = EnumProperty.of("part", RockSpikeBlock.Part.class);

    public static final BooleanProperty TIP = BooleanProperty.of("tip");
}