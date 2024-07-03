package mod.adrenix.nostalgic.neoforge.mixin.embeddium.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import mod.adrenix.nostalgic.mixin.util.candy.lighting.LightingMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin
{
    /* Shadows */

    @Shadow @Final private QuadLightData quadLightData;

    /* Injections */

    /**
     * Helps simulate old water lighting by disabling Sodium's ambient occlusion on water.
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;useAmbientOcclusion()Z"
        )
    )
    private boolean nt_embeddium_world_lighting$modifyWaterAmbientOcclusion(boolean useAmbientOcclusion)
    {
        return CandyTweak.SODIUM_WATER_AO.get() && useAmbientOcclusion;
    }

    /**
     * Recalculates the quad's light data on water blocks to simulate old water rendering.
     */
    @Inject(
        method = "updateQuad",
        at = @At("RETURN")
    )
    private void nt_embeddium_world_lighting$modifyWaterLight(ModelQuadView quad, WorldSlice world, BlockPos blockPos, LightPipeline lighter, Direction direction, float brightness, ColorProvider<FluidState> colorProvider, FluidState fluidState, CallbackInfo callback)
    {
        if (!CandyTweak.OLD_WATER_LIGHTING.get())
            return;

        int light = LightingMixinHelper.getWaterLight(world, blockPos);

        for (int i = 0; i < 4; i++)
            this.quadLightData.lm[i] = light;
    }
}
