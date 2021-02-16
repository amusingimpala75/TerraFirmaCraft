/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;

import net.dries007.tfc.forgereplacements.item.ItemUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroundcoverBlock extends Block implements IFluidLoggable
{
    public static final FluidProperty FLUID = FluidBlockStateProprties.WATER;
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public static final VoxelShape FLAT = createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    public static final VoxelShape SMALL = createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
    public static final VoxelShape MEDIUM = createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 4.0D, 11.0D);
    public static final VoxelShape PIXEL_HIGH = createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    public static final VoxelShape TWIG = createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);

    public static GroundcoverBlock twig(Settings properties)
    {
        return new GroundcoverBlock(properties, TWIG, null);
    }

    public static GroundcoverBlock looseOre(Settings properties)
    {
        return new GroundcoverBlock(properties, SMALL, null);
    }

    private final VoxelShape shape;
    @Nullable
    private final Supplier<? extends Item> pickBlock;

    public GroundcoverBlock(GroundcoverBlockType cover)
    {
        this(Settings.of(Material.SOLID_ORGANIC).strength(0.05F, 0.0F).sounds(BlockSoundGroup.NETHER_WART).nonOpaque(), cover.getShape(), cover.getVanillaItem());
    }

    public GroundcoverBlock(Settings properties, VoxelShape shape, @Nullable Supplier<? extends Item> pickBlock)
    {
        super(properties);

        this.shape = shape;
        this.pickBlock = pickBlock;

        setDefaultState(getStateManager().getDefaultState().with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)).with(FACING, Direction.EAST));
    }

    @NotNull
    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        final FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());

        BlockState state = getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
        if (getFluidProperty().canContain(fluidState.getFluid()))
        {
            return state.with(getFluidProperty(), getFluidProperty().keyFor(fluidState.getFluid()));
        }
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, getFluidProperty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canPlaceAt(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            final Fluid containedFluid = stateIn.get(getFluidProperty()).getFluid();
            if (containedFluid != Fluids.EMPTY)
            {
                worldIn.getFluidTickScheduler().schedule(currentPos, containedFluid, containedFluid.getTickRate(worldIn));
            }
            return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit)
    {
        worldIn.breakBlock(pos, false);
        if (!player.isCreative() && worldIn instanceof ServerWorld)
        {
            BlockEntity tileEntity = state.getBlock().hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
            getDroppedStacks(state, (ServerWorld) worldIn, pos, tileEntity, null, ItemStack.EMPTY).forEach(stackToSpawn -> ItemUtils.giveItemToPlayer(player, stackToSpawn, -1));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isSideSolidFullSquare(worldIn, pos, Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        return pickBlock != null ? new ItemStack(pickBlock.get()) : super.getPickStack(world, pos, state);
    }
}
