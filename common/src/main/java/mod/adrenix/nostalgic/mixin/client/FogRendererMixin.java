package mod.adrenix.nostalgic.mixin.client;

import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.client.config.MixinConfig;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin
{
    /**
     * Disables the sunrise/sunset colors from influencing the fog color.
     * Controlled by the old fog toggle.
     */
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"))
    private static float[] onGetFogColor(DimensionSpecialEffects instance, float timeOfDay, float partialTicks)
    {
        if (MixinConfig.Candy.oldSunriseSunsetFog())
            return null;
        return instance.getSunriseColor(timeOfDay, partialTicks);
    }

    /**
     * Rotates where the custom sunrise/sunset fog color is.
     * This is controlled by the old sunrise/sunset toggle.
     */
    @ModifyArg(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Vector3f;dot(Lcom/mojang/math/Vector3f;)F"))
    private static Vector3f onRotateFogColor(Vector3f vanilla)
    {
        return MixinConfig.Candy.oldSunriseAtNorth() ? new Vector3f(0.0F, 0.0F, -vanilla.x()) : vanilla;
    }
}
