package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.BlockCommonUtil;
import mod.adrenix.nostalgic.util.common.MathUtil;
import mod.adrenix.nostalgic.util.common.WorldCommonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.WorldServerUtil}.
 */

public abstract class WorldClientUtil
{
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
     * Creates the correct blue void color based on the level's current environment.
     */
    public static void setBlueVoidColor()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null)
            return;

        float weatherModifier;
        float partialTicks = minecraft.getDeltaFrameTime();
        float timeOfDay = level.getTimeOfDay(partialTicks);
        float boundedTime = Mth.clamp(Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);

        float r = boundedTime;
        float g = boundedTime;
        float b = boundedTime;

        float rainLevel = level.getRainLevel(partialTicks);
        float thunderLevel = level.getThunderLevel(partialTicks);

        if (rainLevel > 0.0F)
        {
            thunderLevel = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.6F;
            weatherModifier = 1.0F - rainLevel * 0.75F;

            r = r * weatherModifier + thunderLevel * (1.0F - weatherModifier);
            g = g * weatherModifier + thunderLevel * (1.0F - weatherModifier);
            b = b * weatherModifier + thunderLevel * (1.0F - weatherModifier);
        }

        if (thunderLevel > 0.0F)
        {
            float thunderModifier = 1.0F - thunderLevel * 0.75F;
            weatherModifier = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;

            r = r * thunderModifier + weatherModifier * (1.0F - thunderModifier);
            g = g * thunderModifier + weatherModifier * (1.0F - thunderModifier);
            b = b * thunderModifier + weatherModifier * (1.0F - thunderModifier);
        }

        r = Mth.clamp(r, 0.1F, 1.0F) * 0.13F;
        g = Mth.clamp(g, 0.1F, 1.0F) * 0.17F;
        b = Mth.clamp(b, 0.1F, 1.0F) * 0.7F;

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
        int skyLight = level.getBrightness(LightLayer.SKY, blockPos);
        int blockLight = level.getBrightness(LightLayer.BLOCK, blockPos);
        boolean isWaterLight = ModConfig.Candy.oldWaterLighting() && BlockCommonUtil.isInWater(level, blockPos);

        // Water rendering in Sodium is handled differently - so don't modify skylight in water if Sodium is installed
        if (isWaterLight && !NostalgicTweaks.isSodiumInstalled)
            skyLight = BlockCommonUtil.getWaterLightBlock(skyLight);

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

        if (lastBlockLight == -1 || lastBlockLight != skyLight)
        {
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
