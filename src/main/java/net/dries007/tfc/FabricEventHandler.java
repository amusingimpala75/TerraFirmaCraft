/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.util.Optional;
import java.util.Random;

import net.dries007.tfc.fabric.cca.ChunkDataChunkComponent;
import net.dries007.tfc.fabric.cca.Components;
import net.dries007.tfc.fabric.event.FluidReactCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.heat.HeatManager;
import net.dries007.tfc.common.command.TFCCommands;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.common.types.MetalManager;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.network.ChunkUnwatchPacket;
import net.dries007.tfc.util.CacheInvalidationListener;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.support.SupportManager;
import net.dries007.tfc.world.biome.ITFCBiomeProvider;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
import net.dries007.tfc.world.chunkdata.ITFCChunkGenerator;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FabricEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Duplicates logic from @link net.minecraft.server.MinecraftServer#setupSpawn(ServerWorld, ServerWorldProperties, boolean, boolean, boolean) as that version only asks the dimension for the sea level...
     */
    /*@SubscribeEvent
    public static void onCreateWorldSpawn(WorldEvent.CreateSpawnPosition event)
    {
        // Forge why you make everything `IWorld`, it's literally only called from `ServerWorld`...
        if (event.getWorld() instanceof ServerWorld)
        {
            final ServerWorld world = (ServerWorld) event.getWorld();
            final IServerWorldInfo settings = event.getSettings();
            final ChunkGenerator generator = world.getChunkSource().getGenerator();
            if (generator instanceof ITFCChunkGenerator)
            {
                final ITFCBiomeProvider biomeProvider = ((ITFCChunkGenerator) generator).getBiomeSource();
                final Random random = new Random(world.getSeed());
                final int spawnDistance = biomeProvider.getSpawnDistance();

                BlockPos pos = biomeProvider.findBiomeIgnoreClimate(biomeProvider.getSpawnCenterX(), generator.getSeaLevel(), biomeProvider.getSpawnCenterZ(), spawnDistance, spawnDistance / 256, biome -> biome.getMobSettings().playerSpawnFriendly(), random);
                ChunkPos chunkPos;
                if (pos == null)
                {
                    LOGGER.warn("Unable to find spawn biome!");
                    pos = new BlockPos(0, generator.getSeaLevel(), 0);
                }
                chunkPos = new ChunkPos(pos);

                settings.setSpawn(chunkPos.getWorldPosition().offset(8, generator.getSpawnHeight(), 8), 0.0F);
                boolean foundExactSpawn = false;
                int x = 0, z = 0;
                int xStep = 0;
                int zStep = -1;

                for (int tries = 0; tries < 1024; ++tries)
                {
                    if (x > -16 && x <= 16 && z > -16 && z <= 16)
                    {
                        BlockPos spawnPos = Helpers.findValidSpawnLocation(world, new ChunkPos(chunkPos.x + x, chunkPos.z + z));
                        if (spawnPos != null)
                        {
                            settings.setSpawn(spawnPos, 0);
                            foundExactSpawn = true;
                            break;
                        }
                    }

                    if ((x == z) || (x < 0 && x == -z) || (x > 0 && x == 1 - z))
                    {
                        int temp = xStep;
                        xStep = -zStep;
                        zStep = temp;
                    }

                    x += xStep;
                    z += zStep;
                }

                if (!foundExactSpawn)
                {
                    LOGGER.warn("Unable to find a suitable spawn location!");
                }

                if (world.getServer().getWorldData().worldGenSettings().generateBonusChest())
                {
                    LOGGER.warn("No bonus chest for you, you cheaty cheater!");
                }

                event.setCanceled(true);
            }
        }
    }*/

    public static void modifySpawn(ServerWorld world, ServerWorldProperties properties, CallbackInfo ci)
    {
        // Forge why you make everything `IWorld`, it's literally only called from `ServerWorld`...
        if (world != null)
        {
            final ChunkGenerator generator = world.getChunkManager().getChunkGenerator();
            if (generator instanceof ITFCChunkGenerator)
            {
                final ITFCBiomeProvider biomeProvider = ((ITFCChunkGenerator) generator).getBiomeSource();
                final Random random = new Random(world.getSeed());
                final int spawnDistance = biomeProvider.getSpawnDistance();

                BlockPos pos = biomeProvider.findBiomeIgnoreClimate(biomeProvider.getSpawnCenterX(), generator.getSeaLevel(), biomeProvider.getSpawnCenterZ(), spawnDistance, spawnDistance / 256, biome -> biome.getSpawnSettings().isPlayerSpawnFriendly(), random);
                ChunkPos chunkPos;
                if (pos == null)
                {
                    LOGGER.warn("Unable to find spawn biome!");
                    pos = new BlockPos(0, generator.getSeaLevel(), 0);
                }
                chunkPos = new ChunkPos(pos);

                properties.setSpawnPos(chunkPos.getStartPos().add(8, generator.getSpawnHeight(), 8), 0.0F);
                boolean foundExactSpawn = false;
                int x = 0, z = 0;
                int xStep = 0;
                int zStep = -1;

                for (int tries = 0; tries < 1024; ++tries)
                {
                    if (x > -16 && x <= 16 && z > -16 && z <= 16)
                    {
                        BlockPos spawnPos = Helpers.findValidSpawnLocation(world, new ChunkPos(chunkPos.x + x, chunkPos.z + z));
                        if (spawnPos != null)
                        {
                            properties.setSpawnPos(spawnPos, 0);
                            foundExactSpawn = true;
                            break;
                        }
                    }

                    if ((x == z) || (x < 0 && x == -z) || (x > 0 && x == 1 - z))
                    {
                        int temp = xStep;
                        xStep = -zStep;
                        zStep = temp;
                    }

                    x += xStep;
                    z += zStep;
                }

                if (!foundExactSpawn)
                {
                    LOGGER.warn("Unable to find a suitable spawn location!");
                }

                if (world.getServer().getSaveProperties().getGeneratorOptions().hasBonusChest())
                {
                    LOGGER.warn("No bonus chest for you, you cheaty cheater!");
                }

                ci.cancel();
            }
        }
    }

    //TODO: Check the client vs server attachment of components
    /*@SubscribeEvent
    public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<Chunk> event)
    {
        if (!event.getObject().isEmpty())
        {
            World world = event.getObject().getLevel();
            ChunkPos chunkPos = event.getObject().getPos();
            ChunkDataChunkComponent data;
            if (!Helpers.isClientSide(world))
            {
                // Chunk was created on server thread.
                // 1. If this was due to world gen, it won't have any cap data. This is where we clear the world gen cache and attach it to the chunk
                // 2. If this was due to chunk loading, the caps will be deserialized from NBT after this event is posted. Attach empty data here
                data = ChunkDataCache.WORLD_GEN.remove(chunkPos);
                if (data == null)
                {
                    data = new ChunkData(chunkPos);
                }
            }
            else
            {
                // This may happen before or after the chunk is watched and synced to client
                // Default to using the cache. If later the sync packet arrives it will update the same instance in the chunk capability and cache
                data = ChunkDataCache.CLIENT.getOrCreate(chunkPos);
            }
            event.addCapability(ChunkDataCapability.KEY, data);
        }
    }*/

    /*@SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event)
    {
        // Send an update packet to the client when watching the chunk
        ChunkPos pos = event.getPos();
        ChunkDataChunkComponent chunkData = ChunkDataChunkComponent.get(event.getWorld(), pos);
        if (chunkData.getStatus() != ChunkDataChunkComponent.Status.EMPTY)
        {
            //PacketHandler.send(PacketDistributor.PLAYER.with(event::getPlayer), chunkData.getUpdatePacket());
            for (ServerPlayerEntity e : event.getPlayers())
            {
                chunkData.getUpdatePacket().send(e);
            }
        }
        else
        {
            // Chunk does not exist yet but it's queue'd for watch. Queue an update packet to be sent on chunk load
            ChunkDataCache.WATCH_QUEUE.enqueueUnloadedChunk(pos, event.getPlayer());
        }
    }*/

    /*@SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event)
    {
        if (!Helpers.isClientSide(event.getWorld()) && !(event.getChunk() instanceof EmptyChunk))
        {
            ChunkPos pos = event.getChunk().getPos();
            ChunkDataChunkComponent.getCapability(event.getChunk()).ifPresent(data -> {
                ChunkDataCache.SERVER.update(pos, data);
                ChunkDataCache.WATCH_QUEUE.dequeueLoadedChunk(pos, data);
            });
        }
    }*/

    //TODO: Especially check watch/unwatch events
    //TODO: FIXFIXIFIXFIX
    /*public static void registerChunkLoadEvents()
    {
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {

            chunkLoad(chunk);
            chunkWatch(chunk, world);

        });
    }*/

    public static void chunkLoad(WorldChunk chunk)
    {
        if (!(chunk instanceof EmptyChunk))
        {
            ChunkPos pos = chunk.getPos();
            ChunkDataChunkComponent.getCapability(chunk).ifPresent(data -> {
                ChunkDataCache.SERVER.update(pos, data);
                ChunkDataCache.WATCH_QUEUE.dequeueLoadedChunk(pos, data);
            });
        }
    }

    public static void chunkWatch(ServerWorld world, ServerPlayerEntity player, ChunkPos pos)
    {
        // Send an update packet to the client when watching the chunk
        ChunkDataChunkComponent chunkData = ChunkDataChunkComponent.get(world, pos);
        if (chunkData.getStatus() != ChunkDataChunkComponent.Status.EMPTY)
        {
            //PacketHandler.send(PacketDistributor.PLAYER.with(event::getPlayer), chunkData.getUpdatePacket());
            chunkData.getUpdatePacket().send(player);
        }
        else
        {
            // Chunk does not exist yet but it's queue'd for watch. Queue an update packet to be sent on chunk load
            ChunkDataCache.WATCH_QUEUE.enqueueUnloadedChunk(pos, player);
        }
    }

    /*@SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        // Clear server side chunk data cache
        if (!Helpers.isClientSide(event.getWorld()) && !(event.getChunk() instanceof EmptyChunk))
        {
            ChunkDataCache.SERVER.remove(event.getChunk().getPos());
        }
    }*/

    //Todo: Especially check watch/unwatch events
    //TODO: FIXFIXFIXFIXFIX
    /*public static void registerChunkUnloadEvents()
    {
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {

            chunkUnload(chunk);
            chunkUnwatch(chunk, world);
        });
    }*/

    public static void chunkUnload(WorldChunk chunk)
    {
        // Clear server side chunk data cache
        if (!(chunk instanceof EmptyChunk))
        {
            ChunkDataCache.SERVER.remove(chunk.getPos());
        }
    }

    public static void chunkUnwatch(ServerWorld world, ServerPlayerEntity player, ChunkPos pos)
    {
        // Send an update packet to the client when un-watching the chunk
        //PacketHandler.send(PacketDistributor.PLAYER.with(event::getPlayer), new ChunkUnwatchPacket(pos));
        if (ChunkDataCache.WATCH_QUEUE.dequeueChunk(pos, player))
        {
            new ChunkUnwatchPacket(pos).send(player);
        }
    }

    /*@SubscribeEvent
    public static void onChunkUnwatch(ChunkWatchEvent.UnWatch event)
    {
        // Send an update packet to the client when un-watching the chunk
        ChunkPos pos = event.getPos();
        //PacketHandler.send(PacketDistributor.PLAYER.with(event::getPlayer), new ChunkUnwatchPacket(pos));
        for (ServerPlayerEntity e : event.getPlayers())
        {
            new ChunkUnwatchPacket(pos).send(e);
        }
        ChunkDataCache.WATCH_QUEUE.dequeueChunk(pos, event.getPlayer());
    }*/

    /*@SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event)
    {
        // Resource reload listeners
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) event.getDataPackRegistries().getResourceManager();
        resourceManager.registerReloadListener(RockManager.INSTANCE);
        resourceManager.registerReloadListener(MetalManager.INSTANCE);
        resourceManager.registerReloadListener(MetalItemManager.INSTANCE);
        resourceManager.registerReloadListener(SupportManager.INSTANCE);
        resourceManager.registerReloadListener(HeatManager.INSTANCE);

        resourceManager.registerReloadListener(CacheInvalidationListener.INSTANCE);
    }*/

    public static void registerReloadListeners()
    {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(RockManager.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MetalManager.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MetalItemManager.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SupportManager.INSTANCE);
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(HeatManager.INSTANCE);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(CacheInvalidationListener.INSTANCE);

    }

    /*@SubscribeEvent
    public static void beforeServerStart(FMLServerAboutToStartEvent event)
    {
        CacheInvalidationListener.INSTANCE.invalidateAll();
    }*/

    public static void registerServerStartEvent()
    {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> CacheInvalidationListener.INSTANCE.invalidateAll());
    }

    /*@SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        LOGGER.debug("Registering TFC Commands");
        TFCCommands.register(event.getDispatcher());
    }*/

    public static void registerCommands()
    {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) -> TFCCommands.register(dispatcher));
    }

    /*@SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event)
    {
        CacheInvalidationListener.INSTANCE.invalidateAll();
    }*/

    public static void registerServerStopEvent()
    {
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> CacheInvalidationListener.INSTANCE.invalidateAll());
    }

    /*@SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event)
    {
        // Check for possible collapse
        WorldAccess world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);

        if (TFCTags.Blocks.CAN_TRIGGER_COLLAPSE.contains(state.getBlock()) && world instanceof World)
        {
            CollapseRecipe.tryTriggerCollapse((World) world, pos);
        }
    }*/

    public static void registerBlockBreakEvent()
    {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (TFCTags.Blocks.CAN_TRIGGER_COLLAPSE.contains(state.getBlock()) && world != null)
            {
                CollapseRecipe.tryTriggerCollapse(world, pos);
            }
        });
    }

    /*@SubscribeEvent       - Moved to world mixin
    public static void onNeighborUpdate(BlockEvent.NeighborNotifyEvent event)
    {
        if (event.getWorld() instanceof ServerWorld)
        {
            final ServerWorld world = (ServerWorld) event.getWorld();
            for (Direction direction : event.getNotifiedSides())
            {
                // Check each notified block for a potential gravity block
                final BlockPos pos = event.getPos().relative(direction);
                final BlockState state = world.getBlockState(pos);

                if (TFCTags.Blocks.CAN_LANDSLIDE.contains(state.getBlock()))
                {
                    //world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
                    Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.addLandslidePos(pos));
                }

                if (TFCTags.Blocks.BREAKS_WHEN_ISOLATED.contains(state.getBlock()))
                {
                    //world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addIsolatedPos(pos));
                    Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.addIsolatedPos(pos));
                }
            }
        }
    }*/

    //@SubscribeEvent
    public static void onBlockPlace(BlockPos pos2, World world2, BlockState state2)
    {
        if (world2 instanceof ServerWorld)
        {
            final ServerWorld world = (ServerWorld) world2;
            final BlockPos pos = pos2;
            final BlockState state = state2;

            if (TFCTags.Blocks.CAN_LANDSLIDE.contains(state.getBlock()))
            {
                //world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
                Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.addLandslidePos(pos));
            }

            if (TFCTags.Blocks.BREAKS_WHEN_ISOLATED.contains(state.getBlock()))
            {
                //world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addIsolatedPos(pos));
                Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.addIsolatedPos(pos));
            }
        }
    }

    /*@SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            //event.world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.tick(event.world));
            Components.WORLD_TRACKING.maybeGet(event.getWorld()).ifPresent(comp -> comp.tick(event.getWorld()));
        }
    }*/

    public static void registerWorldTickEvent()
    {
        ServerTickEvents.START_WORLD_TICK.register(world -> Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.tick(world)));
    }

    /*@SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        if (!event.getWorld().isClientSide)
        {
            //event.getWorld().getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addCollapsePositions(new BlockPos(event.getExplosion().getPosition()), event.getAffectedBlocks()));
            Components.WORLD_TRACKING.maybeGet(event.getWorld()).ifPresent(cap -> cap.addCollapsePositions(new BlockPos(event.getExplosion().getPosition()), event.getAffectedBlocks()));
        }
    }*/

    /*@SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof ServerWorld && ((ServerWorld) event.getWorld()).dimension() == World.OVERWORLD)
        {
            ServerWorld world = (ServerWorld) event.getWorld();
            if (TFCConfig.SERVER.enableVanillaNaturalRegeneration.get())
            {
                // Natural regeneration should be disabled, allows TFC to have custom regeneration
                world.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, world.getServer());
                LOGGER.info("Updating gamerule naturalRegeneration to false!");
            }
        }
    }*/

    public static void registerWorldLoadEvent()
    {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world != null && world.getRegistryKey() == World.OVERWORLD)
            {
                if (TerraFirmaCraft.getConfig().serverConfig.player.enableVanillaNaturalRegeneration)
                {
                    // Natural regeneration should be disabled, allows TFC to have custom regeneration
                    world.getGameRules().get(GameRules.NATURAL_REGENERATION).set(false, world.getServer());
                    LOGGER.info("Updating gamerule naturalRegeneration to false!");
                }
            }
        });
    }

    /*@SubscribeEvent
    public static void onCreateNetherPortal(BlockEvent.PortalSpawnEvent event)
    {
        if (!TFCConfig.SERVER.enableNetherPortals.get())
        {
            event.setCanceled(true);
        }
    }*/

    /*@SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        Block originalBlock = event.getOriginalState().getBlock();
        if (originalBlock == Blocks.STONE)
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.GABBRO).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (originalBlock == Blocks.COBBLESTONE)
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.RHYOLITE).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
        else if (originalBlock == Blocks.BASALT)
        {
            event.setNewState(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.BASALT).get(Rock.BlockType.HARDENED).get().defaultBlockState());
        }
    }*/

    public static void registerFluidReactionBehaviour()
    {
        FluidReactCallback.EVENT.register(original -> {
            Block originalBlock = original.getBlock();
            if (originalBlock == Blocks.STONE)
            {
                return Optional.of(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.GABBRO).get(Rock.BlockType.HARDENED).getDefaultState());
            }
            else if (originalBlock == Blocks.COBBLESTONE)
            {
                return Optional.of(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.RHYOLITE).get(Rock.BlockType.HARDENED).getDefaultState());
            }
            else if (originalBlock == Blocks.BASALT)
            {
                return Optional.of(TFCBlocks.ROCK_BLOCKS.get(Rock.Default.BASALT).get(Rock.BlockType.HARDENED).getDefaultState());
            }
            else
            {
                return Optional.empty();
            }
        });
    }

    public static void registerEvents()
    {
        //registerChunkLoadEvents();
        registerCommands();
        //registerChunkUnloadEvents();
        registerBlockBreakEvent();
        registerFluidReactionBehaviour();
        registerReloadListeners();
        registerServerStartEvent();
        registerServerStopEvent();
        registerWorldTickEvent();
        registerWorldLoadEvent();
    }
}