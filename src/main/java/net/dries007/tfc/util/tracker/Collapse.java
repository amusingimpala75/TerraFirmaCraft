/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tracker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.dries007.tfc.forgereplacements.NBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class Collapse implements NBTSerializable<CompoundTag>
{
    BlockPos centerPos;
    List<BlockPos> nextPositions;
    public double radiusSquared;

    public Collapse(BlockPos centerPos, List<BlockPos> nextPositions, double radiusSquared)
    {
        this.centerPos = centerPos;
        this.nextPositions = nextPositions;
        this.radiusSquared = radiusSquared;
    }

    public Collapse(CompoundTag nbt)
    {
        deserialize(nbt);
    }

    @Override
    public CompoundTag serialize()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("centerPos", centerPos.asLong());
        nbt.putLongArray("nextPositions", nextPositions.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.putDouble("radiusSquared", radiusSquared);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt)
    {
        if (nbt != null)
        {
            centerPos = BlockPos.fromLong(nbt.getLong("centerPos"));
            nextPositions = Arrays.stream(nbt.getLongArray("nextPositions")).mapToObj(BlockPos::fromLong).collect(Collectors.toList());
            radiusSquared = nbt.getDouble("radiusSquared");
        }
    }

    public List<BlockPos> getNextPositions() {
        return nextPositions;
    }

    public BlockPos getCenterPos() {
        return centerPos;
    }
}