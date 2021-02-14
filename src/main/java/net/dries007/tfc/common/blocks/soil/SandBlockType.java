/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.awt.*;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;

public enum SandBlockType
{
    BROWN(new Color(112, 113, 89).getRGB(), MaterialColor.DIRT),
    WHITE(new Color(202, 202, 201).getRGB(), MaterialColor.QUARTZ),
    BLACK(new Color(56, 56, 56).getRGB(), MaterialColor.BLACK_TERRACOTTA),
    RED(new Color(125, 99, 84).getRGB(), MaterialColor.RED_TERRACOTTA),
    YELLOW(new Color(215, 196, 140).getRGB(), MaterialColor.SAND),
    GREEN(new Color(106, 116, 81).getRGB(), MaterialColor.GREEN),
    PINK(new Color(150, 101, 97).getRGB(), MaterialColor.PINK_TERRACOTTA);

    private static final SandBlockType[] VALUES = values();

    public static SandBlockType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : BROWN;
    }

    private final int dustColor;
    private final MaterialColor materialColor;

    SandBlockType(int dustColor, MaterialColor materialColor)
    {
        this.dustColor = dustColor;
        this.materialColor = materialColor;
    }

    public int getDustColor()
    {
        return dustColor;
    }

    public Block create()
    {
        return new TFCSandBlock(dustColor, FabricBlockSettings.of(Material.AGGREGATE, materialColor).strength(0.5F).sounds(BlockSoundGroup.SAND).breakByTool(FabricToolTags.SHOVELS, 0));
    }
}