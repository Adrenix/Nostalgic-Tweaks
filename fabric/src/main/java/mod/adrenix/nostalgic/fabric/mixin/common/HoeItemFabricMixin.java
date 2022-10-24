package mod.adrenix.nostalgic.fabric.mixin.common;

import mod.adrenix.nostalgic.util.common.WorldCommonUtil;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HoeItem.class)
public abstract class HoeItemFabricMixin
{
    /**
     * Adds the chance for wheat seeds to appear when tilling grass blocks with a hoe item.
     * Controlled by the seeds from grass tweak.
     */
    @Inject
    (
        method = "method_36984",
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
        )
    )
    private static void NT$onChangeIntoState(BlockState state, UseOnContext context, CallbackInfo callback)
    {
        WorldCommonUtil.onTillGrass(state, context);
    }
}
