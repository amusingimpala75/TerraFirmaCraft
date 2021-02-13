/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.item.ShearsItem;
import net.minecraft.item.ToolMaterial;

/**
 * Extends vanilla shears to add durability
 */
public class TFCShearsItem extends ShearsItem
{
    public TFCShearsItem(ToolMaterial tier, Settings builder)
    {
        super(builder.maxDamage(tier.getDurability()));
    }

    // todo implement
}