package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.WorldClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin
{
    /* Shadows */

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private NativeImage lightPixels;
    @Shadow @Final private DynamicTexture lightTexture;
    @Shadow private boolean updateLightTexture;
    @Shadow protected abstract float getDarknessGamma(float partialTicks);
    @Shadow protected abstract float calculateDarknessScale(LivingEntity entity, float gamma, float partialTicks);

    /* Injections */

    /**
     * Disables the light flickering from light emitting sources.
     * Controlled by the old light flicker tweak.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void NT$onTick(CallbackInfo callback)
    {
        if (ModConfig.Candy.oldLightFlicker())
        {
            this.updateLightTexture = true;
            callback.cancel();
        }
    }

    /**
     * Simulates the old lighting engine by bringing back old light colors and abrupt skylight transitioning.
     * Controlled by the old light rendering tweak.
     */
    @Inject(method = "updateLightTexture", at = @At("HEAD"), cancellable = true)
    private void NT$onUpdateLightTexture(float partialTicks, CallbackInfo callback)
    {
        if (!ModConfig.Candy.oldLightColor() || !this.updateLightTexture)
            return;

        this.updateLightTexture = false;
        this.minecraft.getProfiler().push("lightTex");

        ClientLevel level = this.minecraft.level;
        if (level == null || this.minecraft.player == null)
            return;

        double gammaSetting = this.minecraft.options.gamma().get();
        float darknessScale = this.minecraft.options.darknessEffectScale().get().floatValue();
        float darknessGamma = this.getDarknessGamma(partialTicks) * darknessScale;
        float darknessEffect = this.calculateDarknessScale(this.minecraft.player, darknessGamma, partialTicks) * darknessScale;
        float darkenAmount = this.minecraft.gameRenderer.getDarkenWorldAmount(partialTicks);
        float waterVision = this.minecraft.player.getWaterVision();
        float potionEffect = this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION) ?
            GameRenderer.getNightVisionScale(this.minecraft.player, partialTicks) :
            (waterVision > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER) ? waterVision : 0.0F)
        ;

        boolean isGammaDisabled = ModConfig.Candy.disableBrightness();
        boolean isFlashPresent = level.getSkyFlashTime() > 0 && !this.minecraft.options.hideLightningFlash().get();
        boolean isWorldDarkening = darkenAmount > 0;

        float skyLightSubtracted = WorldClientUtil.getSkylightSubtracted(level);

        if (isFlashPresent)
            skyLightSubtracted = 1;
        else if (isWorldDarkening)
        {
            skyLightSubtracted += Math.ceil(3 * darkenAmount);
            skyLightSubtracted = Mth.clamp(skyLightSubtracted, 1.0F, 15.0F);
        }

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                float fromBlockLight = WorldClientUtil.getOldBrightness(x);
                float fromSkyLight = WorldClientUtil.getOldBrightness((int) Math.max(y - skyLightSubtracted, 0));

                if (level.dimension() == Level.END)
                    fromSkyLight = 0.22F + fromSkyLight * 0.75F;

                if (potionEffect > 0.0F)
                {
                    float shiftBrightness = potionEffect * 0.7F;
                    float adjustBlockColor = ((1.0F - fromBlockLight - 0.5F) * (shiftBrightness * fromBlockLight)) + (0.5F * potionEffect);
                    float skyAdjust = ((1.0F - fromSkyLight - 0.5F) * (shiftBrightness * fromSkyLight)) + (0.5F * potionEffect);

                    fromBlockLight += adjustBlockColor;
                    fromSkyLight += skyAdjust;
                }

                if (darknessEffect > 0.0F)
                {
                    fromBlockLight -= darknessEffect;
                    fromSkyLight -= darknessEffect;

                    fromBlockLight = Mth.clamp(fromBlockLight, 0.0F, 1.0F);
                    fromSkyLight = Mth.clamp(fromSkyLight, 0.0F, 1.0F);
                }

                double gamma = isGammaDisabled ? 0.0D : gammaSetting;
                float blockLight = Mth.clamp(fromBlockLight * 255.0F * ((float) gamma + 1.0F), 0.0F, 255.0F);
                float skyLight = Mth.clamp(fromSkyLight * 255.0F * ((float) gamma + 1.0F), 0.0F, 255.0F);
                float light = fromBlockLight > fromSkyLight ? blockLight : skyLight;

                this.lightPixels.setPixelRGBA(x, y, 255 << 24 | (int) light << 16 | (int) light << 8 | (int) light);
            }
        }

        this.lightTexture.upload();
        this.minecraft.getProfiler().pop();

        callback.cancel();
    }
}