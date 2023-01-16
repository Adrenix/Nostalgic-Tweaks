package mod.adrenix.nostalgic.forge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.util.client.BlockClientUtil;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherForgeMixin
{
    /* Shadows */

    @Shadow public abstract BakedModel getBlockModel(BlockState state);

    /* Injections */

    /**
     * Changes the rendering of vanilla torches.
     * Controlled by various old torch tweaks.
     */
    @Inject
    (
        at = @At("HEAD"),
        remap = false,
        cancellable = true,
        method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Z"
    )
    private void NT$onRenderBatched(BlockState state, BlockPos position, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean isAo, Random random, IModelData model, CallbackInfoReturnable<Boolean> callback)
    {
        if (BlockClientUtil.isTorchModel(state))
        {
            BlockClientUtil.oldTorch(poseStack, consumer, this.getBlockModel(state), state, position, random);
            callback.setReturnValue(true);
        }
    }
}
