/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.config.TFCConfig;
import org.jetbrains.annotations.Nullable;


public class DirtBlock extends Block implements IDirtBlock
{
    private final Supplier<? extends Block> grass;
    @Nullable
    private final Supplier<? extends Block> grassPath;
    @Nullable
    private final Supplier<? extends Block> farmland;

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.setBlockState(pos, getToolModifiedState(state, world, pos, player, player.getStackInHand(hand), player.getStackInHand(hand).getItem()));
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public DirtBlock(Settings properties, SoilBlockType grassType, SoilBlockType.Variant variant)
    {
        this(properties, () -> TFCBlocks.SOIL.get(grassType).get(variant), () -> TFCBlocks.SOIL.get(SoilBlockType.GRASS_PATH).get(variant), () -> TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).get(variant));
    }

    public DirtBlock(Settings properties, Supplier<? extends Block> grass, @Nullable Supplier<? extends Block> grassPath, @Nullable Supplier<? extends Block> farmland)
    {
        super(properties);

        this.grass = grass;
        this.grassPath = grassPath;
        this.farmland = farmland;
    }

    public BlockState getGrass()
    {
        return grass.get().getDefaultState();
    }

    @Nullable
    //@Override
    public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, Item toolType)
    {
        if (toolType.isIn(FabricToolTags.HOES) && TFCConfig.SERVER.enableFarmlandCreation.get() && farmland != null)
        {
            return farmland.get().getDefaultState();
        }
        else if (toolType.isIn(FabricToolTags.SHOVELS) && TFCConfig.SERVER.enableGrassPathCreation.get() && grassPath != null)
        {
            return grassPath.get().getDefaultState();
        }
        return state;
    }
}