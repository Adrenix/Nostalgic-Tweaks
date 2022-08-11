package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractBlock.class)
public abstract class BlockBehaviourMixin
{
    /**
     * Changes the darkness of the shade that forms on the corner of blocks.
     * Controlled by the old smooth lighting tweak.
     */
    @ModifyConstant(method = "getShadeBrightness", constant = @Constant(floatValue = 0.2F))
    private float NT$onGetShadeBrightness(float vanilla)
    {
        return ModConfig.Candy.oldSmoothLighting() ? 0.0F : 0.2F;
    }

    /**
     * Tricks the shade calculator thinking that a block is full-sized.
     * This is needed so ambient occlusion can be applied correctly.
     *
     * Controlled by various tweaks.
     */
    @Redirect
    (
        method = "getShadeBrightness",
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;isCollisionShapeFullBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private boolean NT$onGetShadeFromShape(BlockState instance, BlockView blockGetter, BlockPos blockPos)
    {
        if (ModClientUtil.Block.isBlockFullShape(instance.getBlock()))
            return true;
        return instance.isFullCube(blockGetter, blockPos);
    }
}
