/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

public final class PlayerCommand
{
    private static final String QUERY_HUNGER = "tfc.commands.player.query_hunger";
    private static final String QUERY_SATURATION = "tfc.commands.player.query_saturation";

    public static LiteralArgumentBuilder<ServerCommandSource> create()
    {
        return CommandManager.literal("player")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("target", EntityArgumentType.player())
                .then(CommandManager.literal("query")
                    .then(CommandManager.literal("hunger")
                        .executes(context -> queryHunger(context, EntityArgumentType.getPlayer(context, "target")))
                    )
                    .then(CommandManager.literal("saturation")
                        .executes(context -> querySaturation(context, EntityArgumentType.getPlayer(context, "target")))
                    )
                    .then(CommandManager.literal("water")
                        .executes(context -> queryWater(context, EntityArgumentType.getPlayer(context, "target")))
                    )
                    .then(CommandManager.literal("nutrition")
                        .executes(context -> queryNutrition(context, EntityArgumentType.getPlayer(context, "target")))
                    )
                )
                .then(CommandManager.literal("set")
                    .then(CommandManager.literal("hunger")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 20))
                            .executes(context -> setHunger(EntityArgumentType.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), false))
                        )
                    )
                    .then(CommandManager.literal("saturation")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 20))
                            .executes(context -> setSaturation(EntityArgumentType.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), false))
                        )
                    )
                )
                .then(CommandManager.literal("reset")
                    .then(CommandManager.literal("hunger")
                        .executes(context -> setHunger(EntityArgumentType.getPlayer(context, "target"), 20, false))
                    )
                    .then(CommandManager.literal("saturation")
                        .executes(context -> setSaturation(EntityArgumentType.getPlayer(context, "target"), 5, false))
                    )
                    .then(CommandManager.literal("water")
                        .executes(context -> setWater(EntityArgumentType.getPlayer(context, "target"), 100, false))
                    )
                )
                .then(CommandManager.literal("add")
                    .then(CommandManager.literal("hunger")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(-20, 20))
                            .executes(context -> setHunger(EntityArgumentType.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), true))
                        )
                    )
                    .then(CommandManager.literal("saturation")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(-20, 20))
                            .executes(context -> setSaturation(EntityArgumentType.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), true))
                        )
                    )
                    .then(CommandManager.literal("water")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                            .executes(context -> setWater(EntityArgumentType.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), true))
                        )
                    )
                )
            );
    }

    private static int queryHunger(CommandContext<ServerCommandSource> context, PlayerEntity player)
    {
        int hunger = player.getHungerManager().getFoodLevel();
        context.getSource().sendFeedback(new TranslatableText(QUERY_HUNGER, hunger), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int querySaturation(CommandContext<ServerCommandSource> context, PlayerEntity player)
    {
        float saturation = player.getHungerManager().getSaturationLevel();
        context.getSource().sendFeedback(new TranslatableText(QUERY_SATURATION, saturation), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int queryWater(CommandContext<ServerCommandSource> context, PlayerEntity player)
    {
        // todo
        throw new UnsupportedOperationException("Not implemented");
    }

    private static int queryNutrition(CommandContext<ServerCommandSource> context, PlayerEntity player)
    {
        // todo
        throw new UnsupportedOperationException("Not implemented");
    }

    private static int setHunger(PlayerEntity player, int hunger, boolean add)
    {
        if (add)
        {
            hunger += player.getHungerManager().getFoodLevel();
        }
        player.getHungerManager().setFoodLevel(hunger);
        return Command.SINGLE_SUCCESS;
    }

    private static int setSaturation(PlayerEntity player, int saturation, boolean add)
    {
        if (add)
        {
            saturation += player.getHungerManager().getSaturationLevel();
        }
        player.getHungerManager().setSaturationLevelClient(saturation);
        return Command.SINGLE_SUCCESS;
    }

    private static int setWater(PlayerEntity player, int water, boolean add)
    {
        // todo
        throw new UnsupportedOperationException("Not implemented");
    }
}