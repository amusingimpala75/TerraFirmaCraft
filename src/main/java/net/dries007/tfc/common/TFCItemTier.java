/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public final class TFCItemTier implements ToolMaterial
{

    // Tier 0
    public static final ToolMaterial IGNEOUS_INTRUSIVE = new TFCItemTier(0, 60, 3.0f, 2.0f, 5);
    public static final ToolMaterial SEDIMENTARY = new TFCItemTier(0, 70, 3.0f, 2.0f, 5);
    public static final ToolMaterial IGNEOUS_EXTRUSIVE = new TFCItemTier(0, 50, 2.0f, 2.0f, 5);
    public static final ToolMaterial METAMORPHIC = new TFCItemTier(0, 55, 2.5f, 2.0f, 5);
    // Tier 1
    public static final ToolMaterial COPPER = new TFCItemTier(1, 600, 5, 3.25f, 8);
    // Tier 2
    public static final ToolMaterial BRONZE = new TFCItemTier(2, 1300, 8, 4.0f, 13);
    public static final ToolMaterial BISMUTH_BRONZE = new TFCItemTier(2, 1200, 7, 4.0f, 10);
    public static final ToolMaterial BLACK_BRONZE = new TFCItemTier(2, 1460, 6, 4.25f, 10);
    // Tier 3
    public static final ToolMaterial WROUGHT_IRON = new TFCItemTier(3, 2200, 10, 4.75f, 12);
    // Tier 4
    public static final ToolMaterial STEEL = new TFCItemTier(4, 3300, 12, 5.75f, 12);
    // Tier 5
    public static final ToolMaterial BLACK_STEEL = new TFCItemTier(5, 4200, 14, 7.0f, 17);
    // Tier 6
    public static final ToolMaterial BLUE_STEEL = new TFCItemTier(6, 6500, 16, 9.0f, 22);
    public static final ToolMaterial RED_STEEL = new TFCItemTier(6, 6500, 16, 9.0f, 22);

    private final int harvestLevel;
    private final int durability;
    private final float efficiency;
    private final float damage;
    private final int enchantability;

    private TFCItemTier(int harvestLevel, int durability, float efficiency, float damage, int enchantability)
    {
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.efficiency = efficiency;
        this.damage = damage;
        this.enchantability = enchantability;
    }

    @Override
    public int getDurability()
    {
        return durability;
    }

    @Override
    public float getMiningSpeedMultiplier()
    {
        return efficiency;
    }

    @Override
    public float getAttackDamage()
    {
        return damage;
    }

    @Override
    public int getMiningLevel()
    {
        return harvestLevel;
    }

    @Override
    public int getEnchantability()
    {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        // TFC items can't be repaired
        return Ingredient.EMPTY;
    }
}