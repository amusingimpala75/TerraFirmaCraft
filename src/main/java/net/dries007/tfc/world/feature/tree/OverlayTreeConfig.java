/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Optional;

import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OverlayTreeConfig implements FeatureConfig
{
    public static final Codec<OverlayTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("base").forGetter(c -> c.base),
        Identifier.CODEC.fieldOf("overlay").forGetter(c -> c.overlay),
        Codec.intRange(0, Integer.MAX_VALUE).fieldOf("radius").forGetter(c -> c.radius),
        TrunkConfig.CODEC.optionalFieldOf("trunk").forGetter(c -> c.trunk),
        Codec.floatRange(0, 1).optionalFieldOf("overlay_integrity", 0.5f).forGetter(c -> c.overlayIntegrity)
    ).apply(instance, OverlayTreeConfig::new));

    public final Identifier base;
    public final Identifier overlay;
    public final int radius;
    public final Optional<TrunkConfig> trunk;
    public final float overlayIntegrity;

    public OverlayTreeConfig(Identifier base, Identifier overlay, int radius, Optional<TrunkConfig> trunk, float overlayIntegrity)
    {
        this.base = base;
        this.overlay = overlay;
        this.radius = radius;
        this.trunk = trunk;
        this.overlayIntegrity = overlayIntegrity;
    }
}