/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public class FarmlandTileEntity extends TFCTileEntity
{
    // NPK Nutrients
    private float nitrogen;
    private float phosphorous;
    private float potassium;

    public FarmlandTileEntity()
    {
        super(TFCTileEntities.FARMLAND);
        nitrogen = phosphorous = potassium = 0;
    }

    protected FarmlandTileEntity(BlockEntityType<?> type)
    {
        super(type);

        nitrogen = phosphorous = potassium = 0;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag nbt)
    {
        nitrogen = nbt.getFloat("nitrogen");
        phosphorous = nbt.getFloat("phosphorous");
        potassium = nbt.getFloat("potassium");
        super.fromTag(state, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt)
    {
        nbt.putFloat("nitrogen", nitrogen);
        nbt.putFloat("phosphorous", phosphorous);
        nbt.putFloat("potassium", potassium);
        return super.toTag(nbt);
    }

    public float getNitrogen()
    {
        return nitrogen;
    }

    public void setNitrogen(float nitrogen)
    {
        this.nitrogen = nitrogen;
    }

    public float getPhosphorous()
    {
        return phosphorous;
    }

    public void setPhosphorous(float phosphorous)
    {
        this.phosphorous = phosphorous;
    }

    public float getPotassium()
    {
        return potassium;
    }

    public void setPotassium(float potassium)
    {
        this.potassium = potassium;
    }
}
