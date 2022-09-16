package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.WorldServerUtil}.
 */

public abstract class WorldClientUtil
{
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
}
