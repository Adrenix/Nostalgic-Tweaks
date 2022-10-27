package mod.adrenix.nostalgic.fabric.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.IndexBufferBuilder;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexSink;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin
{
    /* Unique Fields */

    @Unique boolean NT$isRenderingTorch = false;

    /* Injections */

    /**
     * Checks if the quads on vanilla torch models should be full bright.
     * Controlled by the old torch brightness tweak.
     */
    @Inject(method = "renderQuad", at = @At("HEAD"))
    private void NT$onStartRenderQuad
    (
        BlockAndTintGetter level,
        BlockState state,
        BlockPos pos,
        BlockPos origin,
        ModelVertexSink vertices,
        IndexBufferBuilder indices,
        Vec3 blockOffset,
        ColorSampler<BlockState> colorSampler,
        BakedQuad bakedQuad,
        QuadLightData light,
        ChunkModelBuilder model,
        CallbackInfo callback
    )
    {
        this.NT$isRenderingTorch = ModConfig.Candy.oldTorchBrightness() && (state.is(Blocks.WALL_TORCH) || state.is(Blocks.TORCH));
    }

    /**
     * Changes the color on the quad if the renderer is processing a vanilla torch.
     * Controlled by the old torch brightness tweak.
     */
    @ModifyArg(method = "renderQuad", index = 7, at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/format/ModelVertexSink;writeVertex(Lnet/minecraft/core/Vec3i;FFFIFFII)V"))
    private int NT$onRenderQuadColor(int color)
    {
        return NT$isRenderingTorch ? LightTexture.FULL_BRIGHT : color;
    }
}
