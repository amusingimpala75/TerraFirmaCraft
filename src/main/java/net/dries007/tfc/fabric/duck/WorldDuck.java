package net.dries007.tfc.fabric.duck;

import net.minecraft.util.math.BlockPos;

public interface WorldDuck {
    boolean inject$isAreaLoaded(BlockPos pos, int radius);
}
