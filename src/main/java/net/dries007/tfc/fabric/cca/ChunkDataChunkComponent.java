/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.fabric.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.dries007.tfc.network.ChunkWatchPacket;
import net.dries007.tfc.world.chunkdata.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ChunkDataChunkComponent implements Component {
    public static final ChunkDataChunkComponent EMPTY = new ChunkDataChunkComponent.Immutable();

    public static ChunkDataChunkComponent get(WorldAccess world, BlockPos pos)
    {
        return get(world, new ChunkPos(pos));
    }

    /**
     * Called to get chunk data when a world context is available.
     * If on client, will query capability, falling back to cache, and send request packets if necessary
     * If on server, will either query capability falling back to cache, or query provider to generate the data.
     *
     * @see ChunkDataProvider#get(BlockPos, ChunkDataChunkComponent.Status)  to directly force chunk generation, or if a world is not available
     * @see ChunkDataCache#get(ChunkPos) to directly access the cache
     */
    public static ChunkDataChunkComponent get(WorldAccess world, ChunkPos pos)
    {
        // Query cache first, picking the correct cache for the current logical side
        ChunkDataChunkComponent data = ChunkDataCache.get(world).get(pos);
        if (data == null)
        {
            return getCapability(world.isChunkLoaded(pos.x, pos.z) ? world.getChunk(pos.getStartPos()) : null).orElse(ChunkDataChunkComponent.EMPTY);
        }
        return data;
    }

    /**
     * Helper method, since lazy optionals and instanceof checks together are ugly
     */
    public static Optional<ChunkDataChunkComponent> getCapability(@Nullable Chunk chunk)
    {
        if (chunk instanceof WorldChunk)
        {
            return Components.CHUNK_DATA.maybeGet(chunk);
        }
        return Optional.empty();
    }

    private ChunkPos pos;
    private Chunk chunk;

    private ChunkDataChunkComponent.Status status;

    private RockData rockData;
    private LerpFloatLayer rainfallLayer;
    private LerpFloatLayer temperatureLayer;
    private ForestType forestType;
    private float forestWeirdness;
    private float forestDensity;
    private PlateTectonicsClassification plateTectonicsInfo;

    public ChunkDataChunkComponent(ChunkPos pos)
    {
        this.pos = pos;

        reset();
    }

    public ChunkDataChunkComponent(Chunk chunk) {
        this.chunk = chunk;

        reset();
    }

    public ChunkPos getPos()
    {
        return pos != null ? pos : chunk.getPos();
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

    public ChunkDataChunkComponent.Status getStatus()
    {
        return status;
    }

    public void setStatus(ChunkDataChunkComponent.Status status)
    {
        this.status = status;
    }

    /**
     * @return If the current chunk data is empty, then return other
     */
    public ChunkDataChunkComponent ifEmptyGet(Supplier<ChunkDataChunkComponent> other)
    {
        return status != ChunkDataChunkComponent.Status.EMPTY ? this : other.get();
    }

    /**
     * Create an update packet to send to client with necessary information
     */
    public ChunkWatchPacket getUpdatePacket()
    {
        return new ChunkWatchPacket(getPos().x, getPos().z, rainfallLayer, temperatureLayer, forestType, forestDensity, forestWeirdness, plateTectonicsInfo);
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

        if (status == ChunkDataChunkComponent.Status.CLIENT || status == ChunkDataChunkComponent.Status.EMPTY)
        {
            this.status = ChunkDataChunkComponent.Status.CLIENT;
        }
        else
        {
            throw new IllegalStateException("ChunkData#onUpdatePacket was called on non client side chunk data: " + this);
        }
    }

    @Override
    public /*CompoundTag*/ void writeToNbt(CompoundTag nbt)
    {
        //CompoundTag nbt = new CompoundTag();

        nbt.putByte("status", (byte) status.ordinal());
        if (status.isAtLeast(ChunkDataChunkComponent.Status.PLATE_TECTONICS))
        {
            nbt.putByte("plateTectonicsInfo", (byte) plateTectonicsInfo.ordinal());
        }
        if (status.isAtLeast(ChunkDataChunkComponent.Status.CLIMATE))
        {
            nbt.put("rainfall", rainfallLayer.serialize());
            nbt.put("temperature", temperatureLayer.serialize());
        }
        if (status.isAtLeast(ChunkDataChunkComponent.Status.ROCKS))
        {
            nbt.put("rockData", rockData.serialize());
        }
        if (status.isAtLeast(ChunkDataChunkComponent.Status.FLORA))
        {
            nbt.putByte("forestType", (byte) forestType.ordinal());
            nbt.putFloat("forestWeirdness", forestWeirdness);
            nbt.putFloat("forestDensity", forestDensity);
        }
        //return nbt;
    }

    @Override
    public void readFromNbt(CompoundTag nbt)
    {
        if (nbt != null)
        {
            status = ChunkDataChunkComponent.Status.valueOf(nbt.getByte("status"));
            if (status.isAtLeast(ChunkDataChunkComponent.Status.PLATE_TECTONICS))
            {
                plateTectonicsInfo = PlateTectonicsClassification.valueOf(nbt.getByte("plateTectonicsInfo"));
            }
            if (status.isAtLeast(ChunkDataChunkComponent.Status.CLIMATE))
            {
                rainfallLayer.deserialize(nbt.getCompound("rainfall"));
                temperatureLayer.deserialize(nbt.getCompound("temperature"));
            }
            if (status.isAtLeast(ChunkDataChunkComponent.Status.ROCKS))
            {
                rockData.deserialize(nbt.getCompound("rockData"));
            }
            if (status.isAtLeast(ChunkDataChunkComponent.Status.FLORA))
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
        return "ChunkData{pos=" + getPos() + ", status=" + status + ", hashCode=" + Integer.toHexString(hashCode()) + '}';
    }

    private void reset()
    {
        rockData = new RockData();
        rainfallLayer = new LerpFloatLayer(250);
        temperatureLayer = new LerpFloatLayer(10);
        forestWeirdness = 0.5f;
        forestDensity = 0.5f;
        forestType = ForestType.NONE;
        status = ChunkDataChunkComponent.Status.EMPTY;
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

        private static final ChunkDataChunkComponent.Status[] VALUES = values();

        public static ChunkDataChunkComponent.Status valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : EMPTY;
        }

        public ChunkDataChunkComponent.Status next()
        {
            return this == FLORA ? FLORA : VALUES[this.ordinal() + 1];
        }

        public boolean isAtLeast(ChunkDataChunkComponent.Status otherStatus)
        {
            return this.ordinal() >= otherStatus.ordinal();
        }
    }

    /**
     * Only used for the empty instance, this will enforce that it never leaks data
     * New empty instances can be constructed via constructor, EMPTY instance is specifically for an immutable empty copy, representing invalid chunk data
     */
    private static final class Immutable extends ChunkDataChunkComponent
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
        public void readFromNbt(CompoundTag nbt)
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
