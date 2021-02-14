package net.dries007.tfc.fabric.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.dries007.tfc.util.tracker.Collapse;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public interface WorldTrackerComponent extends Component {
    /**
     * Marks a position to be checked for a landslide on the next world tick
     */
    void addLandslidePos(BlockPos pos);

    /**
     * Marks a position to be checked for an isolated block on the next world tick
     */
    void addIsolatedPos(BlockPos pos);

    /**
     * Starts a collapse, which will propagate / continue over the next several iterations until finished
     */
    void addCollapseData(Collapse collapse);

    /**
     * Marks a series of positions for immediate collapse checks. Similar to starting a collapse but from specific positions.
     */
    void addCollapsePositions(BlockPos centerPos, Collection<BlockPos> positions);

    void tick(World world);
}
