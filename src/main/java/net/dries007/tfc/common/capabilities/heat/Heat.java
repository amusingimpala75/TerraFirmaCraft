/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.heat;

import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import net.dries007.tfc.TerraFirmaCraft;
import org.jetbrains.annotations.Nullable;

public enum Heat
{
    WARMING(1f, 80f, Formatting.GRAY, Formatting.DARK_GRAY),
    HOT(80f, 210f, Formatting.GRAY, Formatting.DARK_GRAY),
    VERY_HOT(210f, 480f, Formatting.GRAY, Formatting.DARK_GRAY),
    FAINT_RED(480f, 580f, Formatting.DARK_RED),
    DARK_RED(580f, 730f, Formatting.DARK_RED),
    BRIGHT_RED(730f, 930f, Formatting.RED),
    ORANGE(930f, 1100f, Formatting.GOLD),
    YELLOW(1100f, 1300f, Formatting.YELLOW),
    YELLOW_WHITE(1300f, 1400f, Formatting.YELLOW),
    WHITE(1400f, 1500f, Formatting.WHITE),
    BRILLIANT_WHITE(1500f, 1601f, Formatting.WHITE);

    private static final Heat[] VALUES = values();

    public static float maxVisibleTemperature()
    {
        return BRILLIANT_WHITE.getMax();
    }

    @Nullable
    public static Heat getHeat(float temperature)
    {
        for (Heat heat : VALUES)
        {
            if (heat.min <= temperature && temperature < heat.max)
            {
                return heat;
            }
        }
        if (temperature > BRILLIANT_WHITE.max)
        {
            // Default to "hotter than brilliant white" for max
            return BRILLIANT_WHITE;
        }
        return null;
    }

    @Nullable
    public static MutableText getTooltipColorless(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        if (heat != null)
        {
            MutableText base = heat.getDisplayName();
            if (heat != Heat.BRILLIANT_WHITE)
            {
                for (int i = 1; i <= 4; i++)
                {
                    if (temperature <= heat.getMin() + ((float) i * 0.2f) * (heat.getMax() - heat.getMin()))
                        continue;
                    base.append("\u2605");
                }
            }
            return base;
        }
        return null;
    }

    @Nullable
    public static MutableText getTooltip(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        MutableText tooltip = getTooltipColorless(temperature);
        if (tooltip != null && heat != null)
        {
            tooltip.formatted(heat.format);
        }
        return tooltip;
    }

    @Nullable
    public static MutableText getTooltipAlternate(float temperature)
    {
        Heat heat = Heat.getHeat(temperature);
        MutableText tooltip = getTooltipColorless(temperature);
        if (tooltip != null && heat != null)
        {
            tooltip.formatted(heat.alternate);
        }
        return tooltip;
    }

    final Formatting format, alternate;
    private final float min;
    private final float max;

    Heat(float min, float max, Formatting format, Formatting alternate)
    {
        this.min = min;
        this.max = max;
        this.format = format;
        this.alternate = alternate;
    }

    Heat(float min, float max, Formatting format)
    {
        this(min, max, format, format);
    }

    public float getMin()
    {
        return min;
    }

    public float getMax()
    {
        return max;
    }

    public MutableText getDisplayName()
    {
        return new TranslatableText(TerraFirmaCraft.MOD_ID + ".enum.heat." + this.name().toLowerCase());
    }
}