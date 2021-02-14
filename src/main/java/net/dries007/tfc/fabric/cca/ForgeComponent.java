package net.dries007.tfc.fabric.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgeSteps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface ForgeComponent extends Component
{
        /**
         * Gets the current amount of work on the object
         */
        int getWork();

        /**
         * Sets the current amount of work on the object
         */
        void setWork(int work);

        /**
         * Gets the current saved recipe's registry name
         * Returns null if no recipe name is currently saved
         */
        @Nullable
        Identifier getRecipeName();

        /** todo: requires anvil recipes
         * Sets the recipe name from an {@link AnvilRecipe}. If null, sets the recipe name to null
         */
    /*
    default void setRecipe(@Nullable AnvilRecipe recipe)
    {
        setRecipe(recipe != null ? recipe.getRegistryName() : null);
    }
    */

        /**
         * Sets the recipe name from an AnvilRecipe registry name.
         *
         * @param recipeName a registry name of an anvil recipe
         */
        void setRecipe(@Nullable Identifier recipeName);

        /**
         * Gets the last three steps, wrapped in a {@link ForgeSteps} instance.
         * The return value is nonnull, however the individual steps might be
         */
        ForgeSteps getSteps();

        /**
         * Adds a step to the object, shuffling the last three steps down
         *
         * @param step The step to add. In general this should not be null, although it is perfectly valid for it to be
         */
        void addStep(@Nullable ForgeStep step);

        /**
         * Resets the object's {@link IForging} components. Used if an item falls out of an anvil without getting worked
         * Purpose is to preserve stackability on items that haven't been worked yet.
         */
        void reset();

        /**
         * @return true if the item is workable
         */
        default boolean canWork(ItemStack stack)
        {
            return Components.HEAT_COMPONENT.maybeGet(stack).map(heat -> heat.getTemperature() > heat.getForgingTemperature()).orElse(true);
        }

        /**
         * @return true if the item is weldable
         */
        default boolean canWeld(ItemStack stack)
        {
            return Components.HEAT_COMPONENT.maybeGet(stack).map(heat -> heat.getTemperature() > heat.getWeldingTemperature()).orElse(true);
        }
}
