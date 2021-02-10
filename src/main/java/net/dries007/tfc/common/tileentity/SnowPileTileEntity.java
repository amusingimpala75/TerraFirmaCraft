/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;

public class SnowPileTileEntity extends TFCTileEntity
{
    private BlockState internalState;

    public SnowPileTileEntity()
    {
        this(TFCTileEntities.SNOW_PILE);
    }

    protected SnowPileTileEntity(BlockEntityType<?> type)
    {
        super(type);

        this.internalState = Blocks.AIR.getDefaultState();
    }

    public void setInternalState(BlockState state)
    {
        this.internalState = state;
        markDirtyFast();
    }

    public BlockState getDestroyedState(BlockState prevState)
    {
        int prevLayers = prevState.get(SnowBlock.LAYERS);
        if (prevLayers == 1)
        {
            return internalState;
        }
        return prevState.with(SnowBlock.LAYERS, prevLayers - 1);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag nbt)
    {
        internalState = NbtHelper.toBlockState(nbt.getCompound("internalState"));
        super.fromTag(state, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt)
    {
        nbt.put("internalState", NbtHelper.fromBlockState(internalState));
        return super.toTag(nbt);
    }
}
