package mod.adrenix.nostalgic.fabric.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.util.client.BlockClientUtil;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ChunkRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin
{
    /* Shadows */

    @Shadow @Final private ChunkRenderInfo chunkInfo;
    @Shadow @Final private BlockRenderInfo blockInfo;

    /* Injections */

    /**
     * Changes the rendering of vanilla torches.
     * Controlled by various old torch tweaks.
     */
    @Inject
    (
        method = "tessellateBlock",
        cancellable = true,
        at = @At
        (
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V"
        )
    )
    private void NT$onTessellateBlock(BlockState blockState, BlockPos blockPos, BakedModel model, PoseStack poseStack, CallbackInfoReturnable<Boolean> callback)
    {
        if (BlockClientUtil.isTorchModel(blockState))
        {
            RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(blockState);
            VertexConsumer consumer = this.chunkInfo.getInitializedBuffer(renderType);

            BlockClientUtil.oldTorch(poseStack, consumer, model, blockState, blockPos, this.blockInfo.randomSupplier.get());

            callback.setReturnValue(false);
        }
    }
}
