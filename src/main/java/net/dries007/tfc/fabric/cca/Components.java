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
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;

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
        registry.register(CHUNK_DATA, chunk -> {
            ChunkDataChunkComponent data;
            if (chunk instanceof WorldChunk || (chunk instanceof ReadOnlyChunk && ((ReadOnlyChunk)chunk).getWrappedChunk() != null))
            {
                World world;
                ChunkPos chunkPos = chunk.getPos();
                if (chunk instanceof WorldChunk)
                {
                    world = ((WorldChunk) chunk).getWorld();
                }
                else
                {
                    world = ((ReadOnlyChunk) chunk).getWrappedChunk().getWorld();
                }
                if (!Helpers.isClientSide(world))
                {
                    // Chunk was created on server thread.
                    // 1. If this was due to world gen, it won't have any cap data. This is where we clear the world gen cache and attach it to the chunk
                    // 2. If this was due to chunk loading, the caps will be deserialized from NBT after this event is posted. Attach empty data here
                    data = ChunkDataCache.WORLD_GEN.remove(chunkPos);
                    if (data == null)
                    {
                        data = new ChunkDataChunkComponent(chunkPos);
                    }
                }
                else
                {
                    // This may happen before or after the chunk is watched and synced to client
                    // Default to using the cache. If later the sync packet arrives it will update the same instance in the chunk capability and cache
                    data = ChunkDataCache.CLIENT.getOrCreate(chunkPos);
                }
                return data;
            }
            else
            {
                return ChunkDataChunkComponent.EMPTY;
            }
        });
    }
}
