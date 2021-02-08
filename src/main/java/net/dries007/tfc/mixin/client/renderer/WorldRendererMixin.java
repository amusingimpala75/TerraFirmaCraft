/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.renderer;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.util.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
    @Unique
    private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
    @Shadow
    private ClientWorld world;

    /**
     * Redirect the call to {@link Biome#getTemperature(BlockPos)} with one that has a position and world context
     */
    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    private float redirect$renderSnowAndRain$getTemperature(Biome biome, BlockPos pos)
    {
        return Climate.getVanillaBiomeTemperature(biome, world, pos);
    }

    /**
     * Redirect the call to {@link Biome#getPrecipitation()} with one that has a position and world context
     */
    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    private Biome.Precipitation redirect$renderSnowAndRain$getPrecipitation(Biome biome, LightmapTextureManager lightmapIn, float partialTicks, double xIn, double yIn, double zIn)
    {
        mutablePos.set(xIn, yIn, zIn);
        return Climate.getVanillaBiomePrecipitation(biome, world, mutablePos);
    }

    /**
     * Redirect the call to {@link Biome#getTemperature(BlockPos)} with one that has a position and world context
     */
    @Redirect(method = "tickRainSplashing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F"))
    private float redirect$tickRain$getTemperature(Biome biome, BlockPos pos)
    {
        return Climate.getVanillaBiomeTemperature(biome, world, pos);
    }

    /**
     * Redirect the call to {@link Biome#getPrecipitation()} with one that has a position and world context
     */
    @Redirect(method = "tickRainSplashing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;"))
    private Biome.Precipitation redirect$tickRain$getPrecipitation(Biome biome, Camera activeRenderInfo)
    {
        return Climate.getVanillaBiomePrecipitation(biome, world, activeRenderInfo.getBlockPos());
    }
}
