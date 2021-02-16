/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fabric.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.IcebergFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(IcebergFeature.class)
public interface IcebergFeatureAccessor {
    @Invoker("method_13424")
    double call$method_13424(int i, int j, BlockPos blockPos, int k, int l, double d);

    @Invoker("isSnowyOrIcy")
    boolean call$isSnowyOrIcy(Block block);

    @Invoker("clearSnowAbove")
    void call$clearSnowAbove(WorldAccess world, BlockPos pos);
}
