/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class TypedDataManager<T> extends DataManager<T>
{
    protected final Map<Identifier, BiFunction<Identifier, JsonObject, ? extends T>> deserializers;

    protected TypedDataManager(Gson gson, String domain, String typeName, boolean allowNone)
    {
        super(gson, domain, typeName, allowNone);

        this.deserializers = new HashMap<>();
    }

    public void register(Identifier name, BiFunction<Identifier, JsonObject, ? extends T> deserializer)
    {
        if (!deserializers.containsKey(name))
        {
            LOGGER.info("Registered {}: {}", typeName, name);
            deserializers.put(name, deserializer);
        }
        else
        {
            LOGGER.info("Denied registration of {}: {} as it would overwrite an existing entry!", typeName, name);
        }
    }

    @Override
    protected T read(Identifier id, JsonObject json)
    {
        Identifier type;
        if (json.has("type"))
        {
            type = new Identifier(JsonHelper.getString(json, "type"));
        }
        else
        {
            type = getFallbackType();
            if (type == null)
            {
                throw new JsonParseException("missing type id, and this deserializer does not have a fallback type!");
            }
        }
        if (deserializers.containsKey(type))
        {
            return deserializers.get(type).apply(id, json);
        }
        else
        {
            throw new JsonParseException("Unknown " + typeName + ": " + type);
        }
    }

    @Nullable
    protected Identifier getFallbackType()
    {
        return null;
    }
}