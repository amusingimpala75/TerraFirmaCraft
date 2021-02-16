/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.fabric.duck;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

import java.util.List;
import java.util.Map;

public interface ItemSettingsDuck {
    Item.Settings addToolType(Tag<Item> tooltag, int i);

    Map<Tag<Item>, Integer> getToolTags();
}
