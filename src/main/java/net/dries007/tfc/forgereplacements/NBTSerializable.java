package net.dries007.tfc.forgereplacements;

import net.minecraft.nbt.Tag;

public interface NBTSerializable<T extends Tag> {
    T serialize();
    void deserialize(T tag);
}
