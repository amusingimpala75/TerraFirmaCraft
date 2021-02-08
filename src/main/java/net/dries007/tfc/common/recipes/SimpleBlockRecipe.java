/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;


import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.recipe.RecipeSerializer;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;


/**
 * Generic class for single block -> block based in-world crafting recipes.
 */
public abstract class SimpleBlockRecipe implements IBlockRecipe
{
    protected final Identifier id;
    protected final IBlockIngredient ingredient;
    protected final BlockState outputState;
    protected final boolean copyInputState;

    public SimpleBlockRecipe(Identifier id, IBlockIngredient ingredient, BlockState outputState, boolean copyInputState)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.outputState = outputState;
        this.copyInputState = copyInputState;
    }

    @Override
    public boolean matches(World worldIn, BlockPos pos, BlockState state)
    {
        return ingredient.test(state);
    }

    @Override
    public BlockState getBlockCraftingResult(BlockRecipeWrapper wrapper)
    {
        return copyInputState ? wrapper.getState() : outputState;
    }

    @Override
    public Block getBlockRecipeOutput()
    {
        return outputState.getBlock();
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    public IBlockIngredient getBlockIngredient()
    {
        return ingredient;
    }

    public static class Serializer<R extends SimpleBlockRecipe> implements RecipeSerializer<R>
    {
        private final Factory<R> factory;

        public Serializer(Factory<R> factory)
        {
            this.factory = factory;
        }

        @Override
        public R read(Identifier recipeId, JsonObject json)
        {
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(json.get("ingredient"));
            boolean copyInputState = JsonHelper.getBoolean(json, "copy_input", false);
            BlockState state;
            if (!copyInputState)
            {
                state = Helpers.readBlockState(JsonHelper.getString(json, "result"));
            }
            else
            {
                state = Blocks.AIR.getDefaultState();
            }
            return factory.create(recipeId, ingredient, state, copyInputState);
        }

        @Nullable
        @Override
        public R read(Identifier recipeId, PacketByteBuf buffer)
        {
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(buffer);
            boolean copyInputState = buffer.readBoolean();
            BlockState state;
            if (!copyInputState)
            {
                state = Registry.BLOCK.get(buffer.readIdentifier()).getDefaultState();
            }
            else
            {
                state = Blocks.AIR.getDefaultState();
            }
            return factory.create(recipeId, ingredient, state, copyInputState);
        }

        @Override
        public void write(PacketByteBuf buffer, R recipe)
        {
            IBlockIngredient.Serializer.INSTANCE.write(buffer, recipe.ingredient);
            buffer.writeBoolean(recipe.copyInputState);
            if (!recipe.copyInputState)
            {
                buffer.writeIdentifier(Registry.BLOCK.getId(recipe.outputState.getBlock()));
            }
        }

        protected interface Factory<R extends SimpleBlockRecipe>
        {
            R create(Identifier id, IBlockIngredient ingredient, BlockState state, boolean copyInputState);
        }
    }
}