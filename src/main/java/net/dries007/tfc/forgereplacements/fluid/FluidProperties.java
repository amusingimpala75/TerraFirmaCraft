/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.forgereplacements.fluid;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.util.Lazy;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class FluidProperties {
    private Fluid flow;
    private Fluid still;
    private Lazy<BucketItem> bucketItem;
    private Lazy<Block> blockToGet;
    private boolean isInfinite = false;
    private int flowSpeed = 4;
    private int decreasePerBlock = 1;
    private int tickRate = 5;
    private float blastResistance = 100.0F;

    public FluidProperties(Fluid flow, Fluid still) {
        this.flow = flow;
        this.still = still;
    }

    public FluidProperties setFlowSpeed(int s) {
        this.flowSpeed = s;
        return this;
    }

    public FluidProperties setDecreasePerBlock(int d) {
        this.decreasePerBlock = d;
        return this;
    }

    public FluidProperties setInfinite(boolean i) {
        this.isInfinite = i;
        return this;
    }

    public FluidProperties setTickRate(int r) {
        this.tickRate = r;
        return this;
    }

    public FluidProperties setBlastResistance(int r) {
        this.blastResistance = r;
        return this;
    }

    public FluidProperties setBucket(Lazy<BucketItem> b) {
        this.bucketItem = b;
        return this;
    }

    public FluidProperties setBlock(Lazy<Block> b) {
        this.blockToGet = b;
        return this;
    }
    public Fluid getFlow() {
        return flow;
    }

    public Fluid getStill() {
        return still;
    }

    public Lazy<BucketItem> getBucketItem() {
        return bucketItem;
    }

    public Lazy<Block> getBlockToGet() {
        return blockToGet;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public int getFlowSpeed() {
        return flowSpeed;
    }

    public int getDecreasePerBlock() {
        return decreasePerBlock;
    }

    public int getTickRate() {
        return tickRate;
    }

    public float getBlastResistance() {
        return blastResistance;
    }
}
