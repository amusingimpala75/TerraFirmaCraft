package net.dries007.tfc.fabric.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public interface HeatComponent extends Component {
    /**
     * Gets the current temperature. Should call {@link HeatCapability#adjustTemp(float, float, long)} internally
     *
     * @return the temperature.
     */
    float getTemperature();

    /**
     * Sets the temperature. Used for anything that modifies the temperature.
     *
     * @param temperature the temperature to set.
     */
    void setTemperature(float temperature);

    /**
     * Gets the Heat capacity. (A measure of how fast this items heats up or cools down)
     * Implementation is left up to the heating object. (See TEFirePit for example)
     *
     * @return the heat capacity. Typically 0 - 1, can be outside this range, must be non-negative
     */
    float getHeatCapacity();

    /**
     * Gets the temperature at which this item can be worked in forging
     *
     * @return temperature at which this item is able to be worked
     */
    default float getForgingTemperature()
    {
        return 0;
    }

    /**
     * Gets the temperature at which this item can be welded in forging
     *
     * @return temperature at which this item is able to be welded
     */
    default float getWeldingTemperature()
    {
        return 0;
    }

    /**
     * Adds the heat info tooltip when hovering over.
     * When overriding this to show additional information, fall back to IItemHeat.super.addHeatInfo()
     *
     * @param stack The stack to add information to
     * @param text  The list of tooltips
     */
    @Environment(EnvType.CLIENT)
    default void addHeatInfo(ItemStack stack, List<Text> text)
    {
        float temperature = getTemperature();
        MutableText tooltip = Heat.getTooltip(temperature);
        if (tooltip != null)
        {
            // Only add " - can work" and " - can weld" if both temperatures are set
            if (getWeldingTemperature() > 0 && getWeldingTemperature() <= temperature)
            {
                tooltip.append(new TranslatableText(MOD_ID + ".tooltip.welding"));
            }
            else if (getForgingTemperature() > 0 && getForgingTemperature() <= temperature)
            {
                tooltip.append(new TranslatableText(MOD_ID + ".tooltip.forging"));
            }
            text.add(tooltip);
        }
    }
}
