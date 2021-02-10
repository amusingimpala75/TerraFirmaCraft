/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import java.util.Collection;
import java.util.function.Supplier;

import net.dries007.tfc.util.Helpers;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;

@SuppressWarnings({"unused", "SameParameterValue"})
public class TFCTileEntities
{
    public static final BlockEntityType<FarmlandTileEntity> FARMLAND = register("farmland", FarmlandTileEntity::new, TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).values());
    public static final BlockEntityType<SnowPileTileEntity> SNOW_PILE = register("snow_pile", SnowPileTileEntity::new, TFCBlocks.SNOW_PILE);

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<T> factory, Block block)
    {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, Helpers.identifier(name), BlockEntityType.Builder.create(factory, block).build(null));
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<T> factory, Collection<? extends Block> blocks)
    {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, Helpers.identifier(name), BlockEntityType.Builder.create(factory, blocks.toArray(new Block[0])).build(null));
    }

    public static void register() {}
}
