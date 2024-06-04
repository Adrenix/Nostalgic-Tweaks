package mod.adrenix.nostalgic.mixin.util.candy.lighting;

import com.mojang.blaze3d.platform.NativeImage;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

/**
 * This utility class is used only by the client.
 */
public abstract class LightmapMixinHelper
{
    /**
     * To prevent visual issues when joining a world for the first time, the lightmap cache needs populated before it is
     * used for smoothing out lighting transitions.
     */
    private static final FlagHolder CACHE_INITIALIZER = FlagHolder.off();

    /**
     * This two-dimensional array stores the lightmap cache so that it can be used for linear interpolation.
     */
    private static final float[][] LIGHTMAP_CACHE = new float[16][16];

    /**
     * Resets the cache initializer back to its default state. This should be done when the player exits the world since
     * the last known lightmap cache will be invalid if the next world joined is different.
     */
    public static void resetLightingCache()
    {
        CACHE_INITIALIZER.disable();
    }

    /**
     * Modifies the given light pixels image so that it uses classic lighting.
     *
     * @param lightPixels The {@link NativeImage} to modify.
     */
    public static void setClassicTexture(NativeImage lightPixels)
    {
        for (int skyLight = 0; skyLight < 16; skyLight++)
        {
            for (int blockLight = 0; blockLight < 16; blockLight++)
            {
                if (skyLight >= 14 || blockLight >= 14)
                    lightPixels.setPixelRGBA(blockLight, skyLight, 0xFFFFFFFF);
                else
                    lightPixels.setPixelRGBA(blockLight, skyLight, 0xFF999999);
            }
        }
    }

    /**
     * Gets a brightness value for the lightmap texture.
     *
     * @param i          An index from 0-15.
     * @param isSkyLight Whether brightness is being calculated for skylight.
     * @return An old brightness value based on the given lightmap index.
     */
    public static float getLightmapBrightness(int i, boolean isSkyLight)
    {
        float light = 1.0F - (float) i / 15.0F;
        float brightness;

        if (CandyTweak.ROUND_ROBIN_RELIGHT.get())
        {
            if (i <= 4 && !isSkyLight)
                brightness = 3.0F;
            else
            {
                if (isSkyLight)
                {
                    if (i >= 14)
                        brightness = 0.0F;
                    else
                        brightness = 2.6F;
                }
                else
                {
                    if (i < 14)
                    {
                        brightness = switch (i)
                        {
                            case 13 -> 2.7F;
                            case 12 -> 3.0F;
                            default -> 3.4F;
                        };
                    }
                    else
                        brightness = 1.8F;
                }
            }
        }
        else
        {
            if (i <= 4 && isSkyLight)
                brightness = 2.6F;
            else
            {
                if (isSkyLight)
                {
                    if (i >= 14)
                        brightness = 0.0F;
                    else
                        brightness = 2.6F;
                }
                else
                {
                    brightness = switch (i)
                    {
                        case 15 -> 0.6F;
                        case 14 -> 1.2F;
                        case 13 -> 1.8F;
                        case 12, 11 -> 2.4F;
                        case 9, 5 -> 2.5F;
                        case 4 -> 2.9F;
                        default -> 2.6F;
                    };
                }
            }
        }

        return ((1.0F - light) / (light * brightness + 1.0F)) * (1.0F - 0.05F) + 0.05F;
    }

    /**
     * Gets the amount of skylight to apply to the light texture.
     *
     * @param level The {@link ClientLevel} instance.
     * @return The amount of skylight to subtract based on level context.
     */
    public static int getSkylightSubtracted(ClientLevel level)
    {
        if (CandyTweak.ROUND_ROBIN_RELIGHT.get())
            return 0;

        boolean isNether = level.dimension() == Level.NETHER && !CandyTweak.OLD_NETHER_LIGHTING.get();
        float forceBrightness = isNether ? 7.0F : 15.0F;
        float skyDarken = 1.0F - (Mth.cos(level.getTimeOfDay(1.0F) * ((float) Math.PI * 2.0F)) * 2.0F + 0.5F);

        skyDarken = 1.0F - Mth.clamp(skyDarken, 0.0F, 1.0F);
        skyDarken = (float) ((double) skyDarken * (1.0D - (double) (level.getRainLevel(1.0F) * 5.0F) / 16.0D));
        skyDarken = (float) ((double) skyDarken * (1.0D - (double) (level.getThunderLevel(1.0F) * 5.0F) / 16.0D));
        skyDarken = 1.0F - skyDarken;

        return (int) (skyDarken * (forceBrightness - 4.0F) + (15.0F - forceBrightness));
    }

