/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.dries007.tfc.forgereplacements.fluid.FluidProperties;
//import net.minecraft.fluid.FlowableFluid;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.util.Helpers;

import net.dries007.tfc.forgereplacements.fluid.FlowableFluid;

/**
 * Pairs are {Flowing First, Source Second}
 */
@SuppressWarnings({"unused", "SameParameterValue"})
//TODO: Implement commented out areas
public final class TFCFluids {
    /**
     * Texture locations for both vanilla and TFC fluid textures
     */
    public static final Identifier WATER_STILL = new Identifier("block/water_still");
    public static final Identifier WATER_FLOW = new Identifier("block/water_flow");
    public static final Identifier WATER_OVERLAY = new Identifier("block/water_overlay");

    public static final Identifier MOLTEN_STILL = Helpers.identifier("block/molten_still");
    public static final Identifier MOLTEN_FLOW = Helpers.identifier("block/molten_flow");

    /**
     * A mask for fluid color - most fluids should be using this
     */
    public static final int ALPHA_MASK = 0xFF000000;

    /**
     * Fluid instances
     */
    public static final Map<Metal.Default, FluidPair<FlowableFluid>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal -> register(
        "metal/" + metal.name().toLowerCase(),
        "metal/flowing_" + metal.name().toLowerCase(),
        properties -> properties.setBlock(new Lazy<>(() -> TFCBlocks.METAL_FLUIDS.get(metal))).setBucket(new Lazy<>(() -> TFCItems.METAL_FLUID_BUCKETS.get(metal))).setBlastResistance(100),
        //FluidAttributes.builder(MOLTEN_STILL, MOLTEN_FLOW)
        //    .translationKey("fluid.tfc.metal." + metal.name().toLowerCase())
        //    .color(ALPHA_MASK | metal.getColor())
        //    .rarity(metal.getRarity())
        //    .luminosity(15)
        //    .density(3000)
        //    .viscosity(6000)
        //    .temperature(1300)
        //    .sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA),
        MoltenFluid.Source::new,
        MoltenFluid.Flowing::new,
        true
    ));

    public static final FluidPair<FlowableFluid> SALT_WATER = register(
        "salt_water",
        "flowing_salt_water",
        properties -> properties.setBlock(new Lazy<>(() -> TFCBlocks.SALT_WATER)).setBucket(new Lazy<>(() -> TFCItems.SALT_WATER_BUCKET)).setInfinite(true),
        //builder(WATER_STILL, WATER_FLOW, SaltWaterAttributes::new)
        //    .translationKey("fluid.tfc.salt_water")
        //    .overlay(WATER_OVERLAY)
        //    .color(ALPHA_MASK | 0x3F76E4)
        //    .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new,
        false
    );

    public static final FluidPair<FlowableFluid> SPRING_WATER = register(
        "spring_water",
        "flowing_spring_water",
        properties -> properties.setBlock(new Lazy<>(() -> TFCBlocks.SPRING_WATER)).setBucket(new Lazy<>(() ->TFCItems.SPRING_WATER_BUCKET)),
        //FluidAttributes.builder(WATER_STILL, WATER_FLOW)
        //    .translationKey("fluid.tfc.spring_water")
        //    .color(ALPHA_MASK | 0x4ECBD7)
        //    .overlay(WATER_OVERLAY)
        //    .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY),
        MixingFluid.Source::new,
        MixingFluid.Flowing::new,
        false
    );

    /**
     * Registration helper for fluids and this stupid API
     *
     * @param sourceName  The source fluid
     * @param flowingName The flowing fluid
     * @param builder     Fluid properties
     * @return The registered fluid
     */
    private static FluidPair<FlowableFluid> register(String sourceName, String flowingName, Consumer<FluidProperties> builder, boolean burning)
    {
        return register(sourceName, flowingName, builder, net.dries007.tfc.forgereplacements.fluid.FlowableFluid.Still::new, net.dries007.tfc.forgereplacements.fluid.FlowableFluid.Flowing::new, burning);
    }

    @SuppressWarnings("unchecked")
    private static <F extends net.dries007.tfc.forgereplacements.fluid.FlowableFluid> FluidPair<F> register(String sourceName, String flowingName, Consumer<FluidProperties> builder, Supplier<F> sourceFactory, Supplier<F> flowingFactory, boolean burning)
    {
        // The properties needs a reference to both source and flowing
        // In addition, the properties builder cannot be invoked statically, as it has hard references to registry objects, which may not be populated based on class load order - it must be invoked at registration time.
        // So, first we prepare the source and flowing registry objects, referring to the properties box (which will be opened during registration, which is ok)
        // Then, we populate the properties box lazily, (since it's a mutable lazy), so the properties inside are only constructed when the box is opened (again, during registration)
        final Mutable<Lazy<FluidProperties>> propertiesBox = new MutableObject<>();

        //final F source = (F) register(sourceName, () -> sourceFactory.apply(propertiesBox.getValue().get()));
        //final F flowing = (F) register(flowingName, () -> flowingFactory.apply(propertiesBox.getValue().get()));

        final F source = sourceFactory.get();
        final F flowing = flowingFactory.get();

        propertiesBox.setValue(new Lazy<>(() -> {
            FluidProperties lazyProperties = new FluidProperties(flowing, source);
            builder.accept(lazyProperties);
            return lazyProperties;
        }));

        source.init(propertiesBox.getValue().get());
        flowing.init(propertiesBox.getValue().get());

        register(sourceName, () -> source);
        register(flowingName, () -> flowing);

        return new FluidPair<>(flowing, source);
    }

    private static Fluid register(String name, Supplier<Fluid> factory) {
        return Registry.register(Registry.FLUID, Helpers.identifier(name), factory.get());
    }

    /*
     * Helper for the stupid protected constructor on {@link FluidAttributes.Builder}
     */
    //private static FluidAttributes.Builder builder(Identifier stillTexture, Identifier flowingTexture, BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory) {
    //    return new FluidAttributes.Builder(stillTexture, flowingTexture, factory) {};
    //}

    /**
     * This exists for simpler labels and type parameters
     */
    public static class FluidPair<F extends net.minecraft.fluid.FlowableFluid> extends Pair<F, F> {
        private FluidPair(F first, F second) {
            super(first, second);
        }

        public F getFlowing() {
            return getFirst();
        }

        public F getSource() {
            return getSecond();
        }

        public BlockState getSourceBlock() {
            return getSource().getDefaultState().getBlockState();
        }
    }

    public static void register() {}
}