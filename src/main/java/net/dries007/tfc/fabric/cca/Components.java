/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.fabric.cca;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.dries007.tfc.common.capabilities.heat.HeatManager;
import net.dries007.tfc.util.Helpers;
import net.minecraft.item.ItemStack;

public class Components implements
    WorldComponentInitializer,
    ItemComponentInitializer,
    ChunkComponentInitializer
{
    //Item
    public static final ComponentKey<ForgeComponent> FORGE_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(Helpers.identifier("forge"), ForgeComponent.class);
    public static final ComponentKey<HeatComponent> HEAT_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(Helpers.identifier("heat"), HeatComponent.class);

    //World
    public static final ComponentKey<WorldTrackerComponent> WORLD_TRACKING = ComponentRegistryV3.INSTANCE.getOrCreate(Helpers.identifier("world_tracking"), WorldTrackerComponent.class);

    //Chunk
    public static final ComponentKey<ChunkDataChunkComponent> CHUNK_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(Helpers.identifier("chunk_data"), ChunkDataChunkComponent.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry)
    {
        registry.register(WORLD_TRACKING, WorldTrackerWorldComponent.class, WorldTrackerWorldComponent::new);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry)
    {
        registry.registerFor((item) -> true, FORGE_COMPONENT, ForgeItemComponent::new);
        registry.registerFor((item) -> HeatManager.get(new ItemStack(item)) != null, HEAT_COMPONENT, HeatItemComponent::new);
    }

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registry.register(CHUNK_DATA, ChunkDataChunkComponent::new);
    }
}
