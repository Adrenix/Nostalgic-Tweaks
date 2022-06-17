package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.MixinUtil;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FogRenderer.class, priority = MixinUtil.APPLY_FIRST)
public abstract class FogRendererMixin
{
    /**
     * Disables the sunrise/sunset colors from influencing the fog color.
     * Controlled by the old fog tweak.
     */
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"))
    private static float[] NT$onGetFogColor(DimensionSpecialEffects instance, float timeOfDay, float partialTicks)
    {
        if (MixinConfig.Candy.oldSunriseSunsetFog())
            return null;
        return instance.getSunriseColor(timeOfDay, partialTicks);
    }

    /**
     * Rotates where the custom sunrise/sunset fog color is.
     * This is controlled by the old sunrise/sunset tweak.
     */
    @ModifyArg(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Vector3f;dot(Lcom/mojang/math/Vector3f;)F"))
    private static Vector3f NT$onRotateFogColor(Vector3f vanilla)
    {
        return MixinConfig.Candy.oldSunriseAtNorth() ? new Vector3f(0.0F, 0.0F, -vanilla.x()) : vanilla;
    }
}
