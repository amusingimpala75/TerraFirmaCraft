package net.dries007.tfc.mixin.fabric.item;

import net.dries007.tfc.fabric.duck.ItemSettingsDuck;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Item.Settings.class)
public class ItemAdditions implements ItemSettingsDuck {
    private final Map<Tag<Item>, Integer> toolTags = new HashMap<>();

    @Override
    public Item.Settings addToolType(Tag<Item> tooltag, int i) {
        toolTags.put(tooltag, i);
        return (Item.Settings) (Object) this;
    }

    @Override
    public Map<Tag<Item>, Integer> getToolTags() {
        return toolTags;
    }
}
