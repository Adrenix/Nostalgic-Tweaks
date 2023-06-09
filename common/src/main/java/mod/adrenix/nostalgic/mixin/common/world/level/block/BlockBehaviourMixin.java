package mod.adrenix.nostalgic.mixin.common.world.level.block;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.BlockClientUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockBehaviour.class)
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
    private boolean NT$onGetShadeFromShape(BlockState instance, BlockGetter blockGetter, BlockPos blockPos)
    {
        if (BlockClientUtil.isFullShape(instance.getBlock()))
            return true;
        return instance.isCollisionShapeFullBlock(blockGetter, blockPos);
    }
}
