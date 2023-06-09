package mod.adrenix.nostalgic.forge.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.util.client.BlockClientUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"
    )
    private void NT$onRenderBatched(BlockState state, BlockPos position, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean isAo, RandomSource randomSource, ModelData model, RenderType renderType, CallbackInfo callback)
    {
        if (BlockClientUtil.isTorchModel(state))
        {
            BlockClientUtil.oldTorch(poseStack, consumer, this.getBlockModel(state), state, position, randomSource);
            callback.cancel();
        }
    }
}
