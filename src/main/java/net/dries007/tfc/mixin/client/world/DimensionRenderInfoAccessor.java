/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.world;

import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.Identifier;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//@MethodsReturnNonnullByDefault
@Mixin(SkyProperties.class)
public interface DimensionRenderInfoAccessor
{
    @Accessor(value = "BY_IDENTIFIER")
    static Object2ObjectMap<Identifier, SkyProperties> accessor$Effects() { return null; }

    @Accessor(value = "cloudsHeight")
    void accessor$setCloudLevel(float cloudLevel);
}
