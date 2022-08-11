package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.ModClientUtil;
import mod.adrenix.nostalgic.util.common.ModCommonUtil;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BackgroundRenderer.class, priority = ModCommonUtil.APPLY_FIRST)
public abstract class FogRendererMixin
{
    /**
     * Keeps track as to when the mob effect fog is active. If it is active, then we should not render any old fog.
     * Not controlled by any tweaks since this is a state tracker.
     */
    @Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer$MobEffectFogFunction;setupFog(Lnet/minecraft/client/renderer/FogRenderer$FogData;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/effect/MobEffectInstance;FF)V"))
    private static void NT$onMobEffectFog(Camera camera, BackgroundRenderer.FogType fogMode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo callback)
    {
        ModClientUtil.Fog.isMobEffectActive = true;
    }

    /**
     * Disables the sunrise/sunset colors from influencing the fog color.
     * Controlled by the old fog tweak.
     */
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"))
    private static float[] NT$onGetFogColor(DimensionEffects instance, float timeOfDay, float partialTicks)
    {
        if (ModConfig.Candy.oldSunriseSunsetFog())
            return null;
        return instance.getFogColorOverride(timeOfDay, partialTicks);
    }

    /**
     * Rotates where the custom sunrise/sunset fog color is.
     * This is controlled by the old sunrise/sunset tweak.
     */
    @ModifyArg(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Vector3f;dot(Lcom/mojang/math/Vector3f;)F"))
    private static Vec3f NT$onRotateFogColor(Vec3f vanilla)
    {
        return ModConfig.Candy.oldSunriseAtNorth() ? new Vec3f(0.0F, 0.0F, -vanilla.getX()) : vanilla;
    }
}
