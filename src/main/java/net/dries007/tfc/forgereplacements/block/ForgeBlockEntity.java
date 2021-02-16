/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.forgereplacements.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

//TODO Implement
public interface ForgeBlockEntity {

    BlockEntity getBlockEntity();
    default void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket pkt){ }

    default void handleUpdateTag(BlockState state, CompoundTag tag)
    {
        getBlockEntity().fromTag(state, tag);
    }
}