    /**
     * Modifies the given light pixels image so that it uses the old block light grayscale.
     *
     * @param lightPixels    The {@link NativeImage} to modify.
     * @param darknessEffect The darkness effect provided by the light texture.
     * @param partialTick    The normalized progress between two ticks [0.0F, 1.0F].
     */
    public static void setGrayscaleTexture(NativeImage lightPixels, float darknessEffect, float partialTick)
    {
        ClientLevel level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;

        if (level == null || player == null)
            return;

        Minecraft minecraft = Minecraft.getInstance();
        double gammaSetting = minecraft.options.gamma().get();
        float darkenAmount = minecraft.gameRenderer.getDarkenWorldAmount(partialTick);
        float waterVision = player.getWaterVision();
        float potionEffect = player.hasEffect(MobEffects.NIGHT_VISION) ? GameRenderer.getNightVisionScale(player, partialTick) : (waterVision > 0.0F && player.hasEffect(MobEffects.CONDUIT_POWER) ? waterVision : 0.0F);

        boolean isCacheInitialized = CACHE_INITIALIZER.get();
        boolean isSmoothTransition = CandyTweak.SMOOTH_LIGHT_TRANSITION.get() && !CandyTweak.ROUND_ROBIN_RELIGHT.get();
        boolean isGammaDisabled = CandyTweak.DISABLE_BRIGHTNESS.get();
        boolean isFlashPresent = level.getSkyFlashTime() > 0 && !minecraft.options.hideLightningFlash().get();
        boolean isWorldDarkening = darkenAmount > 0;

        int skyLightSubtracted = getSkylightSubtracted(level);

        if (isFlashPresent)
            skyLightSubtracted = 1;
        else if (isWorldDarkening)
            skyLightSubtracted = Mth.clamp(skyLightSubtracted + (int) Math.ceil(3 * darkenAmount), 1, 15);

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                float fromBlockAmbient = LightTexture.getBrightness(level.dimensionType(), x);
                float fromSkyAmbient = LightTexture.getBrightness(level.dimensionType(), y);

                float fromBlockLight = Math.max(getLightmapBrightness(x, false), fromBlockAmbient);
                float fromSkyLight = Math.max(getLightmapBrightness(Math.max(y - skyLightSubtracted, 0), true), fromSkyAmbient);

                if (level.dimension() == Level.END)
                    fromSkyLight = 0.22F + fromSkyLight * 0.75F;

                if (level.dimension() == Level.NETHER)
                    fromSkyLight = 0.05F + fromSkyLight * 0.75F;

                if (potionEffect > 0.0F)
                {
                    float shiftBrightness = potionEffect * 0.7F;
                    float adjustBlockColor = ((1.0F - fromBlockLight - 0.5F) * (shiftBrightness * fromBlockLight)) + (0.5F * potionEffect);
                    float skyAdjust = ((1.0F - fromSkyLight - 0.5F) * (shiftBrightness * fromSkyLight)) + (0.5F * potionEffect);

                    fromBlockLight += adjustBlockColor;
                    fromSkyLight += skyAdjust;
                }

                if (darknessEffect > 0.0F && x != 15)
                {
                    fromBlockLight -= darknessEffect;
                    fromSkyLight -= darknessEffect;

                    fromBlockLight = Mth.clamp(fromBlockLight, 0.025F, 1.0F);
                    fromSkyLight = Mth.clamp(fromSkyLight, 0.025F, 1.0F);
                }

                double gamma = isGammaDisabled ? 0.0D : gammaSetting;
                float skyLight = Mth.clamp(fromSkyLight * 255.0F * ((float) gamma + 1.0F), 6.375F, 255.0F);
                float blockLight = Mth.clamp(fromBlockLight * 255.0F * ((float) gamma + 1.0F), 6.375F, 255.0F);
                float rgba = fromBlockLight > fromSkyLight ? blockLight : skyLight;

                if (isSmoothTransition && isCacheInitialized)
                    rgba = Mth.lerp(partialTick, LIGHTMAP_CACHE[x][y], rgba);

                LIGHTMAP_CACHE[x][y] = rgba;

                int light = Math.round(rgba);
                lightPixels.setPixelRGBA(x, y, 255 << 24 | light << 16 | light << 8 | light);
            }
        }

        if (!isCacheInitialized)
            CACHE_INITIALIZER.enable();
    }
}
