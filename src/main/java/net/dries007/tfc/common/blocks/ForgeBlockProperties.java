/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ForgeBlockProperties
{
    private final AbstractBlock.Settings properties;

    @Nullable
    private Supplier<? extends BlockEntity> tileEntityFactory;
    private int flammability;
    private int fireSpreadSpeed;

    public ForgeBlockProperties(AbstractBlock.Settings properties)
    {
        this.properties = properties;

        tileEntityFactory = null;
        flammability = 0;
        fireSpreadSpeed = 0;
    }

    public ForgeBlockProperties tileEntity(Supplier<? extends BlockEntity> tileEntityFactory)
    {
        this.tileEntityFactory = tileEntityFactory;
        return this;
    }

    public ForgeBlockProperties flammable(int flammability, int fireSpreadSpeed)
    {
        this.flammability = flammability;
        this.fireSpreadSpeed = fireSpreadSpeed;
        return this;
    }

    public AbstractBlock.Settings properties()
    {
        return properties;
    }

    boolean hasTileEntity()
    {
        return tileEntityFactory != null;
    }

    @Nullable
    BlockEntity createTileEntity()
    {
        return tileEntityFactory != null ? tileEntityFactory.get() : null;
    }

    int getFlammability()
    {
        return flammability;
    }

    int getFireSpreadSpeed()
    {
        return fireSpreadSpeed;
    }
}
