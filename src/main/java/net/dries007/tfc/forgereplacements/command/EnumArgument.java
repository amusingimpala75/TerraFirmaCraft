/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.forgereplacements.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {

    private final Class<T> clazz;

    private EnumArgument(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <U extends Enum<U>> EnumArgument<U> enumArgument(Class<U> clazz) {
        return new EnumArgument<>(clazz);
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        return Enum.valueOf(clazz, reader.readUnquotedString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Stream.of(clazz.getEnumConstants()).map(Object::toString), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Stream.of(clazz.getEnumConstants()).map(Objects::toString).collect(Collectors.toList());
    }
}
