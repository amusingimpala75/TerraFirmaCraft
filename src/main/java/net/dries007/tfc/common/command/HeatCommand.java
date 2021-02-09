/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public final class HeatCommand
{
    private static final String QUERY_HEAT = "tfc.commands.heat.set_heat";

    public static LiteralArgumentBuilder<ServerCommandSource> create()
    {
        return CommandManager.literal("heat").requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                .executes(cmd -> heatItem(cmd.getSource(), IntegerArgumentType.getInteger(cmd, "value")))
            );
    }

    private static int heatItem(CommandSource source, int value) throws CommandSyntaxException
    {
        final ServerPlayerEntity player = ((ServerCommandSource)source).getPlayer();
        final ItemStack stack = player.getMainHandStack();
        if (!stack.isEmpty())
        {
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(heat ->
            {
                heat.setTemperature(value);
                ((ServerCommandSource) source).sendFeedback(new TranslatableText(QUERY_HEAT, value), true);
            });
        }
        return Command.SINGLE_SUCCESS;
    }
}