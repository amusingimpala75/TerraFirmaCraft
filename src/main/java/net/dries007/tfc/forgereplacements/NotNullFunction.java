package net.dries007.tfc.forgereplacements;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface NotNullFunction<R, K> {

    @NotNull
    K run(@NotNull R r);

}
