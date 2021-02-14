/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;

public class TFCFarmlandBlock extends FarmlandBlock implements ISoilBlock, IForgeBlockProperties
{
    public static final IntProperty MOISTURE = Properties.MOISTURE;

    public static void turnToDirt(BlockState state, World worldIn, BlockPos pos)
    {
        worldIn.setBlockState(pos, pushEntitiesUpBeforeBlockChange(state, ((TFCFarmlandBlock) state.getBlock()).getDirt(), worldIn, pos));
    }

    private final ForgeBlockProperties properties;
    private final Supplier<? extends Block> dirt;

    public TFCFarmlandBlock(ForgeBlockProperties properties, SoilBlockType.Variant variant)
    {
        this(properties, () -> TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant));
    }

    public TFCFarmlandBlock(ForgeBlockProperties properties, Supplier<? extends Block> dirt)
    {
        super(properties.properties());

        this.properties = properties;
        this.dirt = dirt;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        final BlockState defaultState = getDefaultState();
        return defaultState.canPlaceAt(context.getWorld(), context.getBlockPos()) ? defaultState : getDirt();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.canPlaceAt(worldIn, pos))
        {
            // Turn to TFC farmland dirt
            turnToDirt(state, worldIn, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        // No-op
        // todo: trigger TE updates for moisture?
    }

    @Override
    public void onLandedUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        // No-op
    }

    @Override
    public BlockState getDirt()
    {
        return dirt.get().getDefaultState();
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
