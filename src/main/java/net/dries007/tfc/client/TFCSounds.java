/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

import net.dries007.tfc.util.Helpers;

public final class TFCSounds
{
    public static final SoundEvent ROCK_SLIDE_LONG = create("rock_slide_long");
    public static final SoundEvent ROCK_SLIDE_SHORT = create("rock_slide_short");
    public static final SoundEvent DIRT_SLIDE_SHORT = create("dirt_slide_short");

    private static SoundEvent create(String name)
    {
        return Registry.register(Registry.SOUND_EVENT, Helpers.identifier(name), new SoundEvent(Helpers.identifier(name)));
    }
}