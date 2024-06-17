package mod.adrenix.nostalgic.fabric.mixin.sodium.candy.torch_block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.BakedQuadView;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadOrientation;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.Material;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import mod.adrenix.nostalgic.mixin.util.candy.TorchMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin
{
    /**
     * Removes the bottom face quads for a wall torch model.
     */
    @ModifyReturnValue(
        method = "getGeometry",
        at = @At("RETURN")
    )
    private List<BakedQuad> nt_sodium_torch_block$hideBottom(List<BakedQuad> quads, BlockRenderContext context)
    {
        if (CandyTweak.OLD_TORCH_BOTTOM.get() && TorchMixinHelper.isSheared(context.state()))
        {
            if (CollectionUtil.isModifiable(quads))
                quads.removeIf(quad -> quad.getDirection() == Direction.DOWN);
        }

        return quads;
    }

    /**
     * Changes the torch model used when retrieving quads.
     */
    @ModifyExpressionValue(
        method = "getGeometry",
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;model()Lnet/minecraft/client/resources/model/BakedModel;"
        )
    )
    private BakedModel nt_sodium_torch_block$modifyTorchModel(BakedModel original, BlockRenderContext context)
    {
        if (TorchMixinHelper.isSheared(context.state()))
            return TorchMixinHelper.getModel(context.state());

        return original;
    }

    /**
     * Changes the quad vertices data of wall torch blocks.
     */
    @Inject(
        method = "writeGeometry",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/vertex/builder/ChunkMeshBufferBuilder;push([Lme/jellysquid/mods/sodium/client/render/chunk/vertex/format/ChunkVertexEncoder$Vertex;Lme/jellysquid/mods/sodium/client/render/chunk/terrain/material/Material;)V"
        )
    )
    private void nt_sodium_torch_block$rewriteVertexGeometry(BlockRenderContext context, ChunkModelBuilder builder, Vec3 offset, Material material, BakedQuadView quad, int[] colors, QuadLightData light, CallbackInfo callback, @Local ModelQuadOrientation orientation, @Local ChunkVertexEncoder.Vertex[] vertices)
    {
        if (TorchMixinHelper.isNotLikeTorch(context.state()))
            return;

        PoseStack poseStack = new PoseStack();
        boolean isSheared = TorchMixinHelper.isSheared(context.state());
        boolean isBright = TorchMixinHelper.isBright(context.state());

        if (isSheared)
        {
            poseStack.translate(context.origin().x(), context.origin().y(), context.origin().z());
            TorchMixinHelper.applyShear(poseStack, context.state());
        }

        for (int i = 0; i < vertices.length; i++)
        {
            ChunkVertexEncoder.Vertex vertex = vertices[i];

            if (isSheared)
            {
                int srcIndex = orientation.getVertexIndex(i);
                float x = quad.getX(srcIndex);
                float y = quad.getY(srcIndex);
                float z = quad.getZ(srcIndex);

                Vector4f shear = poseStack.last().pose().transform(new Vector4f(x, y, z, 1.0F));

                vertex.x = shear.x();
                vertex.y = shear.y();
                vertex.z = shear.z();
            }

            vertex.light = isBright ? LightTexture.FULL_BRIGHT : vertex.light;
        }
    }
}
