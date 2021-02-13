/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolMaterial;

public class TFCShieldItem extends ShieldItem
{
    private final ToolMaterial tier;

    public TFCShieldItem(ToolMaterial tier, Settings builder)
    {
        super(builder.maxDamage(tier.getDurability()));
        this.tier = tier;
    }

    public ToolMaterial getTier()
    {
        return this.tier;
    }

    @Override
    public int getEnchantability()
    {
        return this.tier.getEnchantability();
    }

    @Override
    public boolean canRepair(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }
}