package net.dries007.tfc.mixin.fabric.world.loadandwatch;

import net.dries007.tfc.FabricEventHandler;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(ThreadedAnvilChunkStorage.class)
public class TACSMixin {

    @Shadow
    @Final
    private ServerWorld world;

    @Inject(method = "method_18843(Lnet/minecraft/server/world/ChunkHolder;Ljava/util/concurrent/CompletableFuture;JLnet/minecraft/world/chunk/Chunk;)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;setLoadedToWorld(Z)V", shift = At.Shift.AFTER))
    public void inject$unload(ChunkHolder holder, CompletableFuture fut, long pos, Chunk chunk, CallbackInfo ci)
    {
        FabricEventHandler.chunkUnload((WorldChunk) chunk);
    }

    @Inject(method = "method_17227(Lnet/minecraft/server/world/ChunkHolder;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/world/chunk/Chunk;", at=@At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void inject$load(ChunkHolder holder, Chunk chunk, CallbackInfoReturnable<Chunk> ci, ChunkPos pos, WorldChunk worldChunk, WorldChunk worldChunk2)
    {
        FabricEventHandler.chunkLoad(worldChunk2);
    }

    @Inject(method = "sendWatchPackets", at=@At("HEAD"))
    public void inject$watch(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean withinMaxWatchDistance, boolean withinViewDistance, CallbackInfo ci)
    {
        if (withinMaxWatchDistance)
        FabricEventHandler.chunkWatch(world, player, pos);
        else FabricEventHandler.chunkUnwatch(world, player, pos);
    }

}
