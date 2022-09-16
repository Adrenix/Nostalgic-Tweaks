package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.FogUtil;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FogRenderer.class, priority = MixinPriority.APPLY_FIRST)
public abstract class FogRendererMixin
{
    /* Shadows */

    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;

    /* Injections */

    /**
     * Changes the color of fog while the camera is in water.
     * Controlled by the old water fog tweak.
     */
    @Inject(method = "setupColor", at = @At("RETURN"))
    private static void NT$onWaterFogColor(Camera camera, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldWaterFogColor() && camera.getFluidInCamera() == FogType.WATER)
        {
            float respiration = (float) EnchantmentHelper.getRespiration((LivingEntity) camera.getEntity()) * 0.2F;
            int brightness = level.getBrightness(LightLayer.SKY, camera.getBlockPosition());

            fogRed = FogUtil.Water.getRed(brightness, respiration);
            fogGreen = FogUtil.Water.getGreen(brightness, respiration);
            fogBlue = FogUtil.Water.getBlue(brightness, respiration);
        }
    }

    /**
     * Tracks and changes the level fog color to match the current void/cave fog color.
     * Controlled by the void fog tweak.
     */
    @Inject(method = "levelFogColor", at = @At("TAIL"))
    private static void NT$onLevelFogColor(CallbackInfo callback)
    {
        FogUtil.VoidFog.setFogRGB(fogRed, fogGreen, fogBlue);

        if (FogUtil.VoidFog.isRendering())
            FogUtil.VoidFog.setColor(Minecraft.getInstance().gameRenderer.getMainCamera());
    }

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
     * Changes the fog start, end, and shape when the camera is within water.
     * Controlled by the old water fog tweak.
     */
    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void NT$onSetupWaterFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldWaterFogDensity() && camera.getFluidInCamera() == FogType.WATER)
            FogUtil.Water.setupFog(farPlaneDistance);
    }

    /**
     * Disables the sunrise/sunset colors from influencing the fog color.
     * Controlled by the old fog tweak.
     */
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"))
    private static float[] NT$onGetFogColor(DimensionSpecialEffects instance, float timeOfDay, float partialTicks)
    {
        return ModConfig.Candy.oldSunriseSunsetFog() ? null : instance.getSunriseColor(timeOfDay, partialTicks);
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
