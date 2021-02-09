/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.command;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public final class TimeCommand
{
    private static final String DAYTIME = "tfc.commands.time.query.daytime";
    private static final String GAME_TIME = "tfc.commands.time.query.game_time";
    private static final String DAY = "tfc.commands.time.query.day";
    private static final String PLAYER_TICKS = "tfc.commands.time.query.player_ticks";
    private static final String CALENDAR_TICKS = "tfc.commands.time.query.calendar_ticks";

    public static LiteralArgumentBuilder<ServerCommandSource> create()
    {
        return CommandManager.literal("time")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("monthlength")
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> setMonthLength(IntegerArgumentType.getInteger(context, "value")))
                    )
                )
                .then(CommandManager.literal("day")
                    .executes(context -> setTime(context.getSource().getMinecraftServer(), 1000))
                )
                .then(CommandManager.literal("noon")
                    .executes(context -> setTime(context.getSource().getMinecraftServer(), 6000))
                )
                .then(CommandManager.literal("night")
                    .executes(context -> setTime(context.getSource().getMinecraftServer(), 13000))
                )
                .then(CommandManager.literal("midnight")
                    .executes(context -> setTime(context.getSource().getMinecraftServer(), 18000))
                )
            )
            .then(CommandManager.literal("add")
                .then(CommandManager.literal("years")
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * Calendars.SERVER.getCalendarTicksInYear()))
                    )
                )
                .then(CommandManager.literal("months")
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * Calendars.SERVER.getCalendarTicksInMonth()))
                    )
                )
                .then(CommandManager.literal("days")
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value") * ICalendar.TICKS_IN_DAY))
                    )
                )
                .then(CommandManager.literal("ticks")
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> addTime(IntegerArgumentType.getInteger(context, "value")))
                    )
                )
            )
            .then(CommandManager.literal("query")
                .then(CommandManager.literal("daytime")
                    .executes(context -> sendQueryResults(context.getSource(), DAYTIME, Calendars.SERVER.getCalendarDayTime()))
                )
                .then(CommandManager.literal("gametime")
                    .executes(context -> sendQueryResults(context.getSource(), GAME_TIME, context.getSource().getWorld().getTime()))
                )
                .then(CommandManager.literal("day")
                    .executes(context -> sendQueryResults(context.getSource(), DAY, Calendars.SERVER.getTotalDays()))
                )
                .then(CommandManager.literal("ticks")
                    .executes(context -> sendQueryResults(context.getSource(), PLAYER_TICKS, Calendars.SERVER.getTicks()))
                )
                .then(CommandManager.literal("calendarticks")
                    .executes(context -> sendQueryResults(context.getSource(), CALENDAR_TICKS, Calendars.SERVER.getCalendarTicks()))
                )
            );
    }

    private static int setMonthLength(int months)
    {
        Calendars.SERVER.setMonthLength(months);
        return Command.SINGLE_SUCCESS;
    }

    private static int setTime(MinecraftServer server, int dayTime)
    {
        for (ServerWorld world : server.getWorlds())
        {
            long dayTimeJump = dayTime - (world.getTimeOfDay() % ICalendar.TICKS_IN_DAY);
            if (dayTimeJump < 0)
            {
                dayTimeJump += ICalendar.TICKS_IN_DAY;
            }
            world.setTimeOfDay(world.getTimeOfDay() + dayTimeJump);
        }
        Calendars.SERVER.setTimeFromDayTime(dayTime);
        return Command.SINGLE_SUCCESS;
    }

    private static int addTime(long ticksToAdd)
    {
        Calendars.SERVER.setTimeFromCalendarTime(Calendars.SERVER.getCalendarTicks() + ticksToAdd);
        return Command.SINGLE_SUCCESS;
    }

    private static int sendQueryResults(CommandSource source, String translationKey, long value)
    {
        ((ServerCommandSource)source).sendFeedback(new TranslatableText(translationKey, (int) value), false);
        return Command.SINGLE_SUCCESS;
    }
}