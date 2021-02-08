/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.BiFunction;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.tileentity.FarmlandTileEntity;
import net.minecraft.sound.BlockSoundGroup;

public enum SoilBlockType
{
    DIRT((self, variant) -> new DirtBlock(AbstractBlock.Settings.of(Material.SOIL, MaterialColor.DIRT).strength(0.5F).sounds(BlockSoundGroup.GRAVEL), self.transform(), variant)),
    GRASS((self, variant) -> new ConnectedGrassBlock(Block.Settings.of(Material.SOLID_ORGANIC).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS), self.transform(), variant)),
    GRASS_PATH((self, variant) -> new TFCGrassPathBlock(Block.Settings.of(Material.SOIL).strength(0.65F).sounds(BlockSoundGroup.GRASS), self.transform(), variant)),
    CLAY((self, variant) -> new DirtBlock(Block.Settings.of(Material.SOIL, MaterialColor.DIRT).strength(0.5F).sounds(BlockSoundGroup.GRAVEL), self.transform(), variant)),
    CLAY_GRASS((self, variant) -> new ConnectedGrassBlock(Block.Settings.of(Material.SOLID_ORGANIC).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS), self.transform(), variant)),
    FARMLAND((self, variant) -> new TFCFarmlandBlock(new ForgeBlockProperties(AbstractBlock.Settings.of(Material.SOIL).strength(0.6f).sounds(BlockSoundGroup.GRAVEL).blockVision(TFCBlocks::always).suffocates(TFCBlocks::always)).tileEntity(FarmlandTileEntity::new), variant));

    public static final SoilBlockType[] VALUES = values();

    public static SoilBlockType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : DIRT;
    }

    private final BiFunction<SoilBlockType, Variant, Block> factory;

    SoilBlockType(BiFunction<SoilBlockType, Variant, Block> factory)
    {
        this.factory = factory;
    }

    public Block create(Variant variant)
    {
        return factory.apply(this, variant);
    }

    /**
     * Gets the transformed state between grass and dirt variants. Used to subvert shitty compiler illegal forward reference errors.
     */
    private SoilBlockType transform()
    {
        switch (this)
        {
            case DIRT:
                return GRASS;
            case GRASS:
            case GRASS_PATH:
            case FARMLAND:
                return DIRT;
            case CLAY:
                return CLAY_GRASS;
            case CLAY_GRASS:
                return CLAY;
        }
        throw new IllegalStateException("SoilBlockType." + name() + " missing from switch in SoilBlockType#transform");
    }

    public enum Variant
    {
        SILT,
        LOAM,
        SANDY_LOAM,
        SILTY_LOAM;

        private static final Variant[] VALUES = values();

        public static Variant valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : SILT;
        }
    }
}