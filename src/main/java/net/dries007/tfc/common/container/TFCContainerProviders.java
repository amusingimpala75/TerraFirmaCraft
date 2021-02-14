/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.TranslatableText;

import net.dries007.tfc.util.Helpers;

/**
 * {@link NamedScreenHandlerFactory} for static screen / container pairs that are not attached to a TE or other object which makes sense to implement this on.
 */
public class TFCContainerProviders
{
    public static final NamedScreenHandlerFactory CALENDAR = Helpers.createNamedContainerProvider(new TranslatableText("tfc.screen.calendar"), (windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.CALENDAR, windowId, player.inventory));
    public static final NamedScreenHandlerFactory NUTRITION = Helpers.createNamedContainerProvider(new TranslatableText("tfc.screen.nutrition"), (windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.NUTRITION, windowId, player.inventory));
    public static final NamedScreenHandlerFactory CLIMATE = Helpers.createNamedContainerProvider(new TranslatableText("tfc.screen.climate"), (windowId, inv, player) -> new SimpleContainer(TFCContainerTypes.CLIMATE, windowId, player.inventory));
}