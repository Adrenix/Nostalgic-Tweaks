package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.common.BlockCommonUtil;
import mod.adrenix.nostalgic.util.common.ModUtil;
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
        float partialTick = minecraft.getDeltaFrameTime();
        float timeOfDay = level.getTimeOfDay(partialTick);
        float boundedTime = Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F;
        boundedTime = Mth.clamp(boundedTime, 0.0F, 1.0F);

        float r = boundedTime;
        float g = boundedTime;
        float b = boundedTime;

        float rainLevel = level.getRainLevel(partialTick);
        float thunderLevel = level.getThunderLevel(partialTick);

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

        if (ModConfig.Candy.oldWaterLighting() && BlockCommonUtil.isInWater(level, blockPos))
            skyLight = BlockCommonUtil.getWaterLightBlock(skyLight);

        return WorldClientUtil.getMaxLight(skyLight, blockLight);
    }

    /**
     * Gets the light emitted by the sky based on dimension properties and weather.
     * @param level The client level.
     * @return The light emitted by the sky.
     */
    public static int calculateSkylight(ClientLevel level)
    {
        float partialTick = Minecraft.getInstance().getDeltaFrameTime();
        int timeOfDay = (int) (level.getDayTime() % 24000L);

        float rain = level.getRainLevel(partialTick);
        float thunder = level.getThunderLevel(partialTick);

        int rainDiff = 0;
        int thunderDiff = 0;

        if (rain >= 0.3F) rainDiff = 1;
        if (rain >= 0.6F) rainDiff = 2;
        if (rain >= 0.9F) rainDiff = 3;
        if (thunder >= 0.8F) thunderDiff = 5;

        int skyLight = 15;

        if (ModUtil.Numbers.isInRange(timeOfDay, 13670, 22330))
            skyLight = 4;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 22331, 22491) || ModUtil.Numbers.isInRange(timeOfDay, 13509, 13669))
            skyLight = 5;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 22492, 22652) || ModUtil.Numbers.isInRange(timeOfDay, 13348, 13508))
            skyLight = 6;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 22653, 22812) || ModUtil.Numbers.isInRange(timeOfDay, 13188, 13347))
            skyLight = 7;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 22813, 22973) || ModUtil.Numbers.isInRange(timeOfDay, 13027, 13187))
            skyLight = 8;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 22974, 23134) || ModUtil.Numbers.isInRange(timeOfDay, 12867, 13026))
            skyLight = 9;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 23135, 23296) || ModUtil.Numbers.isInRange(timeOfDay, 12705, 12866))
            skyLight = 10;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 23297, 23459) || ModUtil.Numbers.isInRange(timeOfDay, 12542, 12704))
            skyLight = 11;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 23460, 23623) || ModUtil.Numbers.isInRange(timeOfDay, 12377, 12541))
            skyLight = 12;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 23624, 23790) || ModUtil.Numbers.isInRange(timeOfDay, 12210, 12376))
            skyLight = 13;
        else if (ModUtil.Numbers.isInRange(timeOfDay, 23791, 23960) || ModUtil.Numbers.isInRange(timeOfDay, 12041, 12209))
            skyLight = 14;

        return skyLight - Math.max(rainDiff, thunderDiff);
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
        int maxLight = Math.max(currentSkyLight, currentBlockLight);

        if (level == null)
            return maxLight;

        boolean isSkyVisible = currentSkyLight > 0;

        if (!isSkyVisible)
            return maxLight;

        int maxShader = Math.max(currentBlockLight, ModConfig.Candy.getMaxBlockLight());
        int minShader = currentSkyLight >= level.getMaxLightLevel() ? 4 : 0;
        int skyLight = calculateSkylight(level);
        int skyDiff = 15 - currentSkyLight;

        if (lastBlockLight == -1 || lastBlockLight != skyLight)
        {
            lastBlockLight = skyLight;
            enqueueRelightChecks = true;
        }

        return Mth.clamp(Math.max(skyLight - skyDiff, currentBlockLight), minShader, maxShader);
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

        return ModUtil.Numbers.getLargest(center, above, below, north, south, west, east);
    }
}
