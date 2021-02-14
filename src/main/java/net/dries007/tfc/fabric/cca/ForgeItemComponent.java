package net.dries007.tfc.fabric.cca;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgeSteps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ForgeItemComponent extends ItemComponent implements ForgeComponent {

    public ForgeItemComponent(ItemStack stack) {
        super(stack);
    }

    @Override
    public int getWork()
    {
        CompoundTag tag = getOrCreateRootTag();
        if (tag.contains("forging"))
        {
            return tag.getCompound("forging").getInt("work");
        }
        return 0;
    }

    @Override
    public void setWork(int work)
    {
        getOrCreateRootTag().putInt("work", work);
        checkEmpty();
    }

    @Nullable
    @Override
    public Identifier getRecipeName()
    {
        CompoundTag tag = getOrCreateRootTag();
        if (tag.contains("forging") && tag.getCompound("forging").contains("recipe"))
        {
            return new Identifier(tag.getCompound("forging").getString("recipe"));
        }
        return null;
    }

    @Override
    public void setRecipe(@Nullable Identifier recipeName)
    {
        if (recipeName == null)
        {
            getOrCreateRootTag().remove("recipe");
            checkEmpty();
        }
        else
        {
            getOrCreateRootTag().putString("recipe", recipeName.toString());
        }
    }

    @Override
    public ForgeSteps getSteps()
    {
        CompoundTag tag = getOrCreateRootTag();
        if (tag.contains("forging"))
        {
            return ForgeSteps.get(tag.getCompound("forging").getCompound("steps"));
        }
        return ForgeSteps.empty();
    }

    @Override
    public void addStep(@Nullable ForgeStep step)
    {
        getOrCreateRootTag().put("steps", ForgeSteps.get(getOrCreateRootTag().getCompound("steps")).addStep(step).serialize());
        checkEmpty();
    }

    @Override
    public void reset()
    {
        CompoundTag tag = getOrCreateRootTag();
        tag.remove("forging");
        // Also, removes nbt data from container item if there's nothing there
        //if (getOrCreateRootTag().isEmpty())
        //{
        //    container.setTag(null);
        //}
    }

    /*@Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return ForgingCapability.CAPABILITY.orEmpty(cap, capability);
    }*/

    /**
     * Initialize tag if needed, returns a tag with forging data
     * Only call this when adding work / forge step
     */
    private CompoundTag getForgingTag()
    {
        CompoundTag tag = getOrCreateRootTag();
        tag.put("forging", new CompoundTag());
        tag.getCompound("forging").put("steps", new CompoundTag());
        return tag.getCompound("forging");
    }

    private void checkEmpty()
    {
        // Checks if the capability is empty and resets the container tag
        CompoundTag tag = getOrCreateRootTag();
        if (tag.contains("forging"))
        {
            if (getWork() == 0 && !getSteps().hasWork() && getRecipeName() == null)
            {
                reset();
            }
        }
    }
}
