/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.dries007.tfc.util.Helpers;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.block.Block;

import net.dries007.tfc.util.data.DataManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class RockManager extends DataManager<Rock> implements IdentifiableResourceReloadListener
{
    public static final RockManager INSTANCE = new RockManager();

    private final Map<Block, Rock> rockBlocks;

    private RockManager()
    {
        super(new GsonBuilder().create(), "rocks", "rock", false);

        rockBlocks = new HashMap<>();
    }

    /**
     * Gets the rock from a block from a O(1) lookup, rather than iterating the list of rocks
     */
    @Nullable
    public Rock getRock(Block block)
    {
        return rockBlocks.get(block);
    }

    @Override
    protected Rock read(Identifier id, JsonObject obj)
    {
        return new Rock(id, obj);
    }

    @Override
    protected void postProcess()
    {
        rockBlocks.clear();
        for (Rock rock : types.values())
        {
            for (Rock.BlockType blockType : Rock.BlockType.values())
            {
                rockBlocks.put(rock.getBlock(blockType), rock);
            }
        }

        super.postProcess();

        if (getValues().isEmpty())
        {
            throw new IllegalStateException("Something went badly wrong... There are no rocks. This cannot be.");
        }
    }

    @Override
    public Identifier getFabricId() {
        return Helpers.identifier("data_listener/rock_manager");
    }
}