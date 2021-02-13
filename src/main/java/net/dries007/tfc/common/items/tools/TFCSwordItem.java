/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

/**
 * This is needed so we override the damage math done by vanilla
 *
 * For comparison:
 * Vanilla: Tool value (ie: 3.0 for swords) + material damage value (ie: 2.0 for iron) + 1.0 (hand) = 6.0
 * TFC: Tool value (ie: 1.3 for maces) * material damage value (ie: 5.75 for steel) + 1.0 (hand) ~= 7.5
 */
public class TFCSwordItem extends SwordItem
{
    protected final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final float attackDamage;
    private final float attackSpeed;

    public TFCSwordItem(ToolMaterial tier, float attackDamageMultiplier, float attackSpeed, Settings builder)
    {
        super(tier, 0, attackSpeed, builder);
        this.attackDamage = attackDamageMultiplier * tier.getAttackDamage();
        this.attackSpeed = attackSpeed;
        this.attributeModifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
            .put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", attackDamage, EntityAttributeModifier.Operation.ADDITION))
            .put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION))
            .build();
    }

    public float getAttackSpeed()
    {
        return attackSpeed;
    }

    @Override
    public float getAttackDamage()
    {
        return attackDamage;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot)
    {
        return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot);
    }
}