/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

public abstract class TFCTileEntity extends BlockEntity
{
    protected static final Logger LOGGER = LogManager.getLogger();

    protected TFCTileEntity(BlockEntityType<?> type)
    {
        super(type);
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket()
    {
        return new BlockEntityUpdateS2CPacket(getPos(), 1, toTag(new CompoundTag()));
    }

    @Override
    public CompoundTag toInitialChunkDataTag()
    {
        return toTag(super.toInitialChunkDataTag());
    }

    @Override
    public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket pkt)
    {
        fromTag(getCachedState(), pkt.getCompoundTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundTag nbt)
    {
        fromTag(state, nbt);
    }

    /**
     * Syncs the TE data to client via means of a block update
     * Use for stuff that is updated infrequently, for data that is analogous to changing the state.
     * DO NOT call every tick
     */
    public void markForBlockUpdate()
    {
        if (world != null)
        {
            BlockState state = world.getBlockState(pos);
            world.updateListeners(pos, state, state, 3);
            markDirty();
        }
    }

    /**
     * Marks a tile entity for syncing without sending a block update.
     * Use preferentially over {@link InventoryTileEntity#markForBlockUpdate()} if there's no reason to have a block update.
     * For container based integer synchronization, see ITileFields
     * DO NOT call every tick
     */
    public void markForSync()
    {
        sendVanillaUpdatePacket();
        markDirty();
    }

    /**
     * Marks the tile entity dirty without updating comparator output.
     * Useful when called a lot for TE's that don't have a comparator output
     */
    protected void markDirtyFast()
    {
        if (world != null)
        {
            getCachedState();
            world.markDirty(pos, this);
        }
    }

    protected void sendVanillaUpdatePacket()
    {
        BlockEntityUpdateS2CPacket packet = toUpdatePacket();
        BlockPos pos = getPos();

        if (packet != null && world instanceof ServerWorld)
        {
            ((ServerChunkManager) world.getChunkManager()).threadedAnvilChunkStorage.getPlayersWatchingChunk(new ChunkPos(pos), false).forEach(e -> e.networkHandler.sendPacket(packet));
        }
    }
}
