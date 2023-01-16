package mod.adrenix.nostalgic.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.client.BlockClientUtil;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherFabricMixin
{
    /* Shadows */

    @Shadow public abstract BakedModel getBlockModel(BlockState state);

    /* Injections */

    /**
     * Changes the rendering of vanilla torches.
     * Controlled by various old torch tweaks.
     */
    @Inject(method = "renderBatched", at = @At("HEAD"), cancellable = true)
    private void NT$onRenderBatched(BlockState state, BlockPos position, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, boolean isAo, Random random, CallbackInfoReturnable<Boolean> callback)
    {
        if (NostalgicTweaks.OPTIFINE.get() && BlockClientUtil.isTorchModel(state))
        {
            BlockClientUtil.oldTorch(poseStack, consumer, this.getBlockModel(state), state, position, random);
            callback.setReturnValue(true);
        }
    }
}
