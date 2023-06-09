package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix4f;

import java.awt.*;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.WorldServerUtil}.
 */

public abstract class WorldClientUtil
{
    /* Temperature */

    /**
     * Initialized as non-number so that the biome the player spawns in initializes the temperature value. This value is
     * cached so that temperature smoothly moves between biomes and doesn't drastically change causing color flashes.
     */
    private static float currentTemperature = Float.NaN;

    /**
     * Initialized as non-number so that the dark fog is immediately available when entering an overworld dimension.
     * Caching this value is important so that moving between light levels don't cause flashing fog colors.
     */
    private static float currentDarknessShift = Float.NaN;

    /**
     * Gets the biome temperature the player is in.
     * @return A smoothly transitioned biome temperature value.
     */
    public static float getBiomeTemperature()
    {
        LocalPlayer player = Minecraft.getInstance().player;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        if (player == null)
            return 0.0F;

        float temp = player.level().getBiome(camera.getBlockPosition()).value().getBaseTemperature();

        if (Float.isNaN(currentTemperature))
            currentTemperature = temp;
        else
            currentTemperature = MathUtil.moveTowards(currentTemperature, temp, 0.00001F);

        return currentTemperature;
    }

    /**
     * Resets the temperature cache. This should be used when the player is leaving a world or changing dimensions.
     */
    public static void resetWorldInterpolationCache()
    {
        currentTemperature = Float.NaN;
        currentDarknessShift = Float.NaN;
    }

    /**
     * Get a sky color from the current biome.
     * @return A color integer for the sky.
     */
    public static int getSkyColorFromBiome()
    {
        float temp = getBiomeTemperature() / 2.0F;

        return Color.getHSBColor(0.653F - temp * 0.05F, 0.415F + temp * 0.1F, 1.0F).getRGB();
    }

    /**
     * Get a fog color from the current biome and render distance.
     * @return A float array (RGBA) that will be used to change the fog colors.
     */
    public static float[] getFogColorFromBiome()
    {
        float temp = getBiomeTemperature() / 2.0F;
        float saturation = 0.26F;
        int renderDistance = Minecraft.getInstance().options.getEffectiveRenderDistance();

        if (renderDistance <= 8)
        {
            saturation = switch (renderDistance)
            {
                case 6, 7, 8 -> 0.235F;
                case 4, 5 -> 0.22F;
                case 2, 3 -> 0.18F;
                default -> 0.26F;
            };
        }

        return Color.getHSBColor(0.648F - temp * 0.05F, saturation + temp * 0.1F, 1.0F).getRGBComponents(new float[4]);
    }

    /* Sky Helpers */

    /**
     * Determines where the sun/moon should be rotated when rendering it.
     * @param vanilla The vanilla rotation of the sun/moon.
     * @return The new value to use when rotating the sun/moon.
     */
    public static float getSunriseRotation(float vanilla)
    {
        return ModConfig.Candy.oldSunriseAtNorth() ? 0.0F : vanilla;
    }

