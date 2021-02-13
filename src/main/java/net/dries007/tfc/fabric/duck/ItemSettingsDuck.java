package net.dries007.tfc.fabric.duck;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

import java.util.List;
import java.util.Map;

public interface ItemSettingsDuck {
    Item.Settings addToolType(Tag<Item> tooltag, int i);

    Map<Tag<Item>, Integer> getToolTags();
}
