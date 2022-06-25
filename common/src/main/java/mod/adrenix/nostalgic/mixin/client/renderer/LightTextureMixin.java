package mod.adrenix.nostalgic.mixin.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import mod.adrenix.nostalgic.common.config.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
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

    /* Static Helpers */

    private static int calculateSkylightSubtracted(ClientLevel clientLevel)
    {
        float forceBrightness = clientLevel.dimension() == Level.NETHER ? 7.0F : 15.0F;
        float timeOfDay = clientLevel.getTimeOfDay(1.0F);
        float skyDarken = 1.0F - (Mth.cos(timeOfDay * ((float) Math.PI * 2.0F)) * 2.0F + 0.5F);

        skyDarken = 1.0F - Mth.clamp(skyDarken, 0.0F, 1.0F);
        skyDarken = (float) ((double) skyDarken * (1.0D - (double) (clientLevel.getRainLevel(1.0F) * 5.0F) / 16.0D));
        skyDarken = (float) ((double) skyDarken * (1.0D - (double) (clientLevel.getThunderLevel(1.0F) * 5.0F) / 16.0D));
        skyDarken = 1.0F - skyDarken;

        return (int) (skyDarken * (forceBrightness - 4.0F) + (15.0F - forceBrightness));
    }

    private static float getBrightness(int i)
    {
        float mod = 0.05F;
        float brightness = 1.0F - (float) i / 15.0F;
        return ((1.0F - brightness) / (brightness * 3.0F + 1.0F)) * (1.0F - mod) + mod;
    }

    /**
     * Disables the light flickering from light emitting sources.
     * Controlled by the old light flicker tweak.
     */
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onTick(CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldLightFlicker())
        {
            this.updateLightTexture = true;
            callback.cancel();
        }
    }

    /**
     * Brings back the old lighting colors along with using the old brightness table.
     * Controlled by the old lighting tweak.
     */
    @Inject(method = "updateLightTexture", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onUpdateLightTexture(float partialTicks, CallbackInfo callback)
    {
        if (!MixinConfig.Candy.oldLighting() || !this.updateLightTexture)
            return;

        this.updateLightTexture = false;
        this.minecraft.getProfiler().push("lightTex");

        ClientLevel clientLevel = this.minecraft.level;
        if (clientLevel == null || this.minecraft.player == null)
            return;

        float darkenAmount = this.minecraft.gameRenderer.getDarkenWorldAmount(partialTicks);
        float waterVision = this.minecraft.player.getWaterVision();
        float potionEffect = this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION) ?
            GameRenderer.getNightVisionScale(this.minecraft.player, partialTicks) :
            (waterVision > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER) ? waterVision : 0.0F)
        ;

        boolean isFlashPresent = clientLevel.getSkyFlashTime() > 0 && !this.minecraft.options.hideLightningFlash().get();
        boolean isWorldDarkening = darkenAmount > 0;

        int skylightSubtracted = calculateSkylightSubtracted(clientLevel);

        if (isFlashPresent)
            skylightSubtracted = 1;
        else if (isWorldDarkening)
        {
            skylightSubtracted += (int) Math.ceil(3 * darkenAmount);
            skylightSubtracted = Mth.clamp(skylightSubtracted, 1, 15);
        }

        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                float lightBrightness = getBrightness(j);
                int diffSkylight = i - skylightSubtracted;

                if (diffSkylight < 0)
                    diffSkylight = 0;

                float fromSkylight = getBrightness(diffSkylight);
                if (clientLevel.dimension() == Level.END)
                    fromSkylight = 0.22F + fromSkylight * 0.75F;

                if (potionEffect > 0.0F)
                {
                    float shiftBrightness = potionEffect * 0.7F;
                    float adjustBlockColor = ((1.0F - lightBrightness - 0.5F) * (shiftBrightness * lightBrightness)) + (0.5F * potionEffect);
                    float skyAdjust = ((1.0F - fromSkylight - 0.5F) * (shiftBrightness * fromSkylight)) + (0.5F * potionEffect);

                    lightBrightness += adjustBlockColor;
                    fromSkylight += skyAdjust;
                }

                int lightMultiplier = (int) (lightBrightness * 255.0F);
                int skyMultiplier = (int) (fromSkylight * 255.0F);

                double gamma = this.minecraft.options.gamma().get();
                float r = 1.0F;
                float g = 1.0F;
                float b = 1.0F;

                lightMultiplier = (int) ((float) lightMultiplier * ((float) gamma + 1.0F));
                if (lightMultiplier > 255)
                    lightMultiplier = 255;

                skyMultiplier = (int) ((float) skyMultiplier * ((float) gamma + 1.0F));
                if (skyMultiplier > 255)
                    skyMultiplier = 255;

                if (lightBrightness > fromSkylight)
                    this.lightPixels.setPixelRGBA(j, i, 255 << 24 | (int) ((float) lightMultiplier * r) << 16 | (int) ((float) lightMultiplier * g) << 8 | (int) ((float) lightMultiplier * b));
                else
                    this.lightPixels.setPixelRGBA(j, i, 255 << 24 | (int) ((float) skyMultiplier * r) << 16 | (int) ((float) skyMultiplier * g) << 8 | (int) ((float) skyMultiplier * b));
            }
        }

        this.lightTexture.upload();
        this.minecraft.getProfiler().pop();

        callback.cancel();
    }
}