    /**
     * Builds a sky disc for the far plane.
     * @param builder The current buffer builder.
     * @param y The y-level of the sky disc.
     * @return The finished rendered buffer.
     */
    public static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder builder, float y)
    {
        float x = Math.signum(y) * 512.0F;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        builder.vertex(0.0, y, 0.0).endVertex();

        for (int i = -180; i <= 180; i += 45)
            builder.vertex(x * Mth.cos((float) i * ((float) Math.PI / 180)), y, 512.0F * Mth.sin((float) i * ((float) Math.PI / 180))).endVertex();

        return builder.end();
    }

    /**
     * Caches the blue model view matrix so the sky can be overlaid with the blue void correctly.
     */
    public static Matrix4f blueModelView = new Matrix4f();

    /**
     * Caches the blue projection matrix so the sky can be overlaid with the blue void correctly.
     */
    public static Matrix4f blueProjection = new Matrix4f();

    /**
     * Get an RGB float array that indicates how much a color channel should be influenced by the time of day or weather
     * patterns. None of the values returned are clamped. The values returned are influenced based on a custom color.
     *
     * @return An array of 3 floats (RGB).
     */
    public static float[] getStdEnvironmentInfluence()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null)
            return new float[] { 0.0F, 0.0F, 0.0F };

        float partialTicks = minecraft.getDeltaFrameTime();
        float timeOfDay = level.getTimeOfDay(partialTicks);
        float boundedTime = Mth.clamp(Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);

        float rain = 1.0F - level.getRainLevel(partialTicks) + 0.4F;
        float thunder = 1.0F - level.getThunderLevel(partialTicks) + 0.2F;
        float weather = Mth.clamp(Math.min(rain, thunder), 0.0F, 1.0F);

        float r = boundedTime * weather;
        float g = boundedTime * weather;
        float b = boundedTime * weather;

        return new float[] { r, g, b };
    }

    /**
     * Changes the color of the blue void based on environmental factors such as the time of the day and current level
     * weather patterns.
     *
     * @return An array of 3 floats (RGB).
     */
    public static float[] getBlueEnvironmentInfluence()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null)
            return new float[] { 0.0F, 0.0F, 0.0F };

        float partialTicks = minecraft.getDeltaFrameTime();
        float timeOfDay = level.getTimeOfDay(partialTicks);
        float boundedTime = Mth.clamp(Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);

        float voidRed = (float) (0.30F * ((double) (boundedTime * 0.05F + 0.95F)));
        float voidGreen = (float) (0.24F * ((double) (boundedTime * 0.05F + 0.95F)));
        float voidBlue = (float) (0.85F * ((double) (boundedTime * 0.85F + 0.15F)));

        float rainLevel = level.getRainLevel(partialTicks);
        float thunderLevel = level.getThunderLevel(partialTicks);

        if (rainLevel > 0.0F)
        {
            float redGreenShift = 1.0F - rainLevel * 0.5F;
            float blueShift = 1.0F - rainLevel * 0.4F;

            voidRed *= redGreenShift;
            voidGreen *= redGreenShift;
            voidBlue *= blueShift;
        }

        if (thunderLevel > 0.0F)
        {
            float shift = 1.0F - thunderLevel * 0.5F;

            voidRed *= shift;
            voidGreen *= shift;
            voidBlue *= shift;
        }

        return new float[] { voidRed, voidGreen, voidBlue };
    }

    /**
     * Creates the correct blue void color based on the level's current environment.
     */
    public static void setBlueVoidColor()
    {
        boolean isCustom = ModConfig.Candy.isVoidSkyCustom();

        final float[] ENV_RGB = isCustom ? getStdEnvironmentInfluence() : getBlueEnvironmentInfluence();

        float r = ENV_RGB[0];
        float g = ENV_RGB[1];
        float b = ENV_RGB[2];

        final float OLD_RED = 0.13F;
        final float OLD_GREEN = 0.17F;
        final float OLD_BLUE = 0.7F;
        final float[] CUSTOM_RGB = ColorUtil.toFloatRGBA(ModConfig.Candy.getVoidSkyColor());

        r = Mth.clamp(r, 0.08F, 1.0F) * (isCustom ? CUSTOM_RGB[0] : OLD_RED);
        g = Mth.clamp(g, 0.08F, 1.0F) * (isCustom ? CUSTOM_RGB[1] : OLD_GREEN);
        b = Mth.clamp(b, 0.08F, 1.0F) * (isCustom ? CUSTOM_RGB[2] : OLD_BLUE);

        FogUtil.Void.setVoidRGB(r, g, b);

        if (FogUtil.Void.isRendering())
        {
            final float[] VOID_RGB = FogUtil.Void.getVoidRGB();
            r = VOID_RGB[0];
            g = VOID_RGB[1];
            b = VOID_RGB[2];
        }

        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, 1.0F);
    }

    /**
     * Changes the fog color brightness depending on max light brightness and render distance.
     * No changes will be applied of the old dark fog tweak is disabled.
     *
     * @param FOG_COLOR An array of at least three floats (RGB) to modify.
     */
    private static void calculateLightInfluence(final float[] FOG_COLOR)
    {
        Minecraft minecraft = Minecraft.getInstance();

        int renderDistance = minecraft.options.getEffectiveRenderDistance();
        int syncedLight = getSyncedLight(minecraft.level, minecraft.gameRenderer.getMainCamera().getBlockPosition());

        float brightness = getOldBrightness(syncedLight);
        float distanceShift = renderDistance / (12.0F + (renderDistance / 2.0F));
        float darknessShift = brightness * (1.0F - distanceShift) + distanceShift;

        boolean isIgnored = syncedLight == 15 || renderDistance > 8;

        if (!ModConfig.Candy.oldDarkFog() || ModConfig.Candy.oldClassicLight() || isIgnored)
            darknessShift = 1.0F;

        if (Float.isNaN(currentDarknessShift))
            currentDarknessShift = darknessShift;
        else
            currentDarknessShift = MathUtil.moveTowards(currentDarknessShift, darknessShift, 0.0009F);

        FOG_COLOR[0] *= currentDarknessShift;
        FOG_COLOR[1] *= currentDarknessShift;
        FOG_COLOR[2] *= currentDarknessShift;
    }

    /**
     * Get an RGB float array that indicates how much a color channel should be influenced by the sky color and weather
     * patterns. The values returned are influenced based on regular vanilla colors.
     *
     * @return An array of 3 floats (RGB).
     */
    public static float[] getOldInfluencedFog(final float[] FOG_COLOR)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null)
            return new float[] { 0.0F, 0.0F, 0.0F };

        if (ModConfig.Candy.getWorldFog() == TweakVersion.WorldFog.CLASSIC)
        {
            int renderDistance = minecraft.options.getEffectiveRenderDistance();

            if (renderDistance <= 3)
            {
                FOG_COLOR[0] = 1.0F;
                FOG_COLOR[1] = 1.0F;
                FOG_COLOR[2] = 1.0F;
            }
            else if (renderDistance == 4)
            {
                FOG_COLOR[0] = 239.0F / 255.0F;
                FOG_COLOR[1] = 247.0F / 255.0F;
                FOG_COLOR[2] = 1.0F;
            }
            else if (renderDistance <= 8)
            {
                FOG_COLOR[0] = 230.0F / 255.0F;
                FOG_COLOR[1] = 243.0F / 255.0F;
                FOG_COLOR[2] = 1.0F;
            }
        }

        calculateLightInfluence(FOG_COLOR);

        float partialTicks = minecraft.getDeltaFrameTime();
        float timeOfDay = level.getTimeOfDay(partialTicks);
        float boundedTime = Mth.clamp(Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);

        float fogRed = (float) (FOG_COLOR[0] * ((double) (boundedTime * 0.96F + 0.04F)));
        float fogGreen = (float) (FOG_COLOR[1] * ((double) (boundedTime * 0.96F + 0.04F)));
        float fogBlue = (float) (FOG_COLOR[2] * ((double) (boundedTime * 0.947F + 0.063F)));

        float rainLevel = level.getRainLevel(partialTicks);
        float thunderLevel = level.getThunderLevel(partialTicks);

        if (rainLevel > 0.0F)
        {
            float redGreenShift = 1.0F - rainLevel * 0.5F;
            float blueShift = 1.0F - rainLevel * 0.4F;

            fogRed *= redGreenShift;
            fogGreen *= redGreenShift;
            fogBlue *= blueShift;
        }

        if (thunderLevel > 0.0F)
        {
            float shift = 1.0F - thunderLevel * 0.5F;

            fogRed *= shift;
            fogGreen *= shift;
            fogBlue *= shift;
        }


        return new float[] { fogRed, fogGreen, fogBlue };
    }

    /**
     * Get an array of RGB floats that is influenced by environmental world factors such as the time of day and current
     * weather patterns.
     *
     * @param CUSTOM_FOG An array of 3 floats (RGB) from a custom fog color.
     * @return An array of 3 floats (RGB) that is influenced by the world environment.
     */
    public static float[] getCustomInfluencedFog(final float[] CUSTOM_FOG)
    {
        final float[] ENV_RGB = getStdEnvironmentInfluence();

        calculateLightInfluence(ENV_RGB);

        float r = ENV_RGB[0];
        float g = ENV_RGB[1];
        float b = ENV_RGB[2];

        r = Mth.clamp(r, 0.04F, 1.0F) * CUSTOM_FOG[0];
        g = Mth.clamp(g, 0.04F, 1.0F) * CUSTOM_FOG[1];
        b = Mth.clamp(b, 0.04F, 1.0F) * CUSTOM_FOG[2];

        return new float[] { r, g, b };
    }

    /* World Lighting Helpers */

    /**
     * Needed so the renderer knows when chunks should be updated.
     */
    private static int lastBlockLight = -1;

    /**
     * Flag that tracks the state of chunk relighting.
     */
    private static boolean enqueueRelightChecks = false;

    /**
     * Sets relight checks back to their default state.
     * This should be done when exiting a world.
     */
    public static void resetLightingCache()
    {
        lastBlockLight = -1;
        enqueueRelightChecks = false;
    }

    /**
     * @return The current state of the {@link WorldClientUtil#enqueueRelightChecks} flag.
     */
    public static boolean isRelightCheckEnqueued() { return enqueueRelightChecks; }

    /**
     * Sets the {@link WorldClientUtil#enqueueRelightChecks} flag to <code>false</code>.
     */
    public static void setRelightFinished() { enqueueRelightChecks = false; }

    /**
     * Syncs sky/block light values to simulate old light rendering.
     * @param level A block and tint getter instance.
     * @param blockPos A block position.
     * @return Synced sky/block light integer.
     */
    public static int getSyncedLight(BlockAndTintGetter level, BlockPos blockPos)
    {
        if (ModConfig.Candy.oldClassicLight())
            return WorldClientUtil.getClassicLight(blockPos);

        int skyLight = WorldCommonUtil.getBrightness(level, LightLayer.SKY, blockPos);
        int blockLight = WorldCommonUtil.getBrightness(level, LightLayer.BLOCK, blockPos);
        boolean isOldWater = ModConfig.Candy.oldWaterLighting();
        boolean isWaterLight = isOldWater && BlockCommonUtil.isInWater(level, blockPos);

        // Water rendering in Sodium is handled differently - so don't modify skylight in water if Sodium is installed
        if (isWaterLight && !ModTracker.SODIUM.isInstalled())
            skyLight = BlockCommonUtil.getWaterLightBlock(level, blockPos);

        return WorldClientUtil.getMaxLight(skyLight, blockLight);
    }

    /**
     * Get a skylight subtraction value that is influenced by current weather patterns.
     * @param level The client level.
     * @return A skylight subtraction value calculated by level weather.
     */
    public static int getWeatherInfluence(ClientLevel level)
    {
        float partialTick = Minecraft.getInstance().getDeltaFrameTime();
        float rain = level.getRainLevel(partialTick);
        float thunder = level.getThunderLevel(partialTick);

        int rainDiff = 0;
        int thunderDiff = 0;

        if (rain >= 0.3F) rainDiff = 1;
        if (rain >= 0.6F) rainDiff = 2;
        if (rain >= 0.9F) rainDiff = 3;
        if (thunder >= 0.8F) thunderDiff = 5;

        return Math.max(rainDiff, thunderDiff);
    }

    /**
     * Gets the amount of skylight to apply to the light texture.
     * @param level The client level.
     * @return A float to
     */
    public static float getSkylightSubtracted(ClientLevel level)
    {
        if (ModConfig.Candy.oldLightRendering())
            return (float) 0;

        float forceBrightness = level.dimension() == Level.NETHER ? 7.0F : 15.0F;
        float skyDarken = 1.0F - (Mth.cos(level.getTimeOfDay(1.0F) * ((float) Math.PI * 2.0F)) * 2.0F + 0.5F);

        skyDarken = 1.0F - Mth.clamp(skyDarken, 0.0F, 1.0F);
        skyDarken = (float) ((double) skyDarken * (1.0D - (double) (level.getRainLevel(1.0F) * 5.0F) / 16.0D));
        skyDarken = (float) ((double) skyDarken * (1.0D - (double) (level.getThunderLevel(1.0F) * 5.0F) / 16.0D));
        skyDarken = 1.0F - skyDarken;

        return skyDarken * (forceBrightness - 4.0F) + (15.0F - forceBrightness);
    }

    /**
     * Gets a brightness value using the old light brightness table values.
     * @param i An index from 0-15.
     * @return An old brightness value based on the given lightmap index.
     */
    public static float getOldBrightness(int i)
    {
        float light = 1.0F - (float) i / 15.0F;
        return ((1.0F - light) / (light * 3.0F + 1.0F)) * (1.0F - 0.05F) + 0.05F;
    }

    /**
     * The lighting engine in Classic was simple, with only two light levels, bright and dark. Light passes through
     * transparent blocks to light blocks underneath. Blocks that do not receive light are in a dim shadow that remains
     * at the same level of brightness no matter how far they are from a light source.
     *
     * @param blockPos The block pos to determine light data at.
     * @return Either 15 or 0.
     */
    public static int getClassicLight(BlockPos blockPos)
    {
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || !level.dimensionType().hasSkyLight())
            return 0;

        int skyLight = WorldCommonUtil.getBrightness(level, LightLayer.SKY, blockPos);

        if (skyLight == 15)
            return 15;
        else if (skyLight == 0)
            return 0;

        BlockPos abovePos = blockPos.above();

        while (abovePos.getY() < level.getMaxBuildHeight())
        {
            if (level.getBlockState(abovePos).getLightBlock(level, abovePos) == 15)
                return 0;

            if (WorldCommonUtil.getBrightness(level, LightLayer.SKY, abovePos) == 15)
                return 15;

            abovePos = abovePos.above();
        }

        return 15;
    }

    /**
     * There are situations where the world relighting does not complete successfully. This timer will add a couple of
     * extra relight cycles after the last relight enqueue. The extra relight check will update the world renderer chunk
     * cache every 8 seconds. This extra relight will execute 2 times until the timer is reset by a world relight.
     */
    private static final TimeWatcher RELIGHT_TIMER = new TimeWatcher(8000L, 2);

    /**
     * Gets a new light value for blocks being rendered by the level.
     * @param currentSkyLight The current vanilla stored skylight.
     * @param currentBlockLight The current vanilla stored block light.
     * @return The new skylight value.
     */
    public static int getMaxLight(int currentSkyLight, int currentBlockLight)
    {
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || currentSkyLight <= 0)
            return Math.max(currentSkyLight, currentBlockLight);

        int levelMaxLight = level.getMaxLightLevel();
        int weatherDiff = getWeatherInfluence(level);
        int minLight = currentSkyLight >= levelMaxLight ? 4 : 0;
        int maxLight = Math.max(currentBlockLight, ModConfig.Candy.getMaxBlockLight());
        int skyLight = WorldCommonUtil.getDayLight(level) - weatherDiff;
        int skyDiff = 15 - currentSkyLight;

        boolean isBlockLightChanged = lastBlockLight == -1 || lastBlockLight != skyLight;
        boolean isExtraRelight = !isBlockLightChanged && !enqueueRelightChecks && RELIGHT_TIMER.isReady();

        if (isBlockLightChanged || isExtraRelight)
        {
            if (!isExtraRelight)
                RELIGHT_TIMER.reset();

            lastBlockLight = skyLight;
            enqueueRelightChecks = true;
        }

        if (currentSkyLight != levelMaxLight && skyLight <= 4)
            skyLight += weatherDiff;

        return Mth.clamp(Math.max(skyLight - skyDiff, currentBlockLight), minLight, maxLight);
    }

    /**
     * Gets the greatest light value surrounding a water block.
     * @param level A level instance with the a {@link BlockAndTintGetter} interface.
     * @param source The position of the water block.
     * @return The largest light value around the water block.
     */
    public static int getWaterLight(BlockAndTintGetter level, BlockPos source)
    {
        int center = LevelRenderer.getLightColor(level, source);
        int above = LevelRenderer.getLightColor(level, source.above());
        int below = LevelRenderer.getLightColor(level, source.below());
        int north = LevelRenderer.getLightColor(level, source.north());
        int south = LevelRenderer.getLightColor(level, source.south());
        int west = LevelRenderer.getLightColor(level, source.west());
        int east = LevelRenderer.getLightColor(level, source.east());

        return MathUtil.getLargest(center, above, below, north, south, west, east);
    }
}
