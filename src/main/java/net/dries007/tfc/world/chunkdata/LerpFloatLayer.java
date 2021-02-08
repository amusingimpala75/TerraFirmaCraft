/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import net.dries007.tfc.forgereplacements.NBTSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

import net.dries007.tfc.world.noise.NoiseUtil;

/**
 * This is a simple linearly interpolated float grid.
 * It records the value at the corner, and interpolates the values between on demand.
 */
public class LerpFloatLayer implements NBTSerializable<CompoundTag>
{
    private float valueNW, valueNE, valueSW, valueSE;

    public LerpFloatLayer(PacketByteBuf buffer)
    {
        deserialize(buffer);
    }

    public LerpFloatLayer(float defaultValue)
    {
        init(defaultValue, defaultValue, defaultValue, defaultValue);
    }

    public void init(float valueNW, float valueNE, float valueSW, float valueSE)
    {
        this.valueNW = valueNW;
        this.valueNE = valueNE;
        this.valueSW = valueSW;
        this.valueSE = valueSE;
    }

    /**
     * Gets the floating point value approximated at the point within the grid.
     *
     * @param tNS A distance in the N-S direction. 0 = Full north, 1 = Full south.
     * @param tEW A distance in the E-W direction. 0 = Full east, 1 = Full west.
     */
    public float getValue(float tNS, float tEW)
    {
        return NoiseUtil.lerpGrid(valueNE, valueNW, valueSE, valueSW, tNS, tEW);
    }

    @Override
    public CompoundTag serialize()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("nw", valueNW);
        nbt.putFloat("ne", valueNE);
        nbt.putFloat("sw", valueSW);
        nbt.putFloat("se", valueSE);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt)
    {
        valueNW = nbt.getFloat("nw");
        valueNE = nbt.getFloat("ne");
        valueSW = nbt.getFloat("sw");
        valueSE = nbt.getFloat("se");
    }

    public void serialize(PacketByteBuf buffer)
    {
        buffer.writeFloat(valueNW);
        buffer.writeFloat(valueNE);
        buffer.writeFloat(valueSW);
        buffer.writeFloat(valueSE);
    }

    public void deserialize(PacketByteBuf buffer)
    {
        valueNW = buffer.readFloat();
        valueNE = buffer.readFloat();
        valueSW = buffer.readFloat();
        valueSE = buffer.readFloat();
    }
}