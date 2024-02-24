package mod.adrenix.nostalgic.fabric.mixin.tweak.candy.torch_block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.adrenix.nostalgic.mixin.util.candy.TorchMixinHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ChunkRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin extends AbstractBlockRenderContext
{
    /* Shadows */

    @Shadow @Final private ChunkRenderInfo chunkInfo;

    /* Injections */

    /**
     * Changes the quad vertices data of torch blocks.
     */
    @WrapOperation(
        method = "tessellateBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/BakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V"
        )
    )
    private void nt_fabric_torch_block$wrapEmitBlockQuads(BakedModel model, BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, Supplier<RandomSource> randomSource, RenderContext renderContext, Operation<Void> emitBlockQuads, BlockState arg1, BlockPos arg2, final BakedModel arg3, PoseStack poseStack)
    {
        if (TorchMixinHelper.isLikeTorch(blockState))
        {
            RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(blockState);
            VertexConsumer vertexConsumer = this.chunkInfo.getInitializedBuffer(renderType);
            TorchMixinHelper.writeVertices(poseStack, blockAndTintGetter, vertexConsumer, model, blockState, blockPos, randomSource.get());
        }
        else
            emitBlockQuads.call(model, blockAndTintGetter, blockState, blockPos, randomSource, renderContext);
    }
}
