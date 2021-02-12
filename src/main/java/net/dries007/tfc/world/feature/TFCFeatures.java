/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.function.Function;

import net.dries007.tfc.util.Helpers;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.*;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.Codecs;
import net.dries007.tfc.world.feature.cave.*;
import net.dries007.tfc.world.feature.coral.TFCCoralClawFeature;
import net.dries007.tfc.world.feature.coral.TFCCoralMushroomFeature;
import net.dries007.tfc.world.feature.coral.TFCCoralTreeFeature;
import net.dries007.tfc.world.feature.plant.*;
import net.dries007.tfc.world.feature.tree.*;
import net.dries007.tfc.world.feature.vein.*;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCFeatures
{
    public static final CaveSpikesFeature CAVE_SPIKE = register("cave_spike", CaveSpikesFeature::new, DefaultFeatureConfig.CODEC);
    public static final LargeCaveSpikesFeature LARGE_CAVE_SPIKE = register("large_cave_spike", LargeCaveSpikesFeature::new, DefaultFeatureConfig.CODEC);
    public static final ThinSpikeFeature THIN_SPIKE = register("thin_spike", ThinSpikeFeature::new, ThinSpikeConfig.CODEC);
    public static final RivuletFeature RIVULET = register("rivulet", RivuletFeature::new, Codecs.LENIENT_BLOCK_STATE_FEATURE_CONFIG);

    public static final ClusterVeinFeature CLUSTER_VEIN = register("cluster_vein", ClusterVeinFeature::new, VeinConfig.CODEC);
    public static final DiscVeinFeature DISC_VEIN = register("disc_vein", DiscVeinFeature::new, DiscVeinConfig.CODEC);
    public static final PipeVeinFeature PIPE_VEIN = register("pipe_vein", PipeVeinFeature::new, PipeVeinConfig.CODEC);

    public static final BouldersFeature BOULDER = register("boulder", BouldersFeature::new, BoulderConfig.CODEC);
    public static final FissureFeature FISSURE = register("fissure", FissureFeature::new, SingleStateFeatureConfig.CODEC);
    public static final LooseRockFeature LOOSE_ROCK = register("loose_rock", LooseRockFeature::new, DefaultFeatureConfig.CODEC);

    public static final RandomPatchWaterLandFeature WATER_LAND_PATCH = register("water_land_patch", RandomPatchWaterLandFeature::new, RandomPatchFeatureConfig.CODEC);
    public static final RandomPatchDensityFeature RANDOM_PATCH_DENSITY = register("random_patch_density", RandomPatchDensityFeature::new, RandomPatchFeatureConfig.CODEC);
    public static final EmergentPatchFeature EMERGENT_PATCH = register("emergent_patch", EmergentPatchFeature::new, RandomPatchFeatureConfig.CODEC);
    public static final RandomPatchWaterFeature WATER_PATCH = register("water_patch", RandomPatchWaterFeature::new, RandomPatchFeatureConfig.CODEC);
    public static final TFCWeepingVinesFeature HANGING_VINES = register("weeping_vines", TFCWeepingVinesFeature::new, TallPlantConfig.CODEC);
    public static final TFCTwistingVinesFeature TWISTING_VINES = register("twisting_vines", TFCTwistingVinesFeature::new, TallPlantConfig.CODEC);
    public static final TFCKelpFeature KELP = register("kelp", TFCKelpFeature::new, TallPlantConfig.CODEC);
    public static final KelpTreeFeature KELP_TREE = register("kelp_tree", KelpTreeFeature::new, Codecs.LENIENT_BLOCK_STATE_FEATURE_CONFIG);
    public static final TFCCoralClawFeature CORAL_CLAW = register("coral_claw", TFCCoralClawFeature::new, DefaultFeatureConfig.CODEC);
    public static final TFCCoralMushroomFeature CORAL_MUSHROOM = register("coral_mushroom", TFCCoralMushroomFeature::new, DefaultFeatureConfig.CODEC);
    public static final TFCCoralTreeFeature CORAL_TREE = register("coral_tree", TFCCoralTreeFeature::new, DefaultFeatureConfig.CODEC);
    public static final CaveVegetationFeature CAVE_VEGETATION = register("cave_vegetation", CaveVegetationFeature::new, CaveVegetationConfig.CODEC);
    public static final CavePatchFeature CAVE_PATCH = register("cave_patch", CavePatchFeature::new, RandomPatchFeatureConfig.CODEC);
    public static final TFCVinesFeature VINES = register("vines", TFCVinesFeature::new, VineConfig.CODEC);
    public static final IceCaveFeature ICE_CAVE = register("ice_cave", IceCaveFeature::new, DefaultFeatureConfig.CODEC);

    public static final ForestFeature FOREST = register("forest", ForestFeature::new, ForestConfig.CODEC);
    public static final OverlayTreeFeature OVERLAY_TREE = register("overlay_tree", OverlayTreeFeature::new, OverlayTreeConfig.CODEC);
    public static final RandomTreeFeature RANDOM_TREE = register("random_tree", RandomTreeFeature::new, RandomTreeConfig.CODEC);
    public static final StackedTreeFeature STACKED_TREE = register("stacked_tree", StackedTreeFeature::new, StackedTreeConfig.CODEC);

    public static final ErosionFeature EROSION = register("erosion", ErosionFeature::new, DefaultFeatureConfig.CODEC);
    public static final IceAndSnowFeature ICE_AND_SNOW = register("ice_and_snow", IceAndSnowFeature::new, DefaultFeatureConfig.CODEC);

    public static final LakeFeature LAKE = register("lake", LakeFeature::new, DefaultFeatureConfig.CODEC);
    public static final FloodFillLakeFeature FLOOD_FILL_LAKE = register("flood_fill_lake", FloodFillLakeFeature::new, FloodFillLakeConfig.CODEC);
    public static final SpringFeature SPRING = register("spring", SpringFeature::new, SpringFeatureConfig.CODEC);

    public static final SoilDiscFeature SOIL_DISC = register("soil_disc", SoilDiscFeature::new, SoilDiscConfig.CODEC);
    public static final TFCIcebergFeature ICEBERG = register("iceberg", TFCIcebergFeature::new, SingleStateFeatureConfig.CODEC);

    public static final DebugMetaballsFeature DEBUG_METABALLS = register("debug_metaballs", DebugMetaballsFeature::new, DefaultFeatureConfig.CODEC);

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, Function<Codec<C>, F> factory, Codec<C> codec)
    {
        return Registry.register(Registry.FEATURE, Helpers.identifier(name), factory.apply(codec));
    }

    public static void register() {}
}