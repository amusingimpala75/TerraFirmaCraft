/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import net.dries007.tfc.forgereplacements.command.EnumArgument;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.world.feature.tree.TFCTree;

public final class TreeCommand
{
    public static LiteralArgumentBuilder<ServerCommandSource> create()
    {
        return CommandManager.literal("tree")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                .then(CommandManager.argument("wood", EnumArgument.enumArgument(Wood.Default.class))
                    .then(CommandManager.argument("variant", EnumArgument.enumArgument(Variant.class))
                        .executes(context -> placeTree(context.getSource().getWorld(), BlockPosArgumentType.getBlockPos(context, "pos"), context.getArgument("wood", Wood.Default.class), context.getArgument("variant", Variant.class)))
                    )
                    .executes(context -> placeTree(context.getSource().getWorld(), BlockPosArgumentType.getBlockPos(context, "pos"), context.getArgument("wood", Wood.Default.class), Variant.NORMAL))
                )
            );
    }

    private static int placeTree(ServerWorld world, BlockPos pos, Wood.Default wood, Variant variant)
    {
        TFCTree tree = wood.getTree();
        Registry<ConfiguredFeature<?, ?>> registry = world.getRegistryManager().get(Registry.CONFIGURED_FEATURE_WORLDGEN);
        ConfiguredFeature<?, ?> feature = variant == Variant.NORMAL ? tree.getNormalFeature(registry) : tree.getOldGrowthFeature(registry);
        feature.generate(world, world.getChunkManager().getChunkGenerator(), world.getRandom(), pos);
        return Command.SINGLE_SUCCESS;
    }

    private enum Variant
    {
        NORMAL, LARGE
    }
}