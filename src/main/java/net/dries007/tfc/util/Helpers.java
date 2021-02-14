/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.dries007.tfc.forgereplacements.NotNullFunction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.function.FromByteFunction;
import net.dries007.tfc.util.function.ToByteFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public final class Helpers
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    /**
     * Default {@link Identifier}, except with a TFC namespace
     */
    public static Identifier identifier(String name)
    {
        return new Identifier(MOD_ID, name);
    }

    /**
     * Avoids IDE warnings by returning null for fields that are injected in by forge.
     *
     * @return Not null!
     */
    @NotNull
    @SuppressWarnings("ConstantConditions")
    public static <T> T notNull()
    {
        return null;
    }

    public static <T> byte[] createByteArray(T[] array, ToByteFunction<T> byteConverter)
    {
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++)
        {
            bytes[i] = byteConverter.get(array[i]);
        }
        return bytes;
    }

    public static <T> void createArrayFromBytes(byte[] byteArray, T[] array, FromByteFunction<T> byteConverter)
    {
        for (int i = 0; i < byteArray.length; i++)
        {
            array[i] = byteConverter.get(byteArray[i]);
        }
    }

    public static <K, V> Map<K, V> findRegistryObjects(JsonObject obj, String path, Registry<V> registry, Collection<K> keyValues, NotNullFunction<K, String> keyStringMapper)
    {
        return findRegistryObjects(obj, path, registry, keyValues, Collections.emptyList(), keyStringMapper);
    }

    public static <K, V> Map<K, V> findRegistryObjects(JsonObject obj, String path, Registry<V> registry, Collection<K> keyValues, Collection<K> optionalKeyValues, NotNullFunction<K, String> keyStringMapper)
    {
        if (obj.has(path))
        {
            Map<K, V> objects = new HashMap<>();
            JsonObject objectsJson = JsonHelper.getObject(obj, path);
            for (K expectedKey : keyValues)
            {
                String jsonKey = keyStringMapper.run(expectedKey);
                Identifier id = new Identifier(JsonHelper.getString(objectsJson, jsonKey));
                V registryObject = registry.get(id);
                if (registryObject == null)
                {
                    throw new JsonParseException("Unknown registry object: " + id);
                }
                objects.put(expectedKey, registryObject);
            }
            for (K optionalKey : optionalKeyValues)
            {
                String jsonKey = keyStringMapper.run(optionalKey);
                if (objectsJson.has(jsonKey))
                {
                    Identifier id = new Identifier(JsonHelper.getString(objectsJson, jsonKey));
                    V registryObject = registry.get(id);
                    if (registryObject == null)
                    {
                        throw new JsonParseException("Unknown registry object: " + id);
                    }
                    objects.put(optionalKey, registryObject);
                }
            }
            return objects;
        }
        return Collections.emptyMap();
    }

    public static BlockState readBlockState(String block) throws JsonParseException
    {
        BlockArgumentParser parser = parseBlockState(block, false);
        if (parser.getBlockState() != null)
        {
            return parser.getBlockState();
        }
        throw new JsonParseException("Weird result, valid parse but not a block state: " + block);
    }

    public static BlockArgumentParser parseBlockState(String block, boolean allowTags) throws JsonParseException
    {
        StringReader reader = new StringReader(block);
        try
        {
            return new BlockArgumentParser(reader, allowTags).parse(false);
        }
        catch (CommandSyntaxException e)
        {
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * Maps a {@link Supplier} to an {@link Optional} by swallowing any runtime exceptions.
     */
    public static <T> Optional<T> mapSafeOptional(Supplier<T> unsafeSupplier)
    {
        try
        {
            return Optional.of(unsafeSupplier.get());
        }
        catch (RuntimeException e)
        {
            return Optional.empty();
        }
    }

    /**
     * Like {@link Optional#map(Function)} but for suppliers. Does not unbox the provided supplier
     */
    public static <T, R> Supplier<R> mapSupplier(Supplier<T> supplier, Function<T, R> mapper)
    {
        return () -> mapper.apply(supplier.get());
    }

    /**
     * Applies two possible consumers of a given lazy optional
     */
    public static <T> void ifPresentOrElse(Lazy<T> lazyOptional, Consumer<T> ifPresent, Runnable orElse)
    {
        /*lazyOptional.map(t -> {
            ifPresent.accept(t);
            return Unit.INSTANCE;
        }).orElseGet(() -> {
            orElse.run();
            return Unit.INSTANCE;
        });*/
        T t = lazyOptional.get();
        if (t != null) {
            ifPresent.accept(t);
        } else {
            orElse.run();
        }
    }

    /**
     * Creates a map of each enum constant to the value as provided by the value mapper.
     */
    public static <E extends Enum<E>, V> EnumMap<E, V> mapOfKeys(Class<E> enumClass, Function<E, V> valueMapper)
    {
        return mapOfKeys(enumClass, key -> true, valueMapper);
    }

    /**
     * Creates a map of each enum constant to the value as provided by the value mapper, only using enum constants that match the provided predicate.
     */
    public static <E extends Enum<E>, V> EnumMap<E, V> mapOfKeys(Class<E> enumClass, Predicate<E> keyPredicate, Function<E, V> valueMapper)
    {
        return Arrays.stream(enumClass.getEnumConstants()).filter(keyPredicate).collect(Collectors.toMap(Function.identity(), valueMapper, (v, v2) -> v, () -> new EnumMap<>(enumClass)));
    }

    /**
     * Gets the translation key name for an enum. For instance, Metal.UNKNOWN would map to "tfc.enum.metal.unknown"
     */
    public static String getEnumTranslationKey(Enum<?> anEnum)
    {
        return getEnumTranslationKey(anEnum, anEnum.getDeclaringClass().getSimpleName());
    }

    /**
     * Gets the translation key name for an enum, using a custom name instead of the enum class name
     */
    public static String getEnumTranslationKey(Enum<?> anEnum, String enumName)
    {
        return String.join(".", MOD_ID, "enum", enumName, anEnum.name()).toLowerCase();
    }

    /**
     * Names a simple container provider.
     *
     * @return a singleton container provider
     */
    public static NamedScreenHandlerFactory createNamedContainerProvider(Text name, ScreenHandlerFactory provider)
    {
        return new NamedScreenHandlerFactory()
        {
            @Override
            public Text getDisplayName()
            {
                return name;
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
            {
                return provider.createMenu(windowId, inv, player);
            }
        };
    }

    /**
     * Normally, one would just call {@link World#isClient()}
     * HOWEVER
     * There exists a BIG HUGE PROBLEM in very specific scenarios with this
     * Since World's isClientSide() actually returns the isClientSide boolean, which is set AT THE END of the World constructor, many things may happen before this is set correctly. Mostly involving world generation.
     * At this point, THE CLIENT WORLD WILL RETURN {@code false} to {@link World#isClient()}
     *
     * So, this does a roundabout check "is this instanceof ClientWorld or not" without classloading shenanigans.
     */
    public static boolean isClientSide(WorldView world)
    {
        return world instanceof World ? !(world instanceof ServerWorld) : world.isClient();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> T getTileEntity(WorldView world, BlockPos pos, Class<T> tileEntityClass)
    {
        BlockEntity te = world.getBlockEntity(pos);
        if (tileEntityClass.isInstance(te))
        {
            return (T) te;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> T getTileEntityOrThrow(WorldView world, BlockPos pos, Class<T> tileEntityClass)
    {
        BlockEntity te = world.getBlockEntity(pos);
        if (tileEntityClass.isInstance(te))
        {
            return (T) te;
        }
        throw new IllegalStateException("Expected a tile entity at " + pos + " of class " + tileEntityClass.getSimpleName());
    }

    /**
     * This returns the previous result of {@link ServerWorld#getRandomPosInChunk(int, int, int, int)}.
     */
    public static BlockPos getPreviousRandomPos(int x, int y, int z, int yMask, int randValue)
    {
        int i = randValue >> 2;
        return new BlockPos(x + (i & 15), y + (i >> 16 & yMask), z + (i >> 8 & 15));
    }

    public static BlockState getStateForPlacementWithFluid(WorldView world, BlockPos pos, BlockState state)
    {
        FluidState fluid = world.getFluidState(pos);
        if (state.getBlock() instanceof IFluidLoggable)
        {
            return ((IFluidLoggable) state.getBlock()).getStateWithFluid(state, fluid.getFluid());
        }
        return state;
    }

    /**
     * You know this will work, and I know this will work, but this compiler looks pretty stupid.
     */
    public static <E> E resolveEither(Either<E, E> either)
    {
        return either.map(e -> e, e -> e);
    }

    public static void slowEntityInBlock(Entity entity, float factor, int fallDamageReduction)
    {
        Vec3d motion = entity.getVelocity();
        entity.setVelocity(motion.multiply(factor, motion.y < 0 ? factor : 1, factor));
        if (entity.fallDistance > fallDamageReduction)
        {
            entity.handleFallDamage(entity.fallDistance - fallDamageReduction, 1.0f);
        }
        entity.fallDistance = 0;
    }

    /**
     * Copy pasta from {@link SpawnLocating} except one that doesn't require the spawn block be equal to the surface builder config top block
     */
    @Nullable
    public static BlockPos findValidSpawnLocation(ServerWorld world, ChunkPos chunkPos)
    {
        final Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = chunkPos.getStartX(); x <= chunkPos.getEndX(); ++x)
        {
            for (int z = chunkPos.getStartZ(); z <= chunkPos.getEndZ(); ++z)
            {
                mutablePos.set(x, 0, z);

                final Biome biome = world.getBiome(mutablePos);
                final int motionBlockingHeight = chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
                final int worldSurfaceHeight = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15);
                final int oceanFloorHeight = chunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15);
                if (worldSurfaceHeight >= oceanFloorHeight && biome.getSpawnSettings().isPlayerSpawnFriendly())
                {
                    for (int y = 1 + motionBlockingHeight; y >= oceanFloorHeight; y--)
                    {
                        mutablePos.set(x, y, z);

                        final BlockState state = world.getBlockState(mutablePos);
                        if (!state.getFluidState().isEmpty())
                        {
                            break;
                        }

                        if (BlockTags.VALID_SPAWN.contains(state.getBlock()))
                        {
                            return mutablePos.up().toImmutable();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static BlockState copyProperties(BlockState copyTo, BlockState copyFrom)
    {
        for (Property<?> property : copyFrom.getProperties())
        {
            copyTo = copyProperty(copyTo, copyFrom, property);
        }
        return copyTo;
    }

    public static <T extends Comparable<T>> BlockState copyProperty(BlockState copyTo, BlockState copyFrom, Property<T> property)
    {
        if (copyTo.contains(property))
        {
            return copyTo.with(property, copyFrom.get(property));
        }
        return copyTo;
    }

    public static void damageCraftingItem(ItemStack stack, int amount)
    {
        PlayerEntity player = ForgeHooks.getCraftingPlayer(); // Mods may not set this properly
        if (player != null)
        {
            stack.damage(amount, player, entity -> {});
        }
        else
        {
            damageItem(stack, amount);
        }
    }

    /**
     * A replacement for {@link ItemStack#damage(int, LivingEntity, Consumer)} when an entity is not present
     */
    public static void damageItem(ItemStack stack, int amount)
    {
        if (stack.isDamageable())
        {
            // There's no player here so we can't safely do anything.
            //amount = stack.getItem().damageItem(stack, amount, null, e -> {});
            if (stack.damage(amount, RANDOM, null))
            {
                stack.decrement(1);
                stack.setDamage(0);
            }
        }
    }

    /**
     * Copied from {@link World#breakBlock(BlockPos, boolean)}
     * Allows the loot context to be modified
     */
    @SuppressWarnings("deprecation")
    public static void destroyBlockAndDropBlocksManually(World worldIn, BlockPos pos, Consumer<LootContext.Builder> builder)
    {
        BlockState state = worldIn.getBlockState(pos);
        if (!state.isAir())
        {
            FluidState fluidstate = worldIn.getFluidState(pos);
            if (!(state.getBlock() instanceof AbstractFireBlock))
            {
                worldIn.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
            }

            if (worldIn instanceof ServerWorld)
            {
                BlockEntity tileEntity = state.getBlock().hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;

                // Copied from Block.getDrops()
                LootContext.Builder lootContext = new LootContext.Builder((ServerWorld) worldIn)
                    .random(worldIn.random)
                    .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                    .parameter(LootContextParameters.TOOL, ItemStack.EMPTY)
                    .optionalParameter(LootContextParameters.THIS_ENTITY, null)
                    .optionalParameter(LootContextParameters.BLOCK_ENTITY, tileEntity);
                builder.accept(lootContext);
                state.getDroppedStacks(lootContext).forEach(stackToSpawn -> Block.dropStack(worldIn, pos, stackToSpawn));
                state.onStacksDropped((ServerWorld) worldIn, pos, ItemStack.EMPTY);
            }
            worldIn.setBlockState(pos, fluidstate.getBlockState(), 3, 512);
        }
    }
}