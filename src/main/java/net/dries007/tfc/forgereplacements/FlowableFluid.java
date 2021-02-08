package net.dries007.tfc.forgereplacements;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class FlowableFluid extends net.minecraft.fluid.FlowableFluid {

    private final Fluid flow;
    private final Fluid still;
    private final boolean isInfinite;
    private final int flowSpeed;
    private final int decreasePerBlock;
    private final BucketItem bucketItem;
    private final int tickRate;
    private final Block blockToGet;
    private final float blastResistance;


    public FlowableFluid(FluidProperties properties) {
        this.flow = properties.getFlow();
        this.still = properties.getStill();
        this.isInfinite = properties.isInfinite();
        this.flowSpeed = properties.getFlowSpeed();
        this.decreasePerBlock = properties.getDecreasePerBlock();
        this.bucketItem = properties.getBucketItem();
        this.tickRate = properties.getTickRate();
        this.blockToGet = properties.getBlockToGet();
        this.blastResistance = properties.getBlastResistance();
    }
    @Override
    public Fluid getFlowing() {
        return flow;
    }

    @Override
    public Fluid getStill() {
        return still;
    }

    @Override
    protected boolean isInfinite() {
        return isInfinite;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.getBlock().hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return flowSpeed;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return decreasePerBlock;
    }

    @Override
    public Item getBucketItem() {
        return bucketItem;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(WorldView world) {
        return tickRate;
    }

    @Override
    protected float getBlastResistance() {
        return blastResistance;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return blockToGet.getDefaultState().with(Properties.LEVEL_15, method_15741(state));
    }

    public static class Flowing extends FlowableFluid {

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        public Flowing(FluidProperties properties) {
            super(properties);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }
    }

    public static class Still extends FlowableFluid {

        public Still(FluidProperties properties) {
            super(properties);
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }
    }
}
