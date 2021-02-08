/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.dries007.tfc.util.Helpers;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public final class TFCContainerTypes {
    public static final ScreenHandlerType<SimpleContainer> CALENDAR = register("calendar", (windowId, inv) -> new SimpleContainer(TFCContainerTypes.CALENDAR, windowId, inv));
    public static final ScreenHandlerType<SimpleContainer> NUTRITION = register("nutrition", ((windowId, inv) -> new SimpleContainer(TFCContainerTypes.NUTRITION, windowId, inv)));
    public static final ScreenHandlerType<SimpleContainer> CLIMATE = register("climate", ((windowId, inv) -> new SimpleContainer(TFCContainerTypes.CLIMATE, windowId, inv)));

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ScreenHandlerRegistry.SimpleClientHandlerFactory<T> factory) {
        //return CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
        return ScreenHandlerRegistry.registerSimple(Helpers.identifier(name), factory);
    }

    public static void register() {}
}