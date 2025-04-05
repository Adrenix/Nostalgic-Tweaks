package mod.adrenix.nostalgic.mixin.tweak.candy.world_sky;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.helper.candy.level.SkyHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /* Injections */

    /**
     * Draws the old blue void color onto the sky. This occurs after the sky buffer has drawn and before the dark buffer
     * is drawn.
     */
    @Inject(
        method = "method_62215",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"
        )
    )
    private void nt_world_sky$onDrawSkyBuffer(CallbackInfo callback)
    {
        if (CandyTweak.OLD_BLUE_VOID.get() == Generic.MODERN)
            return;

        SkyHelper.setBlueColor();
        SkyHelper.BLUE_VOID_BUFFER.ifPresent(buffer ->
            buffer.drawWithRenderType(RenderType.sky()));
    }

    /**
     * Disables the change in sky color when the sun is rising or setting.
     */
    @ModifyExpressionValue(
        method = "method_62215",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseOrSunsetColor(F)I"
        )
    )
    private int nt_world_sky$setSunriseColor(int color)
    {
        return CandyTweak.RENDER_SUNRISE_SUNSET_COLOR.get() ? color : 0;
    }
}
