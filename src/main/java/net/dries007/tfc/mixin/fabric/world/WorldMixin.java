package net.dries007.tfc.mixin.fabric.world;

import net.dries007.tfc.fabric.duck.WorldDuck;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(WorldView.class)
public abstract class WorldMixin implements WorldDuck {

    @Shadow
    @Deprecated
    public abstract boolean isRegionLoaded(BlockPos min, BlockPos max);

    @Override
    public boolean inject$isAreaLoaded(BlockPos pos, int radius) {
        return this.isRegionLoaded(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));
    }
}
