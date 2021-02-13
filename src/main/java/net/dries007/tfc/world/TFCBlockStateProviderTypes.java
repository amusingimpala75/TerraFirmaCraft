/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.dries007.tfc.mixin.fabric.world.BlockStateProviderAccessor;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class TFCBlockStateProviderTypes
{
    public static final BlockStateProviderType<FacingHorizontalBlockStateProvider> FACING_PROVIDER = BlockStateProviderAccessor.call$register(Helpers.identifier("facing_random").toString(), FacingHorizontalBlockStateProvider.CODEC);

    public static void register() {}
}
