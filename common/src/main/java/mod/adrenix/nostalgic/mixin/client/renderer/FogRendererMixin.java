package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.client.FogUtil;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import mod.adrenix.nostalgic.util.common.ColorUtil;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import mod.adrenix.nostalgic.util.common.WorldCommonUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
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

    @Shadow
    private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity entity, float partialTick)
    {
        return null;
    }

    /* Helpers */

    /**
     * Change the fog renderer's colors.
     * @param rgba An array of floats from 0.0F to 1.0F.
     */
    private static void setFogRGB(float[] rgba)
    {
        fogRed = rgba[0];
        fogGreen = rgba[1];
        fogBlue = rgba[2];

        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
    }

    /* Injections */

    /**
     * Changes the color of fog while the camera is in water.
     * Controlled by the old water fog tweak.
     */
    @Inject(method = "setupColor", at = @At("RETURN"))
    private static void NT$onWaterFogColor(Camera camera, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo callback)
    {
        if (ModConfig.Candy.oldWaterFogColor() && camera.getFluidInCamera() == FogType.WATER && !FogUtil.isMobEffectActive)
        {
            float respiration = (float) EnchantmentHelper.getRespiration((LivingEntity) camera.getEntity()) * 0.2F;
            int brightness = WorldCommonUtil.getBrightness(level, LightLayer.SKY, camera.getBlockPosition());

            fogRed = FogUtil.Water.getRed(brightness, respiration);
            fogGreen = FogUtil.Water.getGreen(brightness, respiration);
            fogBlue = FogUtil.Water.getBlue(brightness, respiration);

            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
        }
        else if (camera.getFluidInCamera() == FogType.NONE)
        {
            if (FogUtil.isOverworld(camera))
            {
                TweakVersion.FogColor fogColor = ModConfig.Candy.getUniversalFog();

                if (ModConfig.Candy.isTerrainFogCustom())
                    setFogRGB(WorldClientUtil.getCustomInfluencedFog(ColorUtil.toFloatRGBA(ModConfig.Candy.getTerrainFogColor())));
                else if (ModConfig.Candy.oldDynamicFogColor())
                    setFogRGB(WorldClientUtil.getOldInfluencedFog(WorldClientUtil.getFogColorFromBiome()));
                else if (fogColor != TweakVersion.FogColor.DISABLED)
                {
                    switch (fogColor)
                    {
                        case ALPHA_BETA -> setFogRGB(WorldClientUtil.getOldInfluencedFog(ColorUtil.toFloatRGBA("0xC0D8FF")));
                        case CLASSIC -> setFogRGB(WorldClientUtil.getOldInfluencedFog(ColorUtil.toFloatRGBA("0xE1F0FF")));
                        case INF_DEV -> setFogRGB(WorldClientUtil.getOldInfluencedFog(ColorUtil.toFloatRGBA("0xB0D0FF")));
                    }
                }
            }
            else if (FogUtil.isNether(camera))
            {
                if (ModConfig.Candy.isNetherFogCustom())
                    setFogRGB(ColorUtil.toFloatRGBA(ModConfig.Candy.getNetherFogColor()));
                else if (ModConfig.Candy.oldNetherFog())
                    setFogRGB(ColorUtil.toFloatRGBA("0x210505"));
            }
        }
    }

    /**
     * Tracks and changes the level fog color to match the current void/cave fog color.
     * Controlled by the void fog tweak.
     */
    @Inject(method = "levelFogColor", at = @At("TAIL"))
    private static void NT$onLevelFogColor(CallbackInfo callback)
    {
        FogUtil.Void.setFogRGB(fogRed, fogGreen, fogBlue);
        FogUtil.Void.setColor(Minecraft.getInstance().gameRenderer.getMainCamera());
    }

    /**
     * Keeps track as to when the mob effect fog is active. If it is active, then we should not render any old fog.
     * Not controlled by any tweaks since this is a state tracker.
     */
    @Inject(method = "setupFog", at = @At("HEAD"))
    private static void NT$onMobEffectFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean nearFog, float partialTick, CallbackInfo callback)
    {
        FogRenderer.MobEffectFogFunction fogFunction = getPriorityFogFunction(camera.getEntity(), partialTick);

        if (fogFunction != null && camera.getEntity() instanceof LivingEntity entity)
            FogUtil.isMobEffectActive = entity.getEffect(fogFunction.getMobEffect()) != null;
        else
            FogUtil.isMobEffectActive = false;
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
