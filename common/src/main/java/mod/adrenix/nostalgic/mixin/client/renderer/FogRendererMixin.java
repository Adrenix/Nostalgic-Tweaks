package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.FogUtil;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FogRenderer.class, priority = MixinPriority.APPLY_FIRST)
public abstract class FogRendererMixin
{
    /**
     * Keeps track as to when the mob effect fog is active. If it is active, then we should not render any old fog.
     * Not controlled by any tweaks since this is a state tracker.
     */
    @Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer$MobEffectFogFunction;setupFog(Lnet/minecraft/client/renderer/FogRenderer$FogData;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/effect/MobEffectInstance;FF)V"))
    private static void NT$onMobEffectFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo callback)
    {
        FogUtil.isMobEffectActive = true;
    }

    /**
     * Disables the sunrise/sunset colors from influencing the fog color.
     * Controlled by the old fog tweak.
     */
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"))
    private static float[] NT$onGetFogColor(DimensionSpecialEffects instance, float timeOfDay, float partialTicks)
    {
        if (ModConfig.Candy.oldSunriseSunsetFog())
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
        return ModConfig.Candy.oldSunriseAtNorth() ? new Vector3f(0.0F, 0.0F, -vanilla.x()) : vanilla;
    }
}
