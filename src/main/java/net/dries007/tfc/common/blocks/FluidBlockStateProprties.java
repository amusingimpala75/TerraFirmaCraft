package net.dries007.tfc.common.blocks;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.fluid.Fluids;

import java.util.stream.Stream;

public class FluidBlockStateProprties {
    public static final FluidProperty WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER));
    public static final FluidProperty WATER_AND_LAVA = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER, Fluids.LAVA));
    public static final FluidProperty SALT_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, TFCFluids.SALT_WATER));
    public static final FluidProperty FRESH_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER));
}
