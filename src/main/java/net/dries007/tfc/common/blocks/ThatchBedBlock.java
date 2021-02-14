/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.dries007.tfc.forgereplacements.block.IForgeBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class ThatchBedBlock extends BedBlock implements IForgeBlock
{
    private static final VoxelShape BED_SHAPE = Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 9.0F, 16.0F);

    public ThatchBedBlock(AbstractBlock.Settings properties)
    {
        super(DyeColor.YELLOW, properties);
    }

    @Override
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit)
    {
        if (!worldIn.isClient)
        {
            if (isOverworld(worldIn))
            {
                if (!worldIn.isThundering())
                {
                    player.sendMessage(new TranslatableText("tfc.thatch_bed.use"), true);
                }
                else
                {
                    player.sendMessage(new TranslatableText("tfc.thatch_bed.thundering"), true);
                }
                return ActionResult.SUCCESS;
            }
            else
            {
                worldIn.createExplosion(null, DamageSource.badRespawnPoint(), null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 7.0F, true, Explosion.DestructionType.DESTROY);
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return BED_SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView worldIn)
    {
        return null; // Need to override as the super class is a ITileEntityProvider
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        Direction facing = state.get(FACING);
        //if (!(world.getBlockState(pos.offset(facing)).isOf(TFCBlocks.THATCH_BED)) || world.getBlockState(pos.down()).isAir(world, pos))
        if (!(world.getBlockState(pos.offset(facing)).isOf(TFCBlocks.THATCH_BED)) || world.getBlockState(pos.down()).isAir())
        {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return false; // Need to override as the super class is a ITileEntityProvider
    }

    @Override
    public boolean isBed(BlockState state, BlockView world, BlockPos pos, @Nullable Entity player)
    {
        return true;
    }
}
