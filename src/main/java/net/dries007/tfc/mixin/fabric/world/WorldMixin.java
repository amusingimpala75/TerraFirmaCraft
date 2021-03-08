/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.world;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.fabric.cca.Components;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;", at=@At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void inject$addLandslideCheck(Entity entity, DamageSource damageSource, ExplosionBehavior explosionBehavior, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType, CallbackInfoReturnable<Explosion> cir, Explosion explosion)
    {
        if (!entity.getEntityWorld().isClient)
        {
            //event.getWorld().getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addCollapsePositions(new BlockPos(event.getExplosion().getPosition()), event.getAffectedBlocks()));
            Components.WORLD_TRACKING.maybeGet(entity.getEntityWorld()).ifPresent(cap -> cap.addCollapsePositions(new BlockPos(d, e, f), explosion.getAffectedBlocks()));
        }
    }

    /*@Inject(method = "updateNeighbor", at=@At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$AbstractBlockState;updateNeighbhor(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V", shift = At.Shift.AFTER))
    public void inject$updateComponentsListener(BlockPos sourcePos, Block sourceBlock, BlockPos neighborPos, CallbackInfo ci)
    {
        final ServerWorld world = (ServerWorld) (Object) this;
        //for (Direction direction : event.getNotifiedSides())
        //{
            // Check each notified block for a potential gravity block
            final BlockPos pos = neighborPos;
            final BlockState state = world.getBlockState(pos);

            if (TFCTags.Blocks.CAN_LANDSLIDE.contains(state.getBlock()))
            {
                //world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
                Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.addLandslidePos(pos));
            }

            if (TFCTags.Blocks.BREAKS_WHEN_ISOLATED.contains(state.getBlock()))
            {
                //world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addIsolatedPos(pos));
                Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.addIsolatedPos(pos));
            }
        //}
    }*/

    @Inject(method = "updateNeighborsAlways", at=@At("TAIL"))
    public void inject$updateComponents(BlockPos pos1, Block block, CallbackInfo ci)
    {
        for (Direction direction : Direction.values())
        {
            updateLandslideAndIsolated(pos1.offset(direction));
        }
    }

    @Inject(method = "updateNeighborsExcept", at=@At("TAIL"))
    public void inject$updateComponentsPartialDirection(BlockPos pos1, Block sourceBlock, Direction direction2, CallbackInfo ci)
    {
        for (Direction direction : Direction.values())
        {
            if (direction.equals(direction2))
            {
                continue;
            }
            updateLandslideAndIsolated(pos1.offset(direction));
        }
    }

    public void updateLandslideAndIsolated(BlockPos pos)
    {
        final ServerWorld world = (ServerWorld) (Object) this;
            final BlockState state = world.getBlockState(pos);

            if (TFCTags.Blocks.CAN_LANDSLIDE.contains(state.getBlock()))
            {
                //world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addLandslidePos(pos));
                Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.addLandslidePos(pos));
            }

            if (TFCTags.Blocks.BREAKS_WHEN_ISOLATED.contains(state.getBlock()))
            {
                //world.getCapability(WorldTrackerCapability.CAPABILITY).ifPresent(cap -> cap.addIsolatedPos(pos));
                Components.WORLD_TRACKING.maybeGet(world).ifPresent(comp -> comp.addIsolatedPos(pos));
            }
    }
}
