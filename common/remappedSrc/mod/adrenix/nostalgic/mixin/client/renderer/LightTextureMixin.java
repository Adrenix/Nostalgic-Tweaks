package mod.adrenix.nostalgic.mixin.client.renderer;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public abstract class LightTextureMixin
{
    /* Shadows */

    @Shadow @Final private MinecraftClient minecraft;
    @Shadow @Final private NativeImage lightPixels;
    @Shadow @Final private NativeImageBackedTexture lightTexture;
    @Shadow private boolean updateLightTexture;
    @Shadow protected abstract float getDarknessGamma(float partialTicks);
    @Shadow protected abstract float calculateDarknessScale(LivingEntity livingEntity, float f, float g);

    /* Static Helpers */

    private static float calculateSkylightSubtracted(ClientWorld clientLevel)
    {
        float forceBrightness = clientLevel.getRegistryKey() == World.NETHER ? 7.0F : 15.0F;
        float timeOfDay = clientLevel.getSkyAngle(1.0F);
        float skyDarken = 1.0F - (MathHelper.cos(timeOfDay * ((float) Math.PI * 2.0F)) * 2.0F + 0.5F);

        skyDarken = 1.0F - MathHelper.clamp(skyDarken, 0.0F, 1.0F);
        skyDarken = (float) ((double) skyDarken * (1.0D - (double) (clientLevel.getRainGradient(1.0F) * 5.0F) / 16.0D));
        skyDarken = (float) ((double) skyDarken * (1.0D - (double) (clientLevel.getThunderGradient(1.0F) * 5.0F) / 16.0D));
        skyDarken = 1.0F - skyDarken;

        return skyDarken * (forceBrightness - 4.0F) + (15.0F - forceBrightness);
    }

    private static float getBrightness(int i)
    {
        float mod = ModConfig.Candy.oldLightBrightness() ? 0.05F : 0.0F;
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
        if (ModConfig.Candy.oldLightFlicker())
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
        if (!ModConfig.Candy.oldLighting() || !this.updateLightTexture)
            return;

        this.updateLightTexture = false;
        this.minecraft.getProfiler().push("lightTex");

        ClientWorld clientLevel = this.minecraft.world;
        if (clientLevel == null || this.minecraft.player == null)
            return;

        float darknessScale = this.minecraft.options.darknessEffectScale().get().floatValue();
        float darknessGamma = this.getDarknessGamma(partialTicks) * darknessScale;
        float darknessEffect = this.calculateDarknessScale(this.minecraft.player, darknessGamma, partialTicks) * darknessScale;
        float darkenAmount = this.minecraft.gameRenderer.getSkyDarkness(partialTicks);
        float waterVision = this.minecraft.player.getUnderwaterVisibility();
        float potionEffect = this.minecraft.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ?
            GameRenderer.getNightVisionStrength(this.minecraft.player, partialTicks) :
            (waterVision > 0.0F && this.minecraft.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? waterVision : 0.0F)
        ;

        boolean isFlashPresent = clientLevel.getLightningTicksLeft() > 0 && !this.minecraft.options.hideLightningFlash().get();
        boolean isWorldDarkening = darkenAmount > 0;

        float skylightSubtracted = calculateSkylightSubtracted(clientLevel);

        if (isFlashPresent)
            skylightSubtracted = 1;
        else if (isWorldDarkening)
        {
            skylightSubtracted += Math.ceil(3 * darkenAmount);
            skylightSubtracted = MathHelper.clamp(skylightSubtracted, 1.0F, 15.0F);
        }

        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                float lightBrightness = getBrightness(j);
                float diffSkylight = i - skylightSubtracted;

                if (diffSkylight < 0)
                    diffSkylight = 0;

                float fromSkylight = getBrightness((int) diffSkylight);
                if (clientLevel.getRegistryKey() == World.END)
                    fromSkylight = 0.22F + fromSkylight * 0.75F;

                if (potionEffect > 0.0F)
                {
                    float shiftBrightness = potionEffect * 0.7F;
                    float adjustBlockColor = ((1.0F - lightBrightness - 0.5F) * (shiftBrightness * lightBrightness)) + (0.5F * potionEffect);
                    float skyAdjust = ((1.0F - fromSkylight - 0.5F) * (shiftBrightness * fromSkylight)) + (0.5F * potionEffect);

                    lightBrightness += adjustBlockColor;
                    fromSkylight += skyAdjust;
                }

                if (darknessEffect > 0.0F)
                {
                    lightBrightness -= darknessEffect;
                    fromSkylight -= darknessEffect;

                    lightBrightness = MathHelper.clamp(lightBrightness, 0.0F, 1.0F);
                    fromSkylight = MathHelper.clamp(fromSkylight, 0.0F, 1.0F);
                }

                float lightMultiplier = lightBrightness * 255.0F;
                float skyMultiplier = fromSkylight * 255.0F;

                double gamma = ModConfig.Candy.disableGamma() ? 0.0D : this.minecraft.options.gamma().get();

                lightMultiplier = lightMultiplier * ((float) gamma + 0.3F + 1.0F);
                if (lightMultiplier > 255.0F)
                    lightMultiplier = 255.0F;

                skyMultiplier = skyMultiplier * ((float) gamma + 1.0F);
                if (skyMultiplier > 255.0F)
                    skyMultiplier = 255.0F;

                if (lightBrightness > fromSkylight)
                    this.lightPixels.setColor(j, i, 255 << 24 | (int) (lightMultiplier) << 16 | (int) (lightMultiplier) << 8 | (int) (lightMultiplier));
                else
                    this.lightPixels.setColor(j, i, 255 << 24 | (int) (skyMultiplier) << 16 | (int) (skyMultiplier) << 8 | (int) (skyMultiplier));
            }
        }

        this.lightTexture.upload();
        this.minecraft.getProfiler().pop();

        callback.cancel();
    }
}