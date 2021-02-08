/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import net.dries007.tfc.network.ChunkWatchPacket;
import org.jetbrains.annotations.Nullable;

public class ChunkData implements ICapabilitySerializable<CompoundTag>
{
    public static final ChunkData EMPTY = new ChunkData.Immutable();

    public static ChunkData get(WorldAccess world, BlockPos pos)
    {
        return get(world, new ChunkPos(pos));
    }

    /**
     * Called to get chunk data when a world context is available.
     * If on client, will query capability, falling back to cache, and send request packets if necessary
     * If on server, will either query capability falling back to cache, or query provider to generate the data.
     *
     * @see ChunkDataProvider#get(ChunkPos, Status) to directly force chunk generation, or if a world is not available
     * @see ChunkDataCache#get(ChunkPos) to directly access the cache
     */
    public static ChunkData get(WorldAccess world, ChunkPos pos)
    {
        // Query cache first, picking the correct cache for the current logical side
        ChunkData data = ChunkDataCache.get(world).get(pos);
        if (data == null)
        {
            return getCapability(world.isChunkLoaded(pos.x, pos.z) ? world.getChunk(pos.getStartPos()) : null).orElse(ChunkData.EMPTY);
        }
        return data;
    }

    /**
     * Helper method, since lazy optionals and instanceof checks together are ugly
     */
    public static Optional<ChunkData> getCapability(@Nullable Chunk chunk)
    {
        if (chunk instanceof WorldChunk)
        {
            return ((Chunk) chunk).getCapability(ChunkDataCapability.CAPABILITY);
        }
        return Optional.empty();
    }

    private final Optional<ChunkData> capability;
    private final ChunkPos pos;

    private Status status;

    private RockData rockData;
    private LerpFloatLayer rainfallLayer;
    private LerpFloatLayer temperatureLayer;
    private ForestType forestType;
    private float forestWeirdness;
    private float forestDensity;
    private PlateTectonicsClassification plateTectonicsInfo;

    public ChunkData(ChunkPos pos)
    {
        this.pos = pos;
        this.capability = Optional.of(() -> this);

        reset();
    }

    public ChunkPos getPos()
    {
        return pos;
    }

    public RockData getRockData()
    {
        return rockData;
    }

    public void setRockData(RockData rockData)
    {
        this.rockData = rockData;
    }

    public float getRainfall(BlockPos pos)
    {
        return getRainfall(pos.getX() & 15, pos.getZ() & 15);
    }

    public float getRainfall(int x, int z)
    {
        return rainfallLayer.getValue(z / 16f, 1 - (x / 16f));
    }

    public void setRainfall(float rainNW, float rainNE, float rainSW, float rainSE)
    {
        rainfallLayer.init(rainNW, rainNE, rainSW, rainSE);
    }

    public float getAverageTemp(BlockPos pos)
    {
        return getAverageTemp(pos.getX() & 15, pos.getZ() & 15);
    }

    public float getAverageTemp(int x, int z)
    {
        return temperatureLayer.getValue(z / 16f, 1 - (x / 16f));
    }

    public void setAverageTemp(float tempNW, float tempNE, float tempSW, float tempSE)
    {
        temperatureLayer.init(tempNW, tempNE, tempSW, tempSE);
    }

    public void setFloraData(ForestType forestType, float forestWeirdness, float forestDensity)
    {
        this.forestType = forestType;
        this.forestWeirdness = forestWeirdness;
        this.forestDensity = forestDensity;
    }

    public ForestType getForestType()
    {
        return forestType;
    }

    public float getForestWeirdness()
    {
        return forestWeirdness;
    }

    public float getForestDensity()
    {
        return forestDensity;
    }

    public PlateTectonicsClassification getPlateTectonicsInfo()
    {
        return plateTectonicsInfo;
    }

    public void setPlateTectonicsInfo(PlateTectonicsClassification plateTectonicsInfo)
    {
        this.plateTectonicsInfo = plateTectonicsInfo;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    /**
     * @return If the current chunk data is empty, then return other
     */
    public ChunkData ifEmptyGet(Supplier<ChunkData> other)
    {
        return status != Status.EMPTY ? this : other.get();
    }

    /**
     * Create an update packet to send to client with necessary information
     */
    public ChunkWatchPacket getUpdatePacket()
    {
        return new ChunkWatchPacket(pos.x, pos.z, rainfallLayer, temperatureLayer, forestType, forestDensity, forestWeirdness, plateTectonicsInfo);
    }

    /**
     * Called on client, sets to received data
     */
    public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness, PlateTectonicsClassification plateTectonicsInfo)
    {
        this.rainfallLayer = rainfallLayer;
        this.temperatureLayer = temperatureLayer;
        this.forestType = forestType;
        this.forestDensity = forestDensity;
        this.forestWeirdness = forestWeirdness;
        this.plateTectonicsInfo = plateTectonicsInfo;

        if (status == Status.CLIENT || status == Status.EMPTY)
        {
            this.status = Status.CLIENT;
        }
        else
        {
            throw new IllegalStateException("ChunkData#onUpdatePacket was called on non client side chunk data: " + this);
        }
    }

