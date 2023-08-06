package mod.adrenix.nostalgic.fabric.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
     * by the old water lighting tweak.
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
        if (ModConfig.Candy.oldWaterLighting())
            return false;

        return Minecraft.useAmbientOcclusion();
    }

    /**
     * Recalculates the lightmap on water block quads to simulate old water rendering. Controlled by the old water
     * lighting tweak.
     */
    @Inject(
        method = "updateQuad",
        at = @At("RETURN")
    )
    private void NT$onCalculateQuadColors(ModelQuadView quad, WorldSlice world, BlockPos pos, LightPipeline lighter, Direction dir, float brightness, ColorProvider<FluidState> color, FluidState fluidState, CallbackInfo callback)
    {
        if (!ModConfig.Candy.oldWaterLighting())
            return;

        int light = WorldClientUtil.getWaterLight(world, pos);

        for (int i = 0; i < 4; i++)
            this.quadLightData.lm[i] = light;
    }
}
