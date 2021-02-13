/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonSerializer;

public class IsIsolatedCondition implements LootCondition
{
    public static final IsIsolatedCondition INSTANCE = new IsIsolatedCondition();

    private IsIsolatedCondition() {}

    @Override
    public LootConditionType getType()
    {
        return TFCLoot.IS_ISOLATED;
    }

    @Override
    public boolean test(LootContext context)
    {
        return context.hasParameter(TFCLoot.ISOLATED);
    }

    public static class Serializer implements JsonSerializer<IsIsolatedCondition>
    {
        @Override
        public void toJson(JsonObject json, IsIsolatedCondition condition, JsonSerializationContext context) {}

        @Override
        public IsIsolatedCondition fromJson(JsonObject json, JsonDeserializationContext context)
        {
            return IsIsolatedCondition.INSTANCE;
        }
    }
}
