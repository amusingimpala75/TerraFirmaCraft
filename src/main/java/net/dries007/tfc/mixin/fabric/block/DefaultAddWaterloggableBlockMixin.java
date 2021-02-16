package net.dries007.tfc.mixin.fabric.block;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class DefaultAddWaterloggableBlockMixin {

    /*private static final BooleanProperty WATERLOGGED = BooleanProperty.of("waterlogged");

    @Shadow
    @Final
    protected StateManager<Block, BlockState> stateManager;

    @Shadow
    protected abstract void setDefaultState(BlockState state);

    @Shadow
    public abstract BlockState getDefaultState();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;appendProperties(Lnet/minecraft/state/StateManager$Builder;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void addWaterloggedPropertiesIfNotAlready(AbstractBlock.Settings settings, CallbackInfo ci, StateManager.Builder<Block, BlockState> builder) {
        if (!((StateMangerAccessor) builder).accessor$getNamedProperties().containsKey("waterlogged") && this instanceof Waterloggable) {
            builder.add(WATERLOGGED);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void inject$setDefault(AbstractBlock.Settings settings, CallbackInfo ci)
    {
        if (this instanceof Waterloggable)
        {
            this.setDefaultState(getDefaultState().with(WATERLOGGED, false));
        }
    }*/
}
