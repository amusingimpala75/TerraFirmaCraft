/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ParentedSurfaceBuilderConfig extends TernarySurfaceConfig
{
    public static final Codec<ParentedSurfaceBuilderConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ConfiguredSurfaceBuilder.CODEC.fieldOf("parent").forGetter(c -> c.parent)
    ).apply(instance, ParentedSurfaceBuilderConfig::new));

    private final ConfiguredSurfaceBuilder<?> parent;

    public ParentedSurfaceBuilderConfig(ConfiguredSurfaceBuilder<?> parent)
    {
        super(Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState());
        this.parent = parent;
    }

    public ConfiguredSurfaceBuilder<?> getParent()
    {
        return parent;
    }
}
