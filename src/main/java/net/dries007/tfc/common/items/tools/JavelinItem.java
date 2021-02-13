/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;

public class JavelinItem extends WeaponItem
{
    public JavelinItem(ToolMaterial tier, float attackDamageMultiplier, float attackSpeed, Item.Settings builder)
    {
        super(tier, attackDamageMultiplier, attackSpeed, builder);
    }

    // todo implement throwing
}