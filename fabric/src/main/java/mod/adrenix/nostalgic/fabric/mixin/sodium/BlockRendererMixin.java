package mod.adrenix.nostalgic.fabric.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.BakedQuadView;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TorchBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin
{
    /* Shadow */

    @Shadow @Final private QuadLightData quadLightData;

    /* Injections */

    /**
     * Changes the color on the quad if the renderer is processing a torch-like block. Controlled by old torch
     * brightness.
     */
    @Inject(
        remap = false,
        method = "getVertexLight",
        at = @At("HEAD"),
        cancellable = true
    )
    private void NT$onGetVertexLight(BlockRenderContext context, LightPipeline lighter, Direction cullFace, BakedQuadView quad, CallbackInfoReturnable<QuadLightData> callback)
    {
        if (ModConfig.Candy.oldTorchBrightness() && context.state().getBlock() instanceof TorchBlock)
        {
            Arrays.fill(this.quadLightData.lm, LightTexture.FULL_BRIGHT);
            Arrays.fill(this.quadLightData.br, 1.0F);

            callback.setReturnValue(this.quadLightData);
        }
    }
}
