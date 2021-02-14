/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;

public class TFCSaplingBlock extends SaplingBlock
{
    public TFCSaplingBlock(SaplingGenerator treeIn, Settings properties)
    {
        super(treeIn, properties);
    }
}