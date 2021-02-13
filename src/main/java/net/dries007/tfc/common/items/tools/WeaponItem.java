/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Generic class for weapons that shouldn't override vanilla's {@link SwordItem}
 * Possible reasons to avoid extending it: Sweeping effect, enchantments, other mods would think it is a sword.
 *
 * Also, TFC material damage is multiplicative instead of additive.
 * For comparison:
 * Vanilla: Tool value (ie: 3.0 for swords) + material damage value (ie: 2.0 for iron) + 1.0 (hand) = 6.0
 * TFC: Tool value (ie: 1.3 for maces) * material damage value (ie: 5.75 for steel) + 1.0 (hand) ~= 7.5
 */
public class WeaponItem extends ToolItem
{
    protected final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final float attackDamage;
    private final float attackSpeed;

    public WeaponItem(ToolMaterial tier, float attackDamageMultiplier, float attackSpeed, Item.Settings builder)
    {
        super(tier, builder);
        this.attackSpeed = attackSpeed;
        this.attackDamage = attackDamageMultiplier * tier.getAttackDamage();
        this.attributeModifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
            .put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", attackDamage, EntityAttributeModifier.Operation.ADDITION))
            .put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION))
            .build();
    }

    public float getAttackSpeed()
    {
        return attackSpeed;
    }

    public float getAttackDamage()
    {
        return attackDamage;
    }

    @Override
    public boolean canMine(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
    {
        return !player.isCreative();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        stack.damage(1, attacker, (entity) -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving)
    {
        if (state.getHardness(worldIn, pos) != 0.0F)
        {
            stack.damage(2, entityLiving, (entity) -> entity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot)
    {
        return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot);
    }
}