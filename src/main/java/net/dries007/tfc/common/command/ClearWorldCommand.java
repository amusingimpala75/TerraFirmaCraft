/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dries007.tfc.forgereplacements.command.EnumArgument;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.feature.vein.VeinConfig;
import net.dries007.tfc.world.feature.vein.VeinFeature;

public final class ClearWorldCommand
{
    private static final String STARTING = "tfc.commands.clear_world.starting";
    private static final String DONE = "tfc.commands.clear_world.done";

    public static LiteralArgumentBuilder<ServerCommandSource> create()
    {
        return CommandManager.literal("clearworld")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 250))
                .then(CommandManager.argument("preset", EnumArgument.enumArgument(Preset.class))
                    .executes(cmd -> clearWorld(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "radius"), cmd.getArgument("preset", Preset.class)))
                )
                .executes(cmd -> clearWorld(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "radius"), Preset.ALL))
            );
    }

    @SuppressWarnings("deprecation")
    private static int clearWorld(CommandSource source, int radius, Preset preset)
    {
        ((ServerCommandSource)source).sendFeedback(new TranslatableText(STARTING), true);

        final World world = ((ServerCommandSource) source).getWorld();
        final BlockPos center = new BlockPos(((ServerCommandSource) source).getPosition());
        final BlockState air = Blocks.AIR.getDefaultState();

        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final Predicate<BlockState> predicate = preset.make(((ServerCommandSource) source).getMinecraftServer());

        int blocksRemoved = 0;

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                final int height = world.getTopY(Heightmap.Type.WORLD_SURFACE, center.getX() + x, center.getZ() + z);
                for (int y = 0; y < height; y++)
                {
                    mutablePos.set(center).move(x, 0, z).setY(y);
                    BlockState state = world.getBlockState(mutablePos);
                    if (!state.isAir() && predicate.test(state))
                    {
                        world.setBlockState(mutablePos, air, 2 | 16);
                        blocksRemoved++;
                    }
                }
            }
        }
        ((ServerCommandSource) source).sendFeedback(new TranslatableText(DONE, blocksRemoved), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Determines which blocks to remove
     * The provided predicate should return TRUE to blocks that should be removed
     */
    enum Preset
    {
        ALL(server -> state -> true),
        RAW_ROCK(server -> {
            final Set<Block> blocks = TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.RAW)).collect(Collectors.toSet());
            return state -> blocks.contains(state.getBlock());
        }),
        EARTH(server -> {
            final Set<Block> blocks = Stream.of(
                TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.RAW)),
                TFCBlocks.ROCK_BLOCKS.values().stream().map(map -> map.get(Rock.BlockType.GRAVEL)),
                TFCBlocks.SOIL.get(SoilBlockType.DIRT).values().stream(),
                TFCBlocks.SOIL.get(SoilBlockType.GRASS).values().stream(),
                TFCBlocks.SAND.values().stream()
            ).flatMap(t -> t).collect(Collectors.toSet());
            return state -> blocks.contains(state.getBlock());
        }),
        NOT_ORE(server -> {
            final Registry<ConfiguredFeature<?, ?>> registry = server.getRegistryManager().get(Registry.CONFIGURED_FEATURE_WORLDGEN);
            Set<Block> blocks = registry.stream()
                .filter(feature -> feature.feature instanceof VeinFeature<?, ?>)
                .flatMap(feature -> ((VeinConfig) feature.config).getOreStates().stream())
                .map(AbstractBlock.AbstractBlockState::getBlock)
                .collect(Collectors.toSet());
            return state -> !blocks.contains(state.getBlock());
        });

        private final Function<MinecraftServer, Predicate<BlockState>> factory;

        Preset(Function<MinecraftServer, Predicate<BlockState>> factory)
        {
            this.factory = factory;
        }

        Predicate<BlockState> make(MinecraftServer server)
        {
            return factory.apply(server);
        }
    }
}