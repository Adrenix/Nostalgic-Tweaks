package mod.adrenix.nostalgic.forge.mixin.rubidium;

import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin
{
    /* Unique */

    @Shadow @Final private QuadLightData cachedQuadLightData;

    /* Injections */

    /**
     * Changes the color on the quad if the renderer is processing a torch-like block. Controlled by old torch
     * brightness.
     */
    @Inject(
        remap = false,
        method = "renderQuadList",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;writeGeometry(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lme/jellysquid/mods/sodium/client/render/vertex/type/ChunkVertexBufferBuilder;Lme/jellysquid/mods/sodium/client/model/IndexBufferBuilder;Lnet/minecraft/world/phys/Vec3;Lme/jellysquid/mods/sodium/client/model/quad/ModelQuadView;[I[F[I)V"
        )
    )
    private void NT$beforeGeometryWriting(BlockRenderContext context, LightPipeline lighter, Vec3 offset, ChunkModelBuilder builder, List<BakedQuad> quads, Direction cullFace, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldTorchBrightness() && context.state().getBlock() instanceof TorchBlock)
            Arrays.fill(this.cachedQuadLightData.lm, LightTexture.FULL_BRIGHT);
    }
}
