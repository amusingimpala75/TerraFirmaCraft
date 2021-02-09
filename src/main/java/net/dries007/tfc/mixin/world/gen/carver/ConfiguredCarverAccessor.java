/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.world.gen.carver;

import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConfiguredCarver.class)
public interface ConfiguredCarverAccessor
{
    /**
     * The accessor has been inline / removed and it is needed to provide additional carving context
     */
    @Accessor("carver")
    Carver<?> accessor$getWorldCarver();
}
