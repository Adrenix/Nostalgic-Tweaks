package mod.adrenix.nostalgic.forge.mixin.rubidium;

import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin
{
    /* Shadows */

    @Shadow @Final private QuadLightData quadLightData;

    /* Injections */

    /**
     * Enforces that the liquid rendering does not use ambient occlusion. This simulates old water rendering. Controlled
     * by old water lighting.
     */
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;useAmbientOcclusion()Z"
        )
    )
    private boolean NT$onUseAmbientOcclusion()
    {
        return !ModConfig.Candy.oldWaterLighting();
    }

    /**
     * Recalculates the lightmap on water block quads to simulate old water rendering. Controlled by old water
     * lighting.
     */
    @Inject(
        remap = false,
        method = "calculateQuadColors",
        at = @At("RETURN")
    )
    private void NT$onCalculateQuadColors(ModelQuadView quad, BlockAndTintGetter level, BlockPos pos, LightPipeline lighter, Direction direction, float brightness, ColorSampler<FluidState> colorSampler, FluidState fluidState, CallbackInfo callback)
    {
        if (!ModConfig.Candy.oldWaterLighting())
            return;

        int light = WorldClientUtil.getWaterLight(level, pos);

        for (int i = 0; i < 4; i++)
            this.quadLightData.lm[i] = light;
    }
}
