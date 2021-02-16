/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.fabric.duck;

import net.minecraft.util.math.BlockPos;

public interface WorldDuck {
    boolean inject$isAreaLoaded(BlockPos pos, int radius);
}
