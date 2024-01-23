package mod.adrenix.nostalgic.mixin.tweak.candy.ambient_occlusion_blocks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.mixin.util.ChestUtil;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin
{
    /**
     * Tricks the shade calculator into thinking that a block is full-sized so that ambient occlusion can be properly
     * applied.
     */
    @ModifyExpressionValue(
        method = "getShadeBrightness",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;isCollisionShapeFullBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private boolean NT$setCustomAmbientOcclusion(boolean isCollisionShapeFullBlock, BlockState state)
    {
        if (CandyTweak.AMBIENT_OCCLUSION_BLOCKS.get().containsBlock(state.getBlock()) || ChestUtil.isOld(state))
            return true;

        return isCollisionShapeFullBlock;
    }
}
