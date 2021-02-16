/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.fabric.cca;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.capabilities.heat.HeatDefinition;
import net.dries007.tfc.common.capabilities.heat.HeatManager;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class HeatItemComponent extends ItemComponent implements HeatComponent {

    public HeatItemComponent(ItemStack stack) {
        super(stack);
    }

    /**
     * This gets the outwards facing temperature. It will differ from the internal temperature value or the value saved to NBT
     * Note: if checking the temperature internally, DO NOT use temperature, use this instead, as temperature does not represent the current temperature
     *
     * @return The current temperature
     */
    @Override
    public float getTemperature() {
        return Util.adjustTemp(this.getLong("heat"), getHeatCapacity(), Calendars.SERVER.getTicks() - this.getLong("ticks"));
    }

    /**
     * Update the temperature, and save the timestamp of when it was updated
     *
     * @param temperature the temperature to set. Between 0 - 1600
     */
    @Override
    public void setTemperature(float temperature) {
        this.putFloat("heat", temperature);
        this.putLong("temp", Calendars.SERVER.getTicks());
    }

    @Override
    public float getHeatCapacity() {
        Optional<HeatDefinition> def = HeatManager.CACHE.getAll(this.stack.getItem()).stream().findFirst();
        return def.map(heatDefinition -> heatDefinition.heatCapactiy).orElse(0.0F);
    }

    @Override
    public float getForgingTemperature() {
        Optional<HeatDefinition> def = HeatManager.CACHE.getAll(this.stack.getItem()).stream().findFirst();
        return def.map(heatDefinition -> heatDefinition.forgingTemp).orElse(0.0F);
    }

    @Override
    public float getWeldingTemperature() {
        Optional<HeatDefinition> def = HeatManager.CACHE.getAll(this.stack.getItem()).stream().findFirst();
        return def.map(heatDefinition -> heatDefinition.forgingTemp).orElse(0.0F);
    }

    /*@Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (getTemperature() <= 0) {
            // Reset temperature to zero
            nbt.putLong("ticks", 0);
            nbt.putFloat("heat", 0);
        } else {
            // Serialize existing values - this is intentionally lazy (and not using the result of getTemperature())
            // Why? So we don't update the serialization unnecessarily. Important for not sending unnecessary client syncs.
            nbt.putLong("ticks", lastUpdateTick);
            nbt.putFloat("heat", temperature);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt != null) {
            temperature = nbt.getFloat("heat");
            lastUpdateTick = nbt.getLong("ticks");
        }
    }*/

    public static class Util
    {
        /**
         * Helper method to adjust temperature towards a value, without overshooting or stuttering
         */
        public static float adjustTempTowards(float temp, float target, float delta)
        {
            return adjustTempTowards(temp, target, delta, delta);
        }

        public static float adjustTempTowards(float temp, float target, float deltaPositive, float deltaNegative)
        {
            if (temp < target)
            {
                return Math.min(temp + deltaPositive, target);
            }
            else if (temp > target)
            {
                return Math.max(temp - deltaNegative, target);
            }
            else
            {
                return target;
            }
        }

        /**
         * Call this from within {@link HeatComponent#getTemperature()}
         */
        public static float adjustTemp(float temp, float heatCapacity, long ticksSinceUpdate)
        {
            if (ticksSinceUpdate <= 0) return temp;
            final float newTemp = temp - heatCapacity * (float) (ticksSinceUpdate * TerraFirmaCraft.getConfig().serverConfig.mechanics.heat.itemHeatingModifier);
            return newTemp < 0 ? 0 : newTemp;
        }

        public static void addTemp(HeatComponent instance)
        {
            // Default modifier = 3 (2x normal cooling)
            addTemp(instance, 3);
        }

        /**
         * Use this to increase the heat on an IItemHeat instance.
         *
         * @param modifier the modifier for how much this will heat up: 0 - 1 slows down cooling, 1 = no heating or cooling, > 1 heats, 2 heats at the same rate of normal cooling, 2+ heats faster
         */
        public static void addTemp(HeatComponent instance, float modifier)
        {
            final float temp = instance.getTemperature() + modifier * instance.getHeatCapacity() * (float) TerraFirmaCraft.getConfig().serverConfig.mechanics.heat.itemHeatingModifier;
            instance.setTemperature(temp);
        }
    }
}