    @Override
    public <T> Optional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return ChunkDataCapability.CAPABILITY.orEmpty(cap, capability);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putByte("status", (byte) status.ordinal());
        if (status.isAtLeast(Status.PLATE_TECTONICS))
        {
            nbt.putByte("plateTectonicsInfo", (byte) plateTectonicsInfo.ordinal());
        }
        if (status.isAtLeast(Status.CLIMATE))
        {
            nbt.put("rainfall", rainfallLayer.serialize());
            nbt.put("temperature", temperatureLayer.serialize());
        }
        if (status.isAtLeast(Status.ROCKS))
        {
            nbt.put("rockData", rockData.serializeNBT());
        }
        if (status.isAtLeast(Status.FLORA))
        {
            nbt.putByte("forestType", (byte) forestType.ordinal());
            nbt.putFloat("forestWeirdness", forestWeirdness);
            nbt.putFloat("forestDensity", forestDensity);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        if (nbt != null)
        {
            status = Status.valueOf(nbt.getByte("status"));
            if (status.isAtLeast(Status.PLATE_TECTONICS))
            {
                plateTectonicsInfo = PlateTectonicsClassification.valueOf(nbt.getByte("plateTectonicsInfo"));
            }
            if (status.isAtLeast(Status.CLIMATE))
            {
                rainfallLayer.deserialize(nbt.getCompound("rainfall"));
                temperatureLayer.deserialize(nbt.getCompound("temperature"));
            }
            if (status.isAtLeast(Status.ROCKS))
            {
                rockData.deserializeNBT(nbt.getCompound("rockData"));
            }
            if (status.isAtLeast(Status.FLORA))
            {
                forestType = ForestType.valueOf(nbt.getByte("forestType"));
                forestWeirdness = nbt.getFloat("forestWeirdness");
                forestDensity = nbt.getFloat("forestDensity");
            }
        }
    }

    @Override
    public String toString()
    {
        return "ChunkData{pos=" + pos + ", status=" + status + ", hashCode=" + Integer.toHexString(hashCode()) + '}';
    }

    private void reset()
    {
        rockData = new RockData();
        rainfallLayer = new LerpFloatLayer(250);
        temperatureLayer = new LerpFloatLayer(10);
        forestWeirdness = 0.5f;
        forestDensity = 0.5f;
        forestType = ForestType.NONE;
        status = Status.EMPTY;
        plateTectonicsInfo = PlateTectonicsClassification.OCEANIC;
    }

    public enum Status
    {
        CLIENT, // Special status - indicates it is a client side shallow copy
        EMPTY, // Empty - default. Should never be called to generate.
        PLATE_TECTONICS, // Metadata about the plate tectonics layer
        CLIMATE, // Climate data, rainfall and temperature
        ROCKS, // Rock layer information, used for surface builder and rock block replacement
        FLORA; // Flora and fauna information, used for features

        private static final Status[] VALUES = values();

        public static Status valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : EMPTY;
        }

        public Status next()
        {
            return this == FLORA ? FLORA : VALUES[this.ordinal() + 1];
        }

        public boolean isAtLeast(Status otherStatus)
        {
            return this.ordinal() >= otherStatus.ordinal();
        }
    }

    /**
     * Only used for the empty instance, this will enforce that it never leaks data
     * New empty instances can be constructed via constructor, EMPTY instance is specifically for an immutable empty copy, representing invalid chunk data
     */
    private static final class Immutable extends ChunkData
    {
        private Immutable()
        {
            super(new ChunkPos(ChunkPos.MARKER));
        }

        @Override
        public void setRockData(RockData rockData)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setRainfall(float rainNW, float rainNE, float rainSW, float rainSE)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setAverageTemp(float tempNW, float tempNE, float tempSW, float tempSE)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setFloraData(ForestType forestType, float forestWeirdness, float forestDensity)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setStatus(Status status)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void setPlateTectonicsInfo(PlateTectonicsClassification plateTectonicsInfo)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void onUpdatePacket(LerpFloatLayer rainfallLayer, LerpFloatLayer temperatureLayer, ForestType forestType, float forestDensity, float forestWeirdness, PlateTectonicsClassification plateTectonicsInfo)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            throw new UnsupportedOperationException("Tried to modify immutable chunk data");
        }

        @Override
        public String toString()
        {
            return "ImmutableChunkData";
        }
    }
}