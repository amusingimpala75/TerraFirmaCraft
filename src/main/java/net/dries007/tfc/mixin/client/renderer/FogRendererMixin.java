/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.client.TFCColors;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BackgroundRenderer.class)
public abstract class FogRendererMixin
{
    @Unique
    private static final BlockPos.Mutable POS = new BlockPos.Mutable();

    /**
     * Replace the call to {@link Biome#getWaterFogColor()} with one that has a position context
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getWaterFogColor()I"))
    private static int redirect$getWaterFogColor(Biome biome, Camera activeRenderInfoIn)
    {
        WorldAccess world = MinecraftClient.getInstance().world;
        if (world != null && TFCBiomes.getExtension(world, biome) != null)
        {
            return TFCColors.getWaterFogColor(activeRenderInfoIn.getBlockPos());
        }
        return biome.getWaterFogColor();
    }

    /**
     * Replace a call to {@link Biome#getFogColor()} with one that has a position context
     */
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getFogColor()I"))
    private static int redirect$getFogColor(Biome biome, ClientWorld world, BiomeAccess manager, float f, int biomeX, int biomeY, int biomeZ)
    {
        if (world != null && TFCBiomes.getExtension(world, biome) != null)
        {
            POS.set(biomeX << 2, biomeY << 2, biomeZ << 2);
            return TFCColors.getFogColor(POS);
        }
        return biome.getFogColor();
    }
}